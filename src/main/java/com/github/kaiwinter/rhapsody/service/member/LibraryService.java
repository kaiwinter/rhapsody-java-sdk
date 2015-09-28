package com.github.kaiwinter.rhapsody.service.member;

import java.util.Collection;

import com.github.kaiwinter.rhapsody.model.AlbumData;
import com.github.kaiwinter.rhapsody.model.AlbumData.Artist;

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
public interface LibraryService {

	/**
	 * Returns a list of all artists in the user's library. Accepts optional parameters limit and offset to select a range of results.
	 *
	 * @param authorization
	 *            the access token
	 * @param pretty
	 *            if <code>true</code> pretty prints the JSON
	 * @param limit
	 *            the number of releases which are loaded, if <code>null</code> the servers default value is used
	 * @param callBack
	 *            callback to which the result is passed
	 */
	@GET("/v1/me/library/artists")
	void loadAllArtistsInLibrary( //
			@Header("Authorization") String authorization, //
			@Query("pretty") boolean pretty, //
			@Query("limit") Integer limit, //
			Callback<Collection<Artist>> callBack);

	/**
	 * Returns a list of albums in a member’s library by the artist. Accepts optional parameters limit and offset to select a range of
	 * results.
	 *
	 * @param authorization
	 *            the access token
	 * @param pretty
	 *            if <code>true</code> pretty prints the JSON
	 * @param artistId
	 *            the ID of the artist to load
	 * @param limit
	 *            the number of releases which are loaded, if <code>null</code> the servers default value is used
	 * @param callBack
	 *            callback to which the result is passed
	 */
	@GET("/v1/me/library/artists/{artistId}/albums")
	void loadAllAlbumsByArtistInLibrary( //
			@Header("Authorization") String authorization, //
			@Query("pretty") boolean pretty, //
			@Path("artistId") String artistId, //
			@Query("limit") Integer limit, //
			Callback<Collection<AlbumData>> callBack);

	/**
	 * Returns a list of albums in a member’s library. Accepts optional parameters limit and offset to select a range of results.
	 * 
	 * @param authorization
	 *            the access token
	 * @param pretty
	 *            if <code>true</code> pretty prints the JSON
	 * @param limit
	 *            the number of releases which are loaded, if <code>null</code> the servers default value is used
	 * @param callBack
	 *            callback to which the result is passed
	 */
	@GET("/v1/me/library/albums")
	void loadAllAlbumsInLibrary( //
			@Header("Authorization") String authorization, //
			@Query("pretty") boolean pretty, //
			@Query("limit") Integer limit, //
			Callback<Collection<AlbumData>> callBack);
}
