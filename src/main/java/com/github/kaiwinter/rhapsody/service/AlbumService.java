package com.github.kaiwinter.rhapsody.service;

import com.github.kaiwinter.rhapsody.model.AlbumData;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

public interface AlbumService {

	@GET("/v1/albums/{albumId}")
	void getAlbum(@Header("Authorization") String authorization, @Query("apiKey") String apiKey, @Query("pretty") boolean pretty,
			@Query("catalog") String catalog, @Path("albumId") String albumId, Callback<AlbumData> callBack);
}