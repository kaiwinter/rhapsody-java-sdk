package com.github.kaiwinter.rhapsody.model;

/**
 * Data structure for the authentication request by password against the Rhapsody API.
 */
public final class PasswordGrant {
   public final String username;
   public final String password;
   public final String grant_type = "password";

   public PasswordGrant(String username, String password) {
      this.username = username;
      this.password = password;
   }
}