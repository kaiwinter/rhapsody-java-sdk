package com.github.kaiwinter.rhapsody.model;

import java.util.List;

/**
 * Data structure which gets filled with the result of a REST call to the Rhapsody API.
 */
public final class AlbumData {
	public String id;
	public String name;
	public Integer discCount;
	public Artist artist;
	public Type type;
	public List<String> tags;
	public List<Image> images;
	public List<Track> tracks;
	public Long released;

	public static final class Image {
		public Integer width;
		public Integer height;
		public String url;
	}

	public static final class Artist {
		public String id;
		public String name;
	}

	public static final class Type {
		public Integer id;
		public String name;
	}

	public static final class Track {
		public String id;
		public String name;
		public Integer disc;
		public Artist artist;
		public Album album;
		public Genre genre;
		public String sample;
		public Integer duration;
	}

	public static final class Album {
		public String id;
		public String name;
	}

	public static final class Genre {
		public String id;
	}
}