package com.github.kaiwinter.rhapsody.service.metadata;

import java.util.Collection;

import com.github.kaiwinter.rhapsody.model.AlbumData;
import com.github.kaiwinter.rhapsody.model.ArtistData;
import com.github.kaiwinter.rhapsody.model.BioData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Wrapper of the Artist REST API.
 *
 * @see https://developer.rhapsody.com/api#artists
 */
public interface ArtistService {

   /**
    * Returns a given artist's name, ID and primary genre.
    * 
    * @param artistId
    *           the ID of the artist to load
    * @param apikey
    *           the API key
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param catalog
    *           countries' catalog (two-letter country code, which is case-sensitive)
    * @return asynchronous result
    */
   @GET("/v1/artists/{artistId}")
   Call<ArtistData> getArtist( //
      @Path("artistId") String artistId, //
      @Query("apikey") String apikey, //
      @Query("pretty") boolean pretty, //
      @Query("catalog") String catalog);

   /**
    * Returns biographical info for a given artist, including up to five short "blurbs" written by our editorial staff.
    * 
    * @param artistId
    *           the ID of the artist to load
    * @param apikey
    *           the API key
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param catalog
    *           countries' catalog (two-letter country code, which is case-sensitive)
    * @return asynchronous result
    */
   @GET("/v1/artists/{artistId}/bio")
   Call<BioData> getBio( //
      @Path("artistId") String artistId, //
      @Query("apikey") String apikey, //
      @Query("pretty") boolean pretty, //
      @Query("catalog") String catalog);

   /**
    * Returns a list of an artist's new releases (if any), updated weekly.
    * 
    * @param artistId
    *           the ID of the artist to load
    * @param apikey
    *           the API key
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param catalog
    *           countries' catalog (two-letter country code, which is case-sensitive)
    * @param limit
    *           the number of releases which are loaded, if <code>null</code> the servers default value is used
    * @return a list of an artist's new releases
    */
   @GET("/v1/artists/{artistId}/albums/new")
   Call<Collection<AlbumData>> getNewReleases( //
      @Path("artistId") String artistId, //
      @Query("apikey") String apikey, //
      @Query("pretty") boolean pretty, //
      @Query("catalog") String catalog, //
      @Query("limit") Integer limit);
}