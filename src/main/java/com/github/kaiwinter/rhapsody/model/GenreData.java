package com.github.kaiwinter.rhapsody.model;

import java.util.Collection;

/**
 * Data structure which gets filled with the result of a REST call to the Rhapsody API.
 */
public final class GenreData {
	public String id;
	public String name;
	public String description;
	public Collection<GenreData> subgenres;
}