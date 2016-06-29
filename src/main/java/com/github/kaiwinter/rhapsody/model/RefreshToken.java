package com.github.kaiwinter.rhapsody.model;

/**
 * Data structure for refreshing the users access token by a REST call.
 */
public final class RefreshToken {
   public String client_id;
   public String client_secret;
   public final String response_type = "code";
   public final String grant_type = "refresh_token";
   public String refresh_token;
}