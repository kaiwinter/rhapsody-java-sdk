package com.github.kaiwinter.rhapsody.service.metadata;

import java.util.Collection;

import com.github.kaiwinter.rhapsody.model.AccountData;
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

	/**
	 * Returns a list of new releases, curated by Rhapsody. This list can be personalized for the user by passing the <code>userId</code>.
	 * The personalization is made by Rhapsody based upon recent listening history.
	 *
	 * <p>
	 * <i>Hint:</i> The <code>userId</code> is the {@link AccountData#id}.
	 * </p>
	 *
	 * @param authorization
	 *            the access token
	 * @param pretty
	 *            if <code>true</code> pretty prints the JSON
	 * @param catalog
	 *            countries' catalog (two-letter country code, which is case-sensitive)
	 * @param userId
	 *            the user ID to get personalized new releases, if <code>null</code>no personalization is made
	 * @param callBack
	 *            callback to which the result is passed
	 */
	@GET("/v1/albums/new")
	void getNewReleases( //
			@Header("Authorization") String authorization, //
			@Query("pretty") boolean pretty, //
			@Query("catalog") String catalog, //
			@Query("guid") String userId, //
			Callback<Collection<AlbumData>> callBack);
}