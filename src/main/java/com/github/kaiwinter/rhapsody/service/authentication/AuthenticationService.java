package com.github.kaiwinter.rhapsody.service.authentication;

import com.github.kaiwinter.rhapsody.model.AccessToken;
import com.github.kaiwinter.rhapsody.model.PasswordGrant;
import com.github.kaiwinter.rhapsody.model.RefreshToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Wrapper for the Rhapsody Authentication REST API.
 * <p>
 * Access tokens expire regularly, so your application should take steps to refresh them as needed. (Note the expires_in
 * value above, which denotes that the associated token expires in 86399 seconds, or just under 24 hours.) To do so,
 * simply call the access_token service, passing your client secret.
 * </p>
 * 
 * @see https://developer.rhapsody.com/api#authentication
 */
public interface AuthenticationService {

   /**
    * Authenticates the user by username/password.
    *
    * @param basicAuthentication
    *           HTTP basic authentication
    * @param passwordGrant
    *           the username/password information
    * @return asynchronous result
    */
   @POST("/oauth/token")
   Call<AccessToken> authorizeByPasswordAsync( //
      @Header("Authorization") String basicAuthentication, //
      @Body PasswordGrant passwordGrant);

   /**
    * Refreshes the access token by sending the refresh token to the server.
    *
    * @param refreshToken
    *           the refresh token
    * @return asynchronous result
    */
   @POST("/oauth/access_token")
   Call<AccessToken> refreshAuthorizationAsync( //
      @Body RefreshToken refreshToken);
}