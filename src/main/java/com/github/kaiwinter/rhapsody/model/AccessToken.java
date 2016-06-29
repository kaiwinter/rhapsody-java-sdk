package com.github.kaiwinter.rhapsody.model;

/**
 * Data structure which gets filled with the result of a REST call to the Rhapsody API.
 */
public final class AccessToken {
   public String access_token;
   public String refresh_token;
   public String catalog; // not contained in token refresh call!
}