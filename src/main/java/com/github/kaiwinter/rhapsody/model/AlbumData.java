package com.github.kaiwinter.rhapsody.model;

import java.util.List;

public class AlbumData {

	public String id;
	public String name;
	public Integer discCount;
	public Artist artist;
	public Type type;
	public List<String> tags;
	public List<Image> images;
	public List<Track> tracks;
	public Long released;

	public static class Image {
		public Integer width;
		public Integer height;
		public String url;
	}

	public static class Artist {
		public String id;
		public String name;
	}

	public static class Type {
		public Integer id;
		public String name;
	}

	public static class Track {
		public String id;
		public String name;
		public Integer disc;
		public Artist artist;
		public Album album;
		public Genre genre;
		public String sample;
		public Integer duration;
	}

	public static class Album {
		public String id;
		public String name;
	}

	public static class Genre {
		public String id;
	}
}