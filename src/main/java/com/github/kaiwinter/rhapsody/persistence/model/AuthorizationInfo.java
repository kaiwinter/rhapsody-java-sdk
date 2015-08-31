package com.github.kaiwinter.rhapsody.persistence.model;

import com.github.kaiwinter.rhapsody.model.AccessToken;

/**
 * Authorization information which are used to authenticate the user without username/password.
 */
public final class AuthorizationInfo {

	public String accessToken;
	public String refreshToken;
	public String catalog;

	/**
	 * Constructs an empty {@link AuthorizationInfo}.
	 */
	public AuthorizationInfo() {
	}

	/**
	 * Constructs a new {@link AuthorizationInfo} from a {@link AccessToken}.
	 * 
	 * @param accessTokenResponse
	 *            the {@link AccessToken}
	 */
	public AuthorizationInfo(AccessToken accessTokenResponse) {
		this.accessToken = accessTokenResponse.access_token;
		this.refreshToken = accessTokenResponse.refresh_token;
		this.catalog = accessTokenResponse.catalog;
	}
}
