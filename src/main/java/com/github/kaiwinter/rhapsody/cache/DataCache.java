package com.github.kaiwinter.rhapsody.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.kaiwinter.rhapsody.model.AlbumData;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Caches API responses.
 *
 * FIXME KW: implement eviction policy or use library
 */
public final class DataCache {

	private Map<String, Collection<AlbumData>> genre2NewReleases = Collections.synchronizedMap(new HashMap<>());

	/**
	 * Returns the new releases for the given <code>genreId</code>.
	 *
	 * @param genreId
	 *            the ID of the Genre
	 * @return the new releases, or <code>null</code> if not cached.
	 */
	public Collection<AlbumData> getReleasesOfGenre(String genreId) {
		return genre2NewReleases.get(genreId);
	}

	/**
	 * Wraps the given <code>callback</code> in an internal callback which adds the result of the given <code>callback</code> to the cache
	 * and calls the original callback afterwards.
	 *
	 * @param genreId
	 *            the ID of the genre
	 * @param callback
	 *            the original callback
	 * @return the wrapped original callback
	 */
	public Callback<Collection<AlbumData>> getAddNewReleasesToCacheCallback(String genreId, Callback<Collection<AlbumData>> callback) {
		return new CallbackExtension<Collection<AlbumData>>(callback) {

			@Override
			public void successExt(Collection<AlbumData> albums, Response response) {
				genre2NewReleases.put(genreId, albums);
			}
		};
	}

	/**
	 * Wrapper for a {@link Callback} which allows the augmentation of the wrapped callback to call additional methods. Here this is used
	 * for adding an API request response to the cache and afterwards calling the original callback method. First
	 * {@link #successExt(Object, Response)} is called and then {@link #success(Object, Response)} of the original {@link Callback}.
	 * <p>
	 * In case of a failure {@link #failure(RetrofitError)} of the original {@link Callback} gets called.
	 * </p>
	 *
	 * @param <T>
	 *            expected response type
	 */
	private abstract class CallbackExtension<T> implements Callback<T> {
		private final Callback<T> callback;

		/**
		 * Is called after the value was cached.
		 *
		 * @param resultObject
		 *            the result object
		 * @param response
		 *            the retrofit {@link Response}
		 */
		abstract void successExt(T resultObject, Response response);

		/**
		 * Constructs a new {@link CallbackExtension} with the wrapped original {@link Callback}.
		 *
		 * @param callback
		 *            the original {@link Callback}
		 */
		public CallbackExtension(Callback<T> callback) {
			this.callback = callback;
		}

		@Override
		public void success(T resultObject, Response response) {
			successExt(resultObject, response);
			callback.success(resultObject, response);
		}

		@Override
		public void failure(RetrofitError error) {
			callback.failure(error);
		}
	}

}
