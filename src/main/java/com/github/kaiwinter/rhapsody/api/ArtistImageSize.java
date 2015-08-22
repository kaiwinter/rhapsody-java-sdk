package com.github.kaiwinter.rhapsody.api;

/**
 * Valid sizes for artist images.
 */
public enum ArtistImageSize {

	/** 70x47 */
	SIZE_70_47("70x47"),

	/** 150x100 */
	SIZE_150_100("150x100"),

	/** 356x237 */
	SIZE_356_237("356x237"),

	/** 633x422 */
	SIZE_633_422("633x422");

	private final String size;

	ArtistImageSize(String size) {
		this.size = size;
	}

	public String getSize() {
		return size;
	}
}
