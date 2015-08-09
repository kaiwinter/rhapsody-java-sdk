package com.github.kaiwinter.rhapsody.service;

import com.github.kaiwinter.rhapsody.model.AccessTokenResponse;
import com.github.kaiwinter.rhapsody.model.PasswordGrant;
import com.github.kaiwinter.rhapsody.model.RefreshToken;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.POST;

public interface AuthorizingService {

	@POST("/oauth/token")
	void authorizeByPassword(@Header("Authorization") String authorization, @Body PasswordGrant body,
			Callback<AccessTokenResponse> callBack);

	@POST("/oauth/access_token")
	void refreshAuthorization(@Body RefreshToken refreshToken, Callback<AccessTokenResponse> callBack);
}