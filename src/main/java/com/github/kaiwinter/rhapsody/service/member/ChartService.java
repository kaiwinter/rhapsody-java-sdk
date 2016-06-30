package com.github.kaiwinter.rhapsody.service.member;

import java.util.List;

import com.github.kaiwinter.rhapsody.model.member.ChartsAlbum;
import com.github.kaiwinter.rhapsody.model.member.ChartsArtist;
import com.github.kaiwinter.rhapsody.model.member.ChartsTrack;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

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
    * @return asynchronous result
    */
   @GET("/v1/me/charts/tracks")
   Call<List<ChartsTrack>> loadTopPlayedTracksAsync( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty, //
      @Query("limit") Integer limit, //
      @Query("range") RangeEnum range);

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
    * @return asynchronous result
    */
   @GET("/v1/me/charts/artists")
   Call<List<ChartsArtist>> loadTopPlayedArtistsAsync( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty, //
      @Query("limit") Integer limit, //
      @Query("range") RangeEnum range);

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
    * @return asynchronous result
    */
   @GET("/v1/me/charts/albums")
   Call<List<ChartsAlbum>> loadTopPlayedAlbumsAsync( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty, //
      @Query("limit") Integer limit, //
      @Query("range") RangeEnum range);
}
