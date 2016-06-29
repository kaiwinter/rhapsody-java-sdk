package com.github.kaiwinter.rhapsody.persistence.model;

/**
 * Authorization information which are used to authenticate the user without username/password.
 */
public final class AuthorizationInfo {

   public String accessToken;
   public String refreshToken;
   public String catalog;
}
