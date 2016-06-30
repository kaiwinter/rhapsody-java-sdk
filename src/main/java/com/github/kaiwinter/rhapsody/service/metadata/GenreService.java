package com.github.kaiwinter.rhapsody.service.metadata;

import java.util.Collection;

import com.github.kaiwinter.rhapsody.model.AlbumData;
import com.github.kaiwinter.rhapsody.model.GenreData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Wrapper of the Genre REST API.
 * 
 * @see https://developer.rhapsody.com/api#genres
 */
public interface GenreService {

   /**
    * Returns a hierarchical map of all genres and subgenres.
    * 
    * @param apikey
    *           the API key
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param catalog
    *           countries' catalog (two-letter country code, which is case-sensitive)
    * @return asynchronous result
    */
   @GET("/v1/genres")
   Call<Collection<GenreData>> getGenresAsync( //
      @Query("apikey") String apikey, //
      @Query("pretty") boolean pretty, //
      @Query("catalog") String catalog);

   /**
    * Returns a list of all new releases by genre.
    * 
    * @param genreId
    *           the ID of the genre to load new releases
    * @param apikey
    *           the API key
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param catalog
    *           countries' catalog (two-letter country code, which is case-sensitive)
    * @param limit
    *           the number of releases which are loaded, if <code>null</code> the servers default value is used
    * @return asynchronous result
    */
   @GET("/v1/genres/{genreId}/albums/new")
   Call<Collection<AlbumData>> getNewReleasesAsync( //
      @Path("genreId") String genreId, //
      @Query("apikey") String apikey, //
      @Query("pretty") boolean pretty, //
      @Query("catalog") String catalog, //
      @Query("limit") Integer limit);
}