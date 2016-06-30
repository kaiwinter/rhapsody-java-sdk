package com.github.kaiwinter.rhapsody.api;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kaiwinter.rhapsody.cache.DataCache;
import com.github.kaiwinter.rhapsody.model.AccessToken;
import com.github.kaiwinter.rhapsody.model.AccountData;
import com.github.kaiwinter.rhapsody.model.AlbumData;
import com.github.kaiwinter.rhapsody.model.AlbumData.Artist;
import com.github.kaiwinter.rhapsody.model.ArtistData;
import com.github.kaiwinter.rhapsody.model.BioData;
import com.github.kaiwinter.rhapsody.model.GenreData;
import com.github.kaiwinter.rhapsody.model.PasswordGrant;
import com.github.kaiwinter.rhapsody.model.RefreshToken;
import com.github.kaiwinter.rhapsody.model.member.ChartsAlbum;
import com.github.kaiwinter.rhapsody.model.member.ChartsArtist;
import com.github.kaiwinter.rhapsody.model.member.ChartsTrack;
import com.github.kaiwinter.rhapsody.persistence.AuthorizationStore;
import com.github.kaiwinter.rhapsody.persistence.impl.TransientAuthorizationStore;
import com.github.kaiwinter.rhapsody.persistence.model.AuthorizationInfo;
import com.github.kaiwinter.rhapsody.service.authentication.AuthenticationService;
import com.github.kaiwinter.rhapsody.service.member.AccountService;
import com.github.kaiwinter.rhapsody.service.member.ChartService;
import com.github.kaiwinter.rhapsody.service.member.ChartService.RangeEnum;
import com.github.kaiwinter.rhapsody.service.member.LibraryService;
import com.github.kaiwinter.rhapsody.service.metadata.AlbumService;
import com.github.kaiwinter.rhapsody.service.metadata.ArtistService;
import com.github.kaiwinter.rhapsody.service.metadata.GenreService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Wrapper for the Rhapsody REST API. It can be used with a Rhapsody account as well as with a Napster account. This
 * class handles the authorization (user login) against the server and stores the received access and refresh token.
 *
 * <p>
 * Basic usage:
 * </p>
 * <ol>
 * <li>Make a {@link #authorize(String, String, AuthenticationCallback)} request</li>
 * <li>Call one of the load* methods to receive data</li>
 * <li>After the access token is expired (24h) call {@link #refreshToken(AuthenticationCallback)} to get a new access
 * token (this doesn't require the user's login data)</li>
 * </ol>
 */
public class RhapsodySdkWrapper {
   private static final Logger LOGGER = LoggerFactory.getLogger(RhapsodySdkWrapper.class.getSimpleName());

   private static final String API_URL = "https://api.rhapsody.com";
   private static final String RHAPSODY_IMAGE_URL = "http://direct.rhapsody.com/imageserver/v2/artists/{artist_id}/images/{size}.{extension}";

   /** The Rhapsody app API key. */
   private final String apiKey;

   /** The Rhapsody app API secret. */
   private final String apiSecret;

   private Retrofit restAdapter;
   private AuthenticationService authService;
   private GenreService genreService;
   private ArtistService artistService;
   private AlbumService albumService;
   private AccountService memberService;
   private LibraryService libraryService;
   private ChartService chartService;

   private final DataCache dataCache;

   private final AuthorizationStore authorizationStore;

   private AuthorizationInfo authorizationInfo;

   /**
    * If true the responses of API requests will be formatted for better readability. Useful with higher LogLevel of the
    * RestAdapter.
    */
   private boolean prettyJson;

   /**
    * Initializes the API wrapper. Provide the API key and API secret of your app from here:
    * <a href="https://developer.rhapsody.com/developer/apps">https://developer.rhapsody.com/developer/apps</a>
    *
    * @param apiKey
    *           the API Key, not <code>null</code>
    * @param apiSecret
    *           the API Secret, not <code>null</code>
    *
    * @throws NullPointerException
    *            if <code>apiKey</code> or <code>apiSecret</code> is <code>null</code>
    */
   public RhapsodySdkWrapper(String apiKey, String apiSecret) {
      this(apiKey, apiSecret, null);
   }

   /**
    * Initializes the API wrapper. Provide the API key and API secret of your app from here:
    * <a href="https://developer.rhapsody.com/developer/apps">https://developer.rhapsody.com/developer/apps</a>
    *
    * @param apiKey
    *           the API Key, not <code>null</code>
    * @param apiSecret
    *           the API Secret, not <code>null</code>
    * @param authorizationStore
    *           {@link AuthorizationStore} implementation to persist user authentication data. If <code>null</code> they
    *           are not persisted.
    *
    * @throws NullPointerException
    *            if <code>apiKey</code> or <code>apiSecret</code> is <code>null</code>
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

      initServices(HttpLoggingInterceptor.Level.NONE);

      dataCache = new DataCache();

      authorizationInfo = this.authorizationStore.loadAuthorizationInfo();
   }

   /**
    * Enabled/Disables full logging of retrofit REST calls.
    *
    * @param enabled
    *           if <code>true</code> sets retrofit's log level to {@link LogLevel#FULL}
    */
   public void setVerboseLoggingEnabled(boolean enabled) {
      if (enabled) {
         initServices(HttpLoggingInterceptor.Level.BODY);

         prettyJson = true;
      } else {
         prettyJson = false;
      }
   }

   private void initServices(Level level) {
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(level);
      OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
      restAdapter = new Retrofit.Builder().baseUrl(API_URL).client(client)
         .addConverterFactory(GsonConverterFactory.create()).build();

      authService = restAdapter.create(AuthenticationService.class);
      genreService = restAdapter.create(GenreService.class);
      artistService = restAdapter.create(ArtistService.class);
      albumService = restAdapter.create(AlbumService.class);
      memberService = restAdapter.create(AccountService.class);
      libraryService = restAdapter.create(LibraryService.class);
      chartService = restAdapter.create(ChartService.class);
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
    * Authorizes for making API requests by using a Rhapsody or Napster login. An access token will be received together
    * with a refresh token and a catalog ID. All three values are set in this bean and are stored in the Preferences
    * store.
    *
    * @param username
    *           the user name to use for the login
    * @param password
    *           the password to use for the login
    * @param loginCallback
    *           callback to register for success or failure messages, can be <code>null</code>
    */
   public void authorize(String username, String password, AuthenticationCallback loginCallback) {
      LOGGER.info("Authorizing");
      String basicAuth = new String(Base64.getEncoder().encode((String.format("%s:%s", apiKey, apiSecret).getBytes())));
      Call<AccessToken> call = authService.authorizeByPasswordAsync(basicAuth, new PasswordGrant(username, password));
      call.enqueue(new Callback<AccessToken>() {

         @Override
         public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
            if (response.isSuccessful()) {
               LOGGER.info("Successfully authorized, access token: {}", response.body().access_token);
               authorizationInfo = new AuthorizationInfo();
               authorizationInfo.accessToken = response.body().access_token;
               authorizationInfo.refreshToken = response.body().refresh_token;
               authorizationInfo.catalog = response.body().catalog;
               authorizationStore.saveAuthorizationInfo(authorizationInfo);

               if (loginCallback != null) {
                  loginCallback.success();
               }
            } else {
               handleError(loginCallback, response);
            }
         }

         @Override
         public void onFailure(Call<AccessToken> call, Throwable throwable) {
            handleError(loginCallback, throwable);
         }
      });
   }

   private void handleError(AuthenticationCallback loginCallback, Response<AccessToken> response) {
      int status = response.code();
      String reason = response.message();
      LOGGER.error("Error authorizing ({} {})", status, reason);

      if (loginCallback != null) {
         loginCallback.failure(status, reason);
      }
   }

   private void handleError(AuthenticationCallback loginCallback, Throwable throwable) {
      LOGGER.error("Error authorizing ({})", throwable.getMessage());

      if (loginCallback != null) {
         loginCallback.failure(0, throwable.getMessage());
      }
   }

   /**
    * Call this method to get a new access token by making a refresh token request to the server. The advantage of doing
    * a refresh instead of a new authorization is that the user don't have to enter his login data again.
    *
    * @param callback
    *           callback to register for success or failure messages
    */
   public void refreshToken(AuthenticationCallback callback) {
      LOGGER.info("Refreshing Token");

      if (authorizationInfo.refreshToken == null) {
         LOGGER.warn("No refresh token available, make an authorization request before trying a refresh request.");
      }
      RefreshToken refreshToken = new RefreshToken();
      refreshToken.client_id = apiKey;
      refreshToken.client_secret = apiSecret;
      refreshToken.refresh_token = authorizationInfo.refreshToken;

      Call<AccessToken> call = authService.refreshAuthorizationAsync(refreshToken);
      call.enqueue(new Callback<AccessToken>() {

         @Override
         public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
            if (response.isSuccessful()) {
               LOGGER.info("Successfully refreshed token, access token: {}", response.body().access_token);
               authorizationInfo.accessToken = response.body().access_token;
               authorizationInfo.refreshToken = response.body().refresh_token;
               authorizationStore.saveAuthorizationInfo(authorizationInfo);

               callback.success();
            } else {
               LOGGER.error("Error refreshing token ({} {})", response.code(), response.message());

               callback.failure(response.code(), response.message());
            }
         }

         @Override
         public void onFailure(Call<AccessToken> call, Throwable throwable) {
            handleError(callback, throwable);
         }
      });
   }

   private String getAuthorizationString() {
      String authorization = "Bearer " + authorizationInfo.accessToken;
      return authorization;
   }

   /**
    * Asynchronously loads the album with the given <code>albumId</code> asynchronously.
    *
    * <p>
    * REST-method: <code>/albums/{albumId}</code>
    * </p>
    *
    * @param albumId
    *           the ID of the album to load
    * @param callback
    *           callback which is called on success or failure
    */
   public void loadAlbum(String albumId, Callback<AlbumData> callback) {
      LOGGER.info("Loading album {}", albumId);
      Call<AlbumData> call = albumService.getAlbum(albumId, apiKey, prettyJson, authorizationInfo.catalog);
      call.enqueue(callback);
   }

   /**
    * Synchronously loads the album with the given <code>albumId</code> synchronously.
    *
    * <p>
    * REST-method: <code>/albums/{albumId}</code>
    * </p>
    *
    * @param albumId
    *           the ID of the album to load
    */
   public AlbumData getAlbum(String albumId) {
      LOGGER.info("Loading album {}", albumId);
      Call<AlbumData> call = albumService.getAlbum(albumId, apiKey, prettyJson, authorizationInfo.catalog);
      try {
         return call.execute().body();
      } catch (IOException e) {
         LOGGER.info("Error loading albums", e);
         return null;
      }
   }

   /**
    * Asynchronously loads the artist's metadata ({@link ArtistData}) with the given <code>artistId</code>
    * asynchronously.
    *
    * <p>
    * REST-method: <code>/artists/{artistId}</code>
    * </p>
    *
    * @param artistId
    *           the ID of the artist to load
    * @param callback
    *           callback which is called on success or failure
    */
   public void loadArtistMeta(String artistId, Callback<ArtistData> callback) {
      LOGGER.info("Loading artist's {} info", artistId);
      Call<ArtistData> call = artistService.getArtist(artistId, apiKey, prettyJson, authorizationInfo.catalog);
      call.enqueue(callback);
   }

   /**
    * Synchronously loads the artist's metadata ({@link ArtistData}) with the given <code>artistId</code> synchronously.
    * 
    * <p>
    * REST-method: <code>/artists/{artistId}</code>
    * </p>
    * 
    * @param artistId
    *           the ID of the artist to load
    * @return the artist's meta information, or <code>null</code>
    */
   public ArtistData getArtistMeta(String artistId) {
      LOGGER.info("Loading artist's {} info", artistId);
      Call<ArtistData> call = artistService.getArtist(artistId, apiKey, prettyJson, authorizationInfo.catalog);
      try {
         return call.execute().body();
      } catch (IOException e) {
         LOGGER.info("Error loading artist's info", e);
         return null;
      }
   }

   /**
    * Loads the artist's biography ({@link ArtistBio}) with the given <code>artistId</code> asynchronously.
    *
    * <p>
    * REST-method: <code>/artists/{artistId}/bio</code>
    * </p>
    *
    * @param artistId
    *           the ID of the artist to load
    * @param callback
    *           callback which is called on success or failure
    */
   public void loadArtistBio(String artistId, Callback<BioData> callback) {
      LOGGER.info("Loading artist's {} bio", artistId);
      Call<BioData> call = artistService.getBio(artistId, apiKey, prettyJson, authorizationInfo.catalog);
      call.enqueue(callback);
   }

   /**
    * Loads Rhapsody genres asynchronously.
    *
    * <p>
    * REST-method: <code>/genres</code>
    * </p>
    *
    * @param callback
    *           callback which is called on success or failure
    */
   public void loadGenres(Callback<Collection<GenreData>> callback) {
      LOGGER.info("Loading genres");
      Call<Collection<GenreData>> call = genreService.getGenresAsync(apiKey, prettyJson, authorizationInfo.catalog);
      call.enqueue(callback);
   }

   /**
    * Loads new releases, curated by Rhapsody asynchronously. This list can be personalized for the user by passing the
    * <code>userId</code> . The personalization is made by Rhapsody based upon recent listening history. If
    * <code>userId</code> is <code>null</code> the parameter is ignored.
    *
    * <p>
    * REST-method: <code>/albums/new</code>
    * </p>
    *
    * @param userId
    *           the <code>guid</code> of the user, may be <code>null</code>
    * @param callback
    *           callback which is called on success or failure
    */
   public void loadAlbumNewReleases(String userId, Callback<Collection<AlbumData>> callback) {
      String cacheId = "rhapsody" + userId;
      Collection<AlbumData> data = dataCache.getNewReleases(cacheId);
      if (data == null) {
         LOGGER.info("Loading curated album releases from server");
         Callback<Collection<AlbumData>> callbackExt = dataCache.getAddNewReleasesToCacheCallback(cacheId, callback);
         Call<Collection<AlbumData>> call = albumService.getNewReleases(apiKey, prettyJson, authorizationInfo.catalog,
            userId);
         call.enqueue(callbackExt);
      } else {
         LOGGER.info("Using curated album releases from cache");
         callback.onResponse(null, Response.success(data));
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
    *           the ID of the genre to load new releases
    * @param limit
    *           the number of releases to load, if <code>null</code> the default value is used (20)
    * @param callback
    *           callback which is called on success or failure
    */
   public void loadGenreNewReleases(String genreId, Integer limit, Callback<Collection<AlbumData>> callback) {
      Collection<AlbumData> data = dataCache.getNewReleases(genreId);
      if (data == null) {
         LOGGER.info("Loading genre new releases from server");
         Callback<Collection<AlbumData>> callbackExt = dataCache.getAddNewReleasesToCacheCallback(genreId, callback);
         Call<Collection<AlbumData>> call = genreService.getNewReleasesAsync(genreId, apiKey, prettyJson,
            authorizationInfo.catalog, limit);
         call.enqueue(callbackExt);
      } else {
         LOGGER.info("Using genre new releases from cache");
         callback.onResponse(null, Response.success(data));
      }
   }

   /**
    * Generate a valid request URL for an artist image.
    *
    * @param artistId
    *           the ID of the artist
    * @param imageSize
    *           the size of the image
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
    *           callback which is called on success or failure
    */
   public void loadAccount(Callback<AccountData> callback) {
      LOGGER.info("Loading account information");
      String authorization = getAuthorizationString();
      Call<AccountData> call = memberService.getAccountAsync(authorization, prettyJson);
      call.enqueue(callback);
   }

   /**
    * Synchronously returns a list of an artist's new releases (if any), updated weekly.
    *
    * @param artistId
    *           the ID of the artist
    * @param limit
    *           the number of releases to load, if <code>null</code> the default value is used (20)
    * @return a list of an artist's new releases
    */
   public Collection<AlbumData> getArtistNewReleases(String artistId, Integer limit) {
      LOGGER.info("Loading artist new releases");
      Call<Collection<AlbumData>> newReleases = artistService.getNewReleases(artistId, apiKey, prettyJson,
         authorizationInfo.catalog, limit);

      try {
         return newReleases.execute().body();
      } catch (IOException e) {
         LOGGER.info("Error loading artist new releases", e);
         return Collections.emptyList();
      }
   }

   /**
    * Loads a list of all artists in the user's library.
    *
    * <p>
    * REST-method: <code>/me/library/artists</code>
    * </p>
    *
    * @param limit
    *           the number of releases to load, if <code>null</code> the default value is used (20)
    * @param callback
    *           callback which is called on success or failure
    */
   public void loadAllArtistsInLibrary(Integer limit, Callback<Collection<Artist>> callback) {
      LOGGER.info("Loading all artists in library");
      Call<Collection<Artist>> call = libraryService.loadAllArtistsInLibrary(getAuthorizationString(), prettyJson,
         limit);
      call.enqueue(callback);
   }

   /**
    * Loads a list of albums in a member’s library by the artist.
    *
    * <p>
    * REST-method: <code>/me/library/artists/{artistId}/albums</code>
    * </p>
    *
    * @param artistId
    *           the ID of the artist to load
    * @param limit
    *           the number of releases to load, if <code>null</code> the default value is used (20)
    * @param callback
    *           callback which is called on success or failure
    */
   public void loadAllAlbumsByArtistInLibrary(String artistId, Integer limit,
      Callback<Collection<AlbumData>> callback) {
      LOGGER.info("Loading all albums by artists in library");
      Call<Collection<AlbumData>> call = libraryService.loadAllAlbumsByArtistInLibrary(getAuthorizationString(),
         artistId, prettyJson, limit);
      call.enqueue(callback);
   }

   /**
    * Loads a list of albums in a member’s library.
    *
    * <p>
    * REST-method: <code>/me/library/albums</code>
    * </p>
    *
    * @param limit
    *           the number of releases to load, if <code>null</code> the default value is used (20)
    * @param callback
    *           callback which is called on success or failure
    */
   public void loadAllAlbumsInLibrary(Integer limit, Callback<Collection<AlbumData>> callback) {
      LOGGER.info("Loading all albums in library");
      Call<Collection<AlbumData>> call = libraryService.loadAllAlbumsInLibrary(getAuthorizationString(), prettyJson,
         limit);
      call.enqueue(callback);
   }

   /**
    * Loads the top played tracks.
    * 
    * <p>
    * REST-method: <code>/me/charts/tracks</code>
    * </p>
    * 
    * @param limit
    *           the number of tracks to load, if <code>null</code> the default value is used (20)
    * @param range
    *           the period to consider for the charts
    * @param callback
    *           callback which is called on success or failure
    */
   public void loadTopPlayedTracks(Integer limit, RangeEnum range, Callback<List<ChartsTrack>> callback) {
      LOGGER.info("Loading artist charts");
      Call<List<ChartsTrack>> call = chartService.loadTopPlayedTracksAsync(getAuthorizationString(), prettyJson, limit,
         RangeEnum.life);
      call.enqueue(callback);
   }

   /**
    * Loads the top played artists.
    * 
    * <p>
    * REST-method: <code>/me/charts/artists</code>
    * </p>
    * 
    * @param limit
    *           the number of artists to load, if <code>null</code> the default value is used (20)
    * @param range
    *           the period to consider for the charts
    * @param callback
    *           callback which is called on success or failure
    */
   public void loadTopPlayedArtists(Integer limit, RangeEnum range, Callback<List<ChartsArtist>> callback) {
      LOGGER.info("Loading artist charts");
      Call<List<ChartsArtist>> call = chartService.loadTopPlayedArtistsAsync(getAuthorizationString(), prettyJson,
         limit, RangeEnum.life);
      call.enqueue(callback);
   }

   /**
    * Loads the top played albums.
    * 
    * <p>
    * REST-method: <code>/me/charts/albums</code>
    * </p>
    * 
    * @param limit
    *           the number of albums to load, if <code>null</code> the default value is used (20)
    * @param range
    *           the period to consider for the charts
    * @param callback
    *           callback which is called on success or failure
    */
   public void loadTopPlayedAlbums(Integer limit, RangeEnum range, Callback<List<ChartsAlbum>> callback) {
      LOGGER.info("Loading album charts");
      Call<List<ChartsAlbum>> call = chartService.loadTopPlayedAlbumsAsync(getAuthorizationString(), prettyJson, limit,
         RangeEnum.life);
      call.enqueue(callback);
   }

   /**
    * Adds an album to the user's library.
    * 
    * @param albumId
    *           the ID of the album to add
    * @param callback
    *           doesn't return any data except the HTTP Response
    */
   public void addAlbumToLibrary(String albumId, Callback<Void> callback) {
      LOGGER.info("Adding album with ID '{}' to library", albumId);
      Call<Void> call = libraryService.addAlbumToLibrary(getAuthorizationString(), authorizationInfo.catalog, albumId);
      call.enqueue(callback);
   }

   /**
    * Deletes an album from the user's library.
    * 
    * <p>
    * REST-method: <code>/v1/me/library/albums/{albumId}</code>
    * </p>
    * 
    * @param albumId
    *           the ID of the album to remove
    * @param callback
    *           doesn't return any data except the HTTP Response
    */
   public void removeAlbumFromLibrary(String albumId, Callback<Void> callback) {
      LOGGER.info("Removing album with ID '{}' from library", albumId);
      Call<Void> call = libraryService.removeAlbumFromLibrary(getAuthorizationString(), albumId);
      call.enqueue(callback);
   }
}
