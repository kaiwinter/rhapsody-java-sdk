package com.github.kaiwinter.rhapsody.persistence.impl;

import com.github.kaiwinter.rhapsody.persistence.AuthorizationStore;
import com.github.kaiwinter.rhapsody.persistence.model.AuthorizationInfo;

/**
 * This implementation doesn't save the {@link AuthorizationInfo}.
 */
public final class TransientAuthorizationStore implements AuthorizationStore {

   @Override
   public AuthorizationInfo loadAuthorizationInfo() {
      AuthorizationInfo authorizationInfo = new AuthorizationInfo();
      return authorizationInfo;
   }

   @Override
   public void saveAuthorizationInfo(AuthorizationInfo authorizationInfo) {
   }

   @Override
   public void clearAuthorization() {
   }
}
