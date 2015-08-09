package com.github.kaiwinter.rhapsody.model;

public class RefreshToken {
	public String client_id;
	public String client_secret;
	public String response_type = "code";
	public String grant_type = "refresh_token";
	public String refresh_token;
}