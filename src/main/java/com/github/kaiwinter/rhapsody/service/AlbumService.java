package com.github.kaiwinter.rhapsody.service;

import com.github.kaiwinter.rhapsody.model.AlbumData;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Wrapper of the Album REST API.
 * 
 * @see https://developer.rhapsody.com/api#albums
 */
public interface AlbumService {

	/**
	 * Returns detailed information about a given album, including its tracks.
	 * 
	 * @param authorization
	 *            the access token
	 * @param pretty
	 *            if <code>true</code> pretty prints the JSON
	 * @param catalog
	 *            countries' catalog (two-letter country code, which is case-sensitive)
	 * @param albumId
	 *            the ID of the album to load
	 * @param callBack
	 *            callback to which the result is passed
	 */
	@GET("/v1/albums/{albumId}")
	void getAlbum( //
			@Header("Authorization") String authorization, //
			@Query("pretty") boolean pretty, //
			@Query("catalog") String catalog, //
			@Path("albumId") String albumId, //
			Callback<AlbumData> callBack);
}