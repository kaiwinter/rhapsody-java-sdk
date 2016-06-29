package com.github.kaiwinter.rhapsody.service.member;

import java.util.Collection;

import com.github.kaiwinter.rhapsody.model.AlbumData;
import com.github.kaiwinter.rhapsody.model.AlbumData.Artist;

import retrofit.Callback;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

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
    * @param callBack
    *           callback to which the result is passed
    */
   @GET("/v1/me/library/artists")
   void loadAllArtistsInLibrary( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty, //
      @Query("limit") Integer limit, //
      Callback<Collection<Artist>> callBack);

   /**
    * Returns a list of albums in a member’s library by the artist. Accepts optional parameters limit and offset to
    * select a range of results.
    *
    * @param authorization
    *           the access token
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param artistId
    *           the ID of the artist to load
    * @param limit
    *           the number of releases which are loaded, if <code>null</code> the servers default value is used
    * @param callBack
    *           callback to which the result is passed
    */
   @GET("/v1/me/library/artists/{artistId}/albums")
   void loadAllAlbumsByArtistInLibrary( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty, //
      @Path("artistId") String artistId, //
      @Query("limit") Integer limit, //
      Callback<Collection<AlbumData>> callBack);

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
    * @param callBack
    *           callback to which the result is passed
    */
   @GET("/v1/me/library/albums")
   void loadAllAlbumsInLibrary( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty, //
      @Query("limit") Integer limit, //
      Callback<Collection<AlbumData>> callBack);

   /**
    * Adds an album to the user's library.
    * 
    * @param authorization
    *           the access token
    * @param catalog
    *           countries' catalog (two-letter country code, which is case-sensitive)
    * @param albumId
    *           the ID of the album to add
    * @param callBack
    *           doesn't return any data except the HTTP Response
    */
   @POST("/v1/me/library/albums")
   @FormUrlEncoded
   void addAlbumToLibrary( //
      @Header("Authorization") String authorization, //
      @Field("catalog") String catalog, //
      @Field("id") String albumId, //
      Callback<Void> callBack);

   /**
    * Deletes an album from the user's library.
    * 
    * @param authorization
    *           the access token
    * @param albumId
    *           the ID of the album to remove
    * @param callBack
    *           doesn't return any data except the HTTP Response
    */
   @DELETE("/v1/me/library/albums/{albumId}")
   void removeAlbumFromLibrary( //
      @Header("Authorization") String authorization, //
      @Path("albumId") String albumId, //
      Callback<Void> callBack);
}
