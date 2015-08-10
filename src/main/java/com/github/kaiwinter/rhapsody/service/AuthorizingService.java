package com.github.kaiwinter.rhapsody.service;

import com.github.kaiwinter.rhapsody.model.AccessTokenResponse;
import com.github.kaiwinter.rhapsody.model.PasswordGrant;
import com.github.kaiwinter.rhapsody.model.RefreshToken;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

/**
 * Wrapper for the Rhapsody Authentication REST API.
 * 
 * Access tokens expire regularly, so your application should take steps to refresh them as needed. (Note the expires_in value above, which
 * denotes that the associated token expires in 86399 seconds, or just under 24 hours.) To do so, simply call the access_token service,
 * passing your client secret.
 * 
 * @see https://developer.rhapsody.com/api#authentication
 */
// FIXME KW: rename to Authentication
public interface AuthorizingService {

	/**
	 * Authenticates the user by username/password.
	 * 
	 * @param basicAuthentication
	 *            HTTP basic authentication
	 * @param passwordGrant
	 *            the username/password information
	 * @param callBack
	 *            the callback which is called after the REST call returns
	 */
	@POST("/oauth/token")
	void authorizeByPassword( //
			@Header("Authorization") String basicAuthentication, //
			@Body PasswordGrant passwordGrant, //
			Callback<AccessTokenResponse> callBack);

	/**
	 * Refreshes the access token by sending the refresh token to the server.
	 * 
	 * @param refreshToken
	 *            the refresh token
	 * @param callBack
	 *            the callback which is called after the REST call finished
	 */
	@POST("/oauth/access_token")
	void refreshAuthorization( //
			@Body RefreshToken refreshToken, //
			Callback<AccessTokenResponse> callBack);
}