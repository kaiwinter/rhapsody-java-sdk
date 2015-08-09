package com.github.kaiwinter.rhapsody.model;

public class PasswordGrant {

	public String username;
	public String password;
	public String grant_type = "password";

	public PasswordGrant(String username, String password) {
		this.username = username;
		this.password = password;
	}
}