package com.github.kaiwinter.rhapsody.service;

import java.util.Collection;

import com.github.kaiwinter.rhapsody.model.AlbumData;
import com.github.kaiwinter.rhapsody.model.GenreData;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

public interface GenreService {

	@GET("/v1/genres")
	void getGenres(@Header("Authorization") String authorization, @Query("apiKey") String apiKey, @Query("pretty") boolean pretty,
			@Query("catalog") String catalog, Callback<Collection<GenreData>> callBack);

	@GET("/v1/genres/{genreId}/albums/new")
	void getNewReleases(@Header("Authorization") String authorization, @Query("apiKey") String apiKey, @Query("pretty") boolean pretty,
			@Query("catalog") String catalog, @Path("genreId") String genreId, @Query("limit") int limit,
			Callback<Collection<AlbumData>> callBack);
}