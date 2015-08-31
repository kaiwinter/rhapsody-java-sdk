package com.github.kaiwinter.rhapsody.api;

import java.util.Base64;
import java.util.Collection;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaiwinter.rhapsody.cache.DataCache;
import com.github.kaiwinter.rhapsody.model.AccessToken;
import com.github.kaiwinter.rhapsody.model.AccountData;
import com.github.kaiwinter.rhapsody.model.AlbumData;
import com.github.kaiwinter.rhapsody.model.ArtistData;
import com.github.kaiwinter.rhapsody.model.BioData;
import com.github.kaiwinter.rhapsody.model.GenreData;
import com.github.kaiwinter.rhapsody.model.PasswordGrant;
import com.github.kaiwinter.rhapsody.model.RefreshToken;
import com.github.kaiwinter.rhapsody.persistence.AuthorizationStore;
import com.github.kaiwinter.rhapsody.persistence.impl.TransientAuthorizationStore;
import com.github.kaiwinter.rhapsody.persistence.model.AuthorizationInfo;
import com.github.kaiwinter.rhapsody.service.AlbumService;
import com.github.kaiwinter.rhapsody.service.ArtistService;
import com.github.kaiwinter.rhapsody.service.AuthenticationService;
import com.github.kaiwinter.rhapsody.service.GenreService;
import com.github.kaiwinter.rhapsody.service.MemberService;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RestAdapter.LogLevel;
import retrofit.RetrofitError;
import retrofit.client.Response;

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

	/** The Rhapsody app API key. */
	private final String apiKey;

	/** The Rhapsody app API secret. */
	private final String apiSecret;

	private final RestAdapter restAdapter;
	private final AuthenticationService authService;
	private final GenreService genreService;
	private final ArtistService artistService;
	private final AlbumService albumService;
	private final MemberService memberService;

	private final DataCache dataCache;

	private final AuthorizationStore authorizationStore;

	private AuthorizationInfo authorizationInfo;

	/** If true the responses of API requests will be formatted for better readability. Useful with higher LogLevel of the RestAdapter. */
	private boolean prettyJson;

	/**
	 * Initializes the API wrapper. Provide the API key and API secret of your app from here:
	 * <a href="https://developer.rhapsody.com/developer/apps">https://developer.rhapsody.com/developer/apps</a>
	 *
	 * @param apiKey
	 *            the API Key, not <code>null</code>
	 * @param apiSecret
	 *            the API Secret, not <code>null</code>
	 *
	 * @throws NullPointerException
	 *             if <code>apiKey</code> or <code>apiSecret</code> is <code>null</code>
	 */
	public RhapsodySdkWrapper(String apiKey, String apiSecret) {
		this(apiKey, apiSecret, null);
	}

	/**
	 * Initializes the API wrapper. Provide the API key and API secret of your app from here:
	 * <a href="https://developer.rhapsody.com/developer/apps">https://developer.rhapsody.com/developer/apps</a>
	 *
	 * @param apiKey
	 *            the API Key, not <code>null</code>
	 * @param apiSecret
	 *            the API Secret, not <code>null</code>
	 * @param authorizationStore
	 *            {@link AuthorizationStore} implementation to persist user authentication data. If <code>null</code> they are not
	 *            persisted.
	 *
	 * @throws NullPointerException
	 *             if <code>apiKey</code> or <code>apiSecret</code> is <code>null</code>
	 */
	public RhapsodySdkWrapper(String apiKey, String apiSecret, AuthorizationStore authorizationStore) {
		Objects.requireNonNull(apiKey, "API Key must not be null");
		Objects.requireNonNull(apiSecret, "API Secret must not be null");

		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
		if (authorizationStore == null) {
			this.authorizationStore = new TransientAuthorizationStore();
		} else {
			this.authorizationStore = authorizationStore;
		}

		restAdapter = new RestAdapter.Builder().setEndpoint(API_URL).build();
		authService = restAdapter.create(AuthenticationService.class);
		genreService = restAdapter.create(GenreService.class);
		artistService = restAdapter.create(ArtistService.class);
		albumService = restAdapter.create(AlbumService.class);
		memberService = restAdapter.create(MemberService.class);

		dataCache = new DataCache();

		authorizationInfo = this.authorizationStore.loadAuthorizationInfo();
	}

	/**
	 * Enabled/Disables full logging of retrofit REST calls.
	 * 
	 * @param enabled
	 *            if <code>true</code> sets retrofit's log level to {@link LogLevel#FULL}
	 */
	public void setVerboseLoggingEnabled(boolean enabled) {
		if (enabled) {
			restAdapter.setLogLevel(LogLevel.FULL);
			prettyJson = true;
		} else {
			prettyJson = false;
		}
	}

	/**
	 * Removes the authentication information from the store. Use this method to log out the user.
	 */
	public void clearAuthorization() {
		authorizationStore.clearAuthorization();
		authorizationInfo.accessToken = null;
		authorizationInfo.refreshToken = null;
		authorizationInfo.catalog = null;
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
		authService.authorizeByPassword(basicAuth, new PasswordGrant(username, password), new Callback<AccessToken>() {

			@Override
			public void success(AccessToken authorizationResponse, Response response) {
				LOGGER.info("Successfully authorized, access token: {}", authorizationResponse.access_token);
				authorizationInfo = new AuthorizationInfo(authorizationResponse);
				authorizationStore.saveAuthorizationInfo(authorizationInfo);

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

		if (authorizationInfo.refreshToken == null) {
			LOGGER.warn("No refresh token available, make an authorization request before trying a refresh request.");
		}
		RefreshToken refreshTokenObj = new RefreshToken();
		refreshTokenObj.client_id = apiKey;
		refreshTokenObj.client_secret = apiSecret;
		refreshTokenObj.refresh_token = authorizationInfo.refreshToken;

		authService.refreshAuthorization(refreshTokenObj, new Callback<AccessToken>() {

			@Override
			public void success(AccessToken authorizationResponse, Response response) {
				LOGGER.info("Successfully refreshed token, access token: {}", authorizationResponse.access_token);
				authorizationInfo = new AuthorizationInfo(authorizationResponse);
				authorizationStore.saveAuthorizationInfo(authorizationInfo);

				callback.success();
			}

			@Override
			public void failure(RetrofitError error) {
				LOGGER.error("Error refreshing token ({} {})", error.getResponse().getStatus(), error.getResponse().getReason());

				callback.failure(error.getResponse().getStatus(), error.getResponse().getReason());
			}
		});
	}

	private String getAuthorizationString() {
		String authorization = "Bearer " + authorizationInfo.accessToken;
		return authorization;
	}

	/**
	 * Loads the album with the given <code>albumId</code> asynchronously.
	 *
	 * <p>
	 * REST-method: <code>/albums/{albumId}</code>
	 * </p>
	 *
	 * @param albumId
	 *            the ID of the album to load
	 * @param callback
	 *            callback which is called on success or failure
	 */
	public void loadAlbum(String albumId, Callback<AlbumData> callback) {
		LOGGER.info("Loading album {}", albumId);
		String authorization = getAuthorizationString();
		albumService.getAlbum(authorization, prettyJson, authorizationInfo.catalog, albumId, callback);
	}

	/**
	 * Loads the artist's metadata ({@link ArtistData}) with the given <code>artistId</code> asynchronously.
	 *
	 * <p>
	 * REST-method: <code>/artists/{artistId}</code>
	 * </p>
	 *
	 * @param artistId
	 *            the ID of the artist to load
	 * @param callback
	 *            callback which is called on success or failure
	 */
	public void loadArtistMeta(String artistId, Callback<ArtistData> callback) {
		LOGGER.info("Loading artist's {} info", artistId);
		String authorization = getAuthorizationString();
		artistService.getArtist(authorization, prettyJson, authorizationInfo.catalog, artistId, callback);
	}

	/**
	 * Loads the artist's biography ({@link ArtistBio}) with the given <code>artistId</code> asynchronously.
	 *
	 * <p>
	 * REST-method: <code>/artists/{artistId}/bio</code>
	 * </p>
	 *
	 * @param artistId
	 *            the ID of the artist to load
	 * @param callback
	 *            callback which is called on success or failure
	 */
	public void loadArtistBio(String artistId, Callback<BioData> callback) {
		LOGGER.info("Loading artist's {} bio", artistId);
		String authorization = getAuthorizationString();
		artistService.getBio(authorization, prettyJson, authorizationInfo.catalog, artistId, callback);
	}

	/**
	 * Loads Rhapsody genres asynchronously.
	 *
	 * <p>
	 * REST-method: <code>/genres</code>
	 * </p>
	 *
	 * @param artistId
	 *            the ID of the artist to load
	 * @param callback
	 *            callback which is called on success or failure
	 */
	public void loadGenres(Callback<Collection<GenreData>> callback) {
		LOGGER.info("Loading genres");
		String authorization = getAuthorizationString();
		genreService.getGenres(authorization, prettyJson, authorizationInfo.catalog, callback);
	}

	/**
	 * Loads new releases, curated by Rhapsody asynchronously. This list can be personalized for the user by passing the <code>userId</code>
	 * . The personalization is made by Rhapsody based upon recent listening history. If <code>userId</code> is <code>null</code> the
	 * parameter is ignored.
	 *
	 * <p>
	 * REST-method: <code>/albums/new</code>
	 * </p>
	 *
	 * @param userId
	 *            the <code>guid</code> of the user, may be <code>null</code>
	 * @param callback
	 *            callback which is called on success or failure
	 */
	public void loadAlbumNewReleases(String userId, Callback<Collection<AlbumData>> callback) {
		String cacheId = "rhapsody" + userId;
		Collection<AlbumData> data = dataCache.getNewReleases(cacheId);
		if (data == null) {
			LOGGER.info("Loading curated album releases from server");
			String authorization = getAuthorizationString();
			Callback<Collection<AlbumData>> callbackExt = dataCache.getAddNewReleasesToCacheCallback(cacheId, callback);
			albumService.getNewReleases(authorization, prettyJson, authorizationInfo.catalog, userId, callbackExt);
		} else {
			LOGGER.info("Using curated album releases from cache");
			callback.success(data, null);
		}
	}

	/**
	 * Loads all new releases for the genre with the given <code>genreId</code> asynchronously.
	 *
	 * <p>
	 * REST-method: <code>/genres/{genreId}/albums/new</code>
	 * </p>
	 *
	 * @param genreId
	 *            the ID of the genre to load new releases
	 * @param limit
	 *            the number of releases to load, if <code>null</code> the default value is used (20)
	 * @param callback
	 *            callback which is called on success or failure
	 */
	public void loadGenreNewReleases(String genreId, Integer limit, Callback<Collection<AlbumData>> callback) {
		Collection<AlbumData> data = dataCache.getNewReleases(genreId);
		if (data == null) {
			LOGGER.info("Loading genre new releases from server");
			String authorization = getAuthorizationString();
			Callback<Collection<AlbumData>> callbackExt = dataCache.getAddNewReleasesToCacheCallback(genreId, callback);
			genreService.getNewReleases(authorization, prettyJson, authorizationInfo.catalog, genreId, limit, callbackExt);
		} else {
			LOGGER.info("Using genre new releases from cache");
			callback.success(data, null);
		}
	}

	/**
	 * Generate a valid request URL for an artist image.
	 *
	 * @param artistId
	 *            the ID of the artist
	 * @param imageSize
	 *            the size of the image
	 * @return image URL
	 */
	public String getArtistImageUrl(String artistId, ArtistImageSize imageSize) {
		String imageUrl = RHAPSODY_IMAGE_URL.replace("{artist_id}", artistId);
		imageUrl = imageUrl.replace("{size}.{extension}", imageSize.getSize() + ".png");

		return imageUrl;
	}

	/**
	 * Loads the user account ({@link AccountData}).
	 *
	 * <p>
	 * REST-method: <code>/me/account</code>
	 * </p>
	 *
	 * @param callback
	 *            callback which is called on success or failure
	 */
	public void loadAccount(Callback<AccountData> callback) {
		LOGGER.info("Loading account information");
		String authorization = getAuthorizationString();
		memberService.getAccount(authorization, prettyJson, callback);
	}

	/**
	 * Synchronously returns a list of an artist's new releases (if any), updated weekly.
	 * 
	 * @param artistId
	 *            the ID of the artist
	 * @param limit
	 *            the number of releases to load, if <code>null</code> the default value is used (20)
	 * @return a list of an artist's new releases
	 */
	public Collection<AlbumData> getArtistNewReleases(String artistId, Integer limit) {
		LOGGER.info("Loading artist new releases");
		String authorization = getAuthorizationString();
		Collection<AlbumData> newReleases = artistService.getNewReleases(authorization, prettyJson, authorizationInfo.catalog, artistId);

		return newReleases;
	}
}
