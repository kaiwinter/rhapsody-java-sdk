package com.github.kaiwinter.rhapsody.api;

import java.util.Base64;
import java.util.Collection;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaiwinter.rhapsody.cache.DataCache;
import com.github.kaiwinter.rhapsody.model.AccessTokenResponse;
import com.github.kaiwinter.rhapsody.model.AccountData;
import com.github.kaiwinter.rhapsody.model.AlbumData;
import com.github.kaiwinter.rhapsody.model.ArtistData;
import com.github.kaiwinter.rhapsody.model.BioData;
import com.github.kaiwinter.rhapsody.model.GenreData;
import com.github.kaiwinter.rhapsody.model.PasswordGrant;
import com.github.kaiwinter.rhapsody.model.RefreshToken;
import com.github.kaiwinter.rhapsody.service.MemberService;
import com.github.kaiwinter.rhapsody.service.AlbumService;
import com.github.kaiwinter.rhapsody.service.ArtistService;
import com.github.kaiwinter.rhapsody.service.AuthorizingService;
import com.github.kaiwinter.rhapsody.service.GenreService;

import javafx.scene.image.Image;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.Response;

// FIXME KW: replace java.util.Preferences to make this more platform independent, esp. to allow usage on android
/**
 * Wrapper for the Rhapsody REST API. It can be used with a Rhapsody account as well as with a Napster account. This class handles the
 * authorization (user login) against the server and stores the received access and refresh token.
 *
 * <p>
 * Basic usage:
 * </p>
 * <ol>
 * <li>Make a {@link #authorize(String, String, AuthenticationCallback)} request</li>
 * <li>Call one of the load* methods to receive data</li>
 * <li>After the access token is expired (24h) call {@link #refreshToken(AuthenticationCallback)} to get a new access token (this doesn't
 * require the user's login data)</li>
 * </ol>
 */
public final class RhapsodySdkWrapper {
	private static final Logger LOGGER = LoggerFactory.getLogger(RhapsodySdkWrapper.class.getSimpleName());

	private static final String API_URL = "https://api.rhapsody.com";
	private static final String RHAPSODY_IMAGE_URL = "http://direct.rhapsody.com/imageserver/v2/artists/{artist_id}/images/{size}.{extension}";

	private static final String PROPERTY_ACCESS_TOKEN = "access_token";
	private static final String PROPERTY_REFRESH_TOKEN = "refresh_token";
	private static final String PROPERTY_CATALOG = "catalog";

	/** The Rhapsody app API key. */
	private final String apiKey;

	/** The Rhapsody app API secret. */
	private final String apiSecret;

	/** If true the responses of API requests will be formatted for better readability. Useful with higher LogLevel of the RestAdapter. */
	private final boolean prettyJson;

	private final AuthorizingService authService;
	private final GenreService genreService;
	private final ArtistService artistService;
	private final AlbumService albumService;
	private final MemberService memberService;

	private final DataCache dataCache;

	private String accessToken;
	private String refreshToken;
	private String catalog;

	/**
	 * Initializes the API wrapper. Provide the API key and API secret of your app from here:
	 * <a href="https://developer.rhapsody.com/developer/apps">https://developer.rhapsody.com/developer/apps</a>
	 *
	 * @param apiKey
	 *            the API Key, not <code>null</code>
	 * @param apiSecret
	 *            the API Secret, not <code>null</code>
	 *
	 * @throws IllegalArgumentException
	 *             if <code>apiKey</code> or <code>apiSecret</code> is <code>null</code>
	 */
	public RhapsodySdkWrapper(String apiKey, String apiSecret) {
		this(apiKey, apiSecret, false);
	}

	/**
	 * Initializes the API wrapper. Provide the API key and API secret of your app from here:
	 * <a href="https://developer.rhapsody.com/developer/apps">https://developer.rhapsody.com/developer/apps</a>
	 *
	 * @param apiKey
	 *            the API Key, not <code>null</code>
	 * @param apiSecret
	 *            the API Secret, not <code>null</code>
	 * @param verboseLogging
	 *            if <code>true</code> sets retrofit's log level to {@link LogLevel#FULL}
	 *
	 * @throws IllegalArgumentException
	 *             if <code>apiKey</code> or <code>apiSecret</code> is <code>null</code>
	 */
	public RhapsodySdkWrapper(String apiKey, String apiSecret, boolean verboseLogging) {
		if (apiKey == null) {
			throw new IllegalArgumentException("API Key must not be null");
		}
		if (apiSecret == null) {
			throw new IllegalArgumentException("API Secret must not be null");
		}
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;

		RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(API_URL).build();
		authService = restAdapter.create(AuthorizingService.class);
		genreService = restAdapter.create(GenreService.class);
		artistService = restAdapter.create(ArtistService.class);
		albumService = restAdapter.create(AlbumService.class);
		memberService = restAdapter.create(MemberService.class);

		dataCache = new DataCache();

		loadAuthorizationInfo();

		if (verboseLogging) {
			restAdapter.setLogLevel(LogLevel.FULL);
			prettyJson = true;
		} else {
			prettyJson = false;
		}
	}

	private void loadAuthorizationInfo() {
		Preferences preferences = Preferences.userNodeForPackage(getClass());
		accessToken = preferences.get(PROPERTY_ACCESS_TOKEN, null);
		refreshToken = preferences.get(PROPERTY_REFRESH_TOKEN, null);
		catalog = preferences.get(PROPERTY_CATALOG, null);
	}

	private void saveAuthorizationInfo(AccessTokenResponse authorizationResponse) {
		Preferences userNodeForPackage = Preferences.userNodeForPackage(getClass());
		userNodeForPackage.put(PROPERTY_ACCESS_TOKEN, authorizationResponse.access_token);
		userNodeForPackage.put(PROPERTY_REFRESH_TOKEN, authorizationResponse.refresh_token);
		userNodeForPackage.put(PROPERTY_CATALOG, authorizationResponse.catalog);
	}

	/**
	 * Removes the authentication information from the Preferences store. Use this method to log out the user.
	 */
	public void clearAuthorization() {
		Preferences preferences = Preferences.userNodeForPackage(getClass());
		preferences.remove(PROPERTY_ACCESS_TOKEN);
		preferences.remove(PROPERTY_REFRESH_TOKEN);
		preferences.remove(PROPERTY_CATALOG);
		accessToken = null;
		refreshToken = null;
		catalog = null;
	}

	/**
	 * Authorizes for making API requests by using a Rhapsody or Napster login. An access token will be received together with a refresh
	 * token and a catalog ID. All three values are set in this bean and are stored in the Preferences store.
	 *
	 * @param username
	 *            the user name to use for the login
	 * @param password
	 *            the password to use for the login
	 * @param loginCallback
	 *            callback to register for success or failure messages, can be <code>null</code>
	 */
	public void authorize(String username, String password, AuthenticationCallback loginCallback) {
		LOGGER.info("Authorizing");
		String basicAuth = new String(Base64.getEncoder().encode((String.format("%s:%s", apiKey, apiSecret).getBytes())));
		authService.authorizeByPassword(basicAuth, new PasswordGrant(username, password), new Callback<AccessTokenResponse>() {

			@Override
			public void success(AccessTokenResponse authorizationResponse, Response response) {
				LOGGER.info("Successfully authorized, access token: {}", authorizationResponse.access_token);
				accessToken = authorizationResponse.access_token;
				refreshToken = authorizationResponse.refresh_token;
				catalog = authorizationResponse.catalog;
				saveAuthorizationInfo(authorizationResponse);

				if (loginCallback != null) {
					loginCallback.success();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				int status = error.getResponse().getStatus();
				String reason = error.getResponse().getReason();
				LOGGER.error("Error authorizing ()", status, error);

				if (loginCallback != null) {
					loginCallback.failure(status, reason);
				}
			}
		});

	}

	/**
	 * Call this method to get a new access token by making a refresh token request to the server. The advantage of doing a refresh instead
	 * of a new authorization is that the user don't have to enter his login data again.
	 *
	 * @param callback
	 *            callback to register for success or failure messages
	 */
	public void refreshToken(AuthenticationCallback callback) {
		LOGGER.info("Refreshing Token");

		if (refreshToken == null) {
			LOGGER.warn("No refresh token available, make an authorization request before trying a refresh request.");
		}
		RefreshToken refreshTokenObj = new RefreshToken();
		refreshTokenObj.client_id = apiKey;
		refreshTokenObj.client_secret = apiSecret;
		refreshTokenObj.refresh_token = refreshToken;

		authService.refreshAuthorization(refreshTokenObj, new Callback<AccessTokenResponse>() {

			@Override
			public void success(AccessTokenResponse authorizationResponse, Response response) {
				LOGGER.info("Successfully refreshed token, access token: {}", authorizationResponse.access_token);
				accessToken = authorizationResponse.access_token;
				refreshToken = authorizationResponse.refresh_token;
				catalog = authorizationResponse.catalog;
				saveAuthorizationInfo(authorizationResponse);

				callback.success();
			}

			@Override
			public void failure(RetrofitError error) {
				LOGGER.error("Error refreshing token ({} {})", error.getResponse().getStatus(), error.getResponse().getReason());

				callback.failure(error.getResponse().getStatus(), error.getResponse().getReason());
			}
		});
	}

	public void loadAlbum(String albumId, Callback<AlbumData> callback) {
		LOGGER.info("Loading album {}", albumId);
		albumService.getAlbum("Bearer " + accessToken, prettyJson, catalog, albumId, callback);
	}

	public void loadArtistMeta(String artistId, Callback<ArtistData> callback) {
		LOGGER.info("Loading artist's {} info", artistId);
		artistService.getArtist("Bearer " + accessToken, prettyJson, catalog, artistId, callback);
	}

	public void loadArtistBio(String artistId, Callback<BioData> callback) {
		LOGGER.info("Loading artist's {} bio", artistId);
		artistService.getBio("Bearer " + accessToken, prettyJson, catalog, artistId, callback);
	}

	public void loadGenres(Callback<Collection<GenreData>> callback) {
		LOGGER.info("Loading genres");
		genreService.getGenres("Bearer " + accessToken, prettyJson, catalog, callback);
	}

	public void loadAlbumNewReleases(Callback<Collection<AlbumData>> callback) {
		Collection<AlbumData> data = dataCache.getNewReleases("rhapsody");
		if (data == null) {
			LOGGER.info("Loading new releases from server [RHAPSODY]");
			Callback<Collection<AlbumData>> callbackExt = dataCache.getAddNewReleasesToCacheCallback("rhapsody", callback);
			albumService.getNewReleases("Bearer " + accessToken, prettyJson, catalog, callbackExt);
		} else {
			LOGGER.info("Using new releases from cache [RHAPSODY]");
			callback.success(data, null);
		}
	}

	public void loadGenreNewReleases(String genreId, Callback<Collection<AlbumData>> callback) {
		Collection<AlbumData> data = dataCache.getNewReleases(genreId);
		if (data == null) {
			LOGGER.info("Loading new releases from server");
			Callback<Collection<AlbumData>> callbackExt = dataCache.getAddNewReleasesToCacheCallback(genreId, callback);
			genreService.getNewReleases("Bearer " + accessToken, prettyJson, catalog, genreId, null, callbackExt);
		} else {
			LOGGER.info("Using new releases from cache");
			callback.success(data, null);
		}
	}

	public Image loadArtistImage(String artistId) {
		String imageUrl = RHAPSODY_IMAGE_URL.replace("{artist_id}", artistId);
		imageUrl = imageUrl.replace("{size}.{extension}", "356x237.png");

		return new Image(imageUrl, true);
	}

	public void loadAccount(Callback<AccountData> callback) {
		LOGGER.info("Loading account information");
		memberService.getAccount("Bearer " + accessToken, prettyJson, callback);
	}
}
