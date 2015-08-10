package com.github.kaiwinter.rhapsody.service;

import com.github.kaiwinter.rhapsody.model.ArtistData;
import com.github.kaiwinter.rhapsody.model.BioData;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;
import retrofit.http.Query;

public interface ArtistService {

	/**
	 * Returns a given artist's name, ID and primary genre.
	 * 
	 * @param authorization
	 *            the access token
	 * @param pretty
	 *            if <code>true</code> pretty prints the JSON
	 * @param catalog
	 *            countries' catalog (two-letter country code, which is case-sensitive)
	 * @param artistId
	 *            the ID of the artist to load
	 * @param callBack
	 *            callback to which the result is passed
	 */
	@GET("/v1/artists/{artistId}")
	void getArtist( //
			@Header("Authorization") String authorization, //
			@Query("pretty") boolean pretty, //
			@Query("catalog") String catalog, //
			@Path("artistId") String artistId, //
			Callback<ArtistData> callBack);

	/**
	 * Returns biographical info for a given artist, including up to five short "blurbs" written by our editorial staff.
	 * 
	 * @param authorization
	 *            the access token
	 * @param pretty
	 *            if <code>true</code> pretty prints the JSON
	 * @param catalog
	 *            countries' catalog (two-letter country code, which is case-sensitive)
	 * @param artistId
	 *            the ID of the artist to load
	 * @param callBack
	 *            callback to which the result is passed
	 */
	@GET("/v1/artists/{artistId}/bio")
	void getBio( //
			@Header("Authorization") String authorization, //
			@Query("pretty") boolean pretty, //
			@Query("catalog") String catalog, //
			@Path("artistId") String artistId, //
			Callback<BioData> callBack);
}