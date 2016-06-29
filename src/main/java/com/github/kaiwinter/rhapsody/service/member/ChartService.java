package com.github.kaiwinter.rhapsody.service.member;

import java.util.List;

import com.github.kaiwinter.rhapsody.model.member.ChartsAlbum;
import com.github.kaiwinter.rhapsody.model.member.ChartsArtist;
import com.github.kaiwinter.rhapsody.model.member.ChartsTrack;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;

/**
 * Wrapper of the Charts REST API.
 *
 * @see https://developer.rhapsody.com/api#charts
 */
public interface ChartService {

   /**
    * The time range to load the charts for.
    */
   public enum RangeEnum {
      /** Last week. */
      week,
      /** Last month. */
      month,
      /** Last year. */
      year,
      /** Alltime. */
      life;
   }

   /**
    * Returns a list of most played tracks, ordered by play count, updated daily. Defaults to limit of 20.
    *
    * @param authorization
    *           the access token
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param limit
    *           the number of releases which are loaded, if <code>null</code> the servers default value is used
    * @param range
    *           the period to consider for the charts
    * @param callBack
    *           callback to which the result is passed
    */
   @GET("/v1/me/charts/tracks")
   void loadTopPlayedTracks( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty, //
      @Query("limit") Integer limit, //
      @Query("range") RangeEnum range, //
      Callback<List<ChartsTrack>> callBack);

   /**
    * Returns a list of most played artists, ordered by play count, updated daily. Defaults to limit of 20.
    * 
    * @param authorization
    *           the access token
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param limit
    *           the number of releases which are loaded, if <code>null</code> the servers default value is used
    * @param range
    *           the period to consider for the charts
    * @param callBack
    *           callback to which the result is passed
    */
   @GET("/v1/me/charts/artists")
   void loadTopPlayedArtists( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty, //
      @Query("limit") Integer limit, //
      @Query("range") RangeEnum range, //
      Callback<List<ChartsArtist>> callBack);

   /**
    * Returns a list of most played albums, ordered by play count, updated daily. Defaults to limit of 20.
    * 
    * @param authorization
    *           the access token
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param limit
    *           the number of releases which are loaded, if <code>null</code> the servers default value is used
    * @param range
    *           the period to consider for the charts
    * @param callBack
    *           callback to which the result is passed
    */
   @GET("/v1/me/charts/albums")
   void loadTopPlayedAlbums( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty, //
      @Query("limit") Integer limit, //
      @Query("range") RangeEnum range, //
      Callback<List<ChartsAlbum>> callBack);
}
