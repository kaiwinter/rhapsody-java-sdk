package com.github.kaiwinter.rhapsody.persistence.impl;

import org.junit.Assert;
import org.junit.Test;

import com.github.kaiwinter.rhapsody.persistence.AuthorizationStore;
import com.github.kaiwinter.rhapsody.persistence.model.AuthorizationInfo;

/**
 * Tests for {@link PreferencesAuthorizationStore}.
 */
public final class PreferencesAuthorizationStoreTest {

   /**
    * Tests if the values are stored and cleared correctly.
    */
   @Test
   public void test() {
      AuthorizationStore authorizationStore = new PreferencesAuthorizationStore();
      authorizationStore.clearAuthorization();

      // Assert not set on start
      AuthorizationInfo authorizationInfo = authorizationStore.loadAuthorizationInfo();
      Assert.assertNull(authorizationInfo.accessToken);
      Assert.assertNull(authorizationInfo.refreshToken);
      Assert.assertNull(authorizationInfo.catalog);

      // set values and save
      authorizationInfo.accessToken = "accessToken";
      authorizationInfo.refreshToken = "refreshToken";
      authorizationInfo.catalog = "catalog";
      authorizationStore.saveAuthorizationInfo(authorizationInfo);

      // Reload, assert saved values are persisted
      authorizationInfo = authorizationStore.loadAuthorizationInfo();
      Assert.assertEquals("accessToken", authorizationInfo.accessToken);
      Assert.assertEquals("refreshToken", authorizationInfo.refreshToken);
      Assert.assertEquals("catalog", authorizationInfo.catalog);

      // Clear values, reload and assert they are not set
      authorizationStore.clearAuthorization();
      authorizationInfo = authorizationStore.loadAuthorizationInfo();
      Assert.assertNull(authorizationInfo.accessToken);
      Assert.assertNull(authorizationInfo.refreshToken);
      Assert.assertNull(authorizationInfo.catalog);
   }
}
