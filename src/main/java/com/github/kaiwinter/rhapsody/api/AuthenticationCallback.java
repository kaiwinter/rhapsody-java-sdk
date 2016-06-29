package com.github.kaiwinter.rhapsody.api;

/**
 * Use this callback for the {@link RhapsodySdkWrapper#authorize(String, String, AuthenticationCallback)} and
 * {@link RhapsodySdkWrapper#refreshToken(AuthenticationCallback)} requests.
 */
public interface AuthenticationCallback {

   /**
    * Is called if the authentication request or the token refresh request succeeds.
    */
   default void success() {
   }

   /**
    * Is called if the authentication request or the token refresh request fails.
    *
    * @param status
    *           the HTTP status code
    * @param reason
    *           the status message
    */
   default void failure(int status, String reason) {
   }
}
