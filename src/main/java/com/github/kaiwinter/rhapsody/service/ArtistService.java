package com.github.kaiwinter.rhapsody.service;

import com.github.kaiwinter.rhapsody.model.ArtistData;
import com.github.kaiwinter.rhapsody.model.BioData;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ArtistService {

	@GET("/v1/artists/{artistId}")
	void getArtist(@Header("Authorization") String authorization, @Query("apiKey") String apiKey, @Query("pretty") boolean pretty,
			@Query("catalog") String catalog, @Path("artistId") String artistId, Callback<ArtistData> callBack);

	@GET("/v1/artists/{artistId}/bio")
	void getBio(@Header("Authorization") String authorization, @Query("apiKey") String apiKey, @Query("pretty") boolean pretty,
			@Query("catalog") String catalog, @Path("artistId") String artistId, Callback<BioData> callBack);
}