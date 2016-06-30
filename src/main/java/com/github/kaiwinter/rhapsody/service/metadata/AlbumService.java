package com.github.kaiwinter.rhapsody.service.metadata;

import java.util.Collection;

import com.github.kaiwinter.rhapsody.model.AccountData;
import com.github.kaiwinter.rhapsody.model.AlbumData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Wrapper of the Album REST API.
 * 
 * @see https://developer.rhapsody.com/api#albums
 */
public interface AlbumService {

   /**
    * Asynchronously returns detailed information about a given album, including its tracks.
    * 
    * @param albumId
    *           the ID of the album to load
    * @param apikey
    *           the API key
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param catalog
    *           countries' catalog (two-letter country code, which is case-sensitive)
    * @return asynchronous result
    */
   @GET("/v1/albums/{albumId}")
   Call<AlbumData> getAlbum( //
      @Path("albumId") String albumId, //
      @Query("apikey") String apikey, //
      @Query("pretty") boolean pretty, //
      @Query("catalog") String catalog);

   /**
    * Returns a list of new releases, curated by Rhapsody. This list can be personalized for the user by passing the
    * <code>userId</code>. The personalization is made by Rhapsody based upon recent listening history.
    *
    * <p>
    * <i>Hint:</i> The <code>userId</code> is the {@link AccountData#id}.
    * </p>
    *
    * @param apikey
    *           the API key
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param catalog
    *           countries' catalog (two-letter country code, which is case-sensitive)
    * @param userId
    *           the user ID to get personalized new releases, if <code>null</code>no personalization is made
    * @return asynchronous result
    */
   @GET("/v1/albums/new")
   public Call<Collection<AlbumData>> getNewReleases( //
      @Query("apikey") String apikey, //
      @Query("pretty") boolean pretty, //
      @Query("catalog") String catalog, //
      @Query("guid") String userId);
}