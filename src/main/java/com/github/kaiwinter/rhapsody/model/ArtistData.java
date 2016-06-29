package com.github.kaiwinter.rhapsody.model;

/**
 * Data structure which gets filled with the result of a REST call to the Rhapsody API.
 */
public final class ArtistData {
   public String id;
   public String name;
   public Genre genre;

   public static final class Genre {
      public String id;
   }
}
