package com.github.kaiwinter.rhapsody.service.member;

import java.util.Collection;

import com.github.kaiwinter.rhapsody.model.AlbumData;
import com.github.kaiwinter.rhapsody.model.AlbumData.Artist;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Wrapper of the Album REST API.
 *
 * @see https://developer.rhapsody.com/api#albums
 */
public interface LibraryService {

   /**
    * Returns a list of all artists in the user's library. Accepts optional parameters limit and offset to select a
    * range of results.
    *
    * @param authorization
    *           the access token
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param limit
    *           the number of releases which are loaded, if <code>null</code> the servers default value is used
    * @return asynchronous result
    */
   @GET("/v1/me/library/artists")
   Call<Collection<Artist>> loadAllArtistsInLibrary( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty, //
      @Query("limit") Integer limit);

   /**
    * Returns a list of albums in a member’s library by the artist. Accepts optional parameters limit and offset to
    * select a range of results.
    *
    * @param authorization
    *           the access token
    * @param artistId
    *           the ID of the artist to load
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param limit
    *           the number of releases which are loaded, if <code>null</code> the servers default value is used
    * @return asynchronous result
    */
   @GET("/v1/me/library/artists/{artistId}/albums")
   Call<Collection<AlbumData>> loadAllAlbumsByArtistInLibrary( //
      @Header("Authorization") String authorization, //
      @Path("artistId") String artistId, //
      @Query("pretty") boolean pretty, //
      @Query("limit") Integer limit);

   /**
    * Returns a list of albums in a member’s library. Accepts optional parameters limit and offset to select a range of
    * results.
    * 
    * @param authorization
    *           the access token
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param limit
    *           the number of releases which are loaded, if <code>null</code> the servers default value is used
    * @return asynchronous result
    */
   @GET("/v1/me/library/albums")
   Call<Collection<AlbumData>> loadAllAlbumsInLibrary( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty, //
      @Query("limit") Integer limit);

   /**
    * Adds an album to the user's library.
    * 
    * @param authorization
    *           the access token
    * @param catalog
    *           countries' catalog (two-letter country code, which is case-sensitive)
    * @param albumId
    *           the ID of the album to add
    * @return asynchronous result, doesn't return any data except the HTTP Response
    */
   @POST("/v1/me/library/albums")
   @FormUrlEncoded
   Call<Void> addAlbumToLibrary( //
      @Header("Authorization") String authorization, //
      @Field("catalog") String catalog, //
      @Field("id") String albumId);

   /**
    * Deletes an album from the user's library.
    * 
    * @param authorization
    *           the access token
    * @param albumId
    *           the ID of the album to remove
    * @return asynchronous result, doesn't return any data except the HTTP Response
    */
   @DELETE("/v1/me/library/albums/{albumId}")
   Call<Void> removeAlbumFromLibrary( //
      @Header("Authorization") String authorization, //
      @Path("albumId") String albumId);
}
