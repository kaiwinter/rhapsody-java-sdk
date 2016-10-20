package com.github.kaiwinter.rhapsody.persistence.impl;

import java.util.prefs.Preferences;

import com.github.kaiwinter.rhapsody.persistence.AuthorizationStore;
import com.github.kaiwinter.rhapsody.persistence.model.AuthorizationInfo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Uses the Java {@link Preferences} mechanism to store the authentication information.
 * 
 * <p>
 * <b>Note: this will only run on Android.</b>
 * </p>
 */
public final class SharedPreferencesAuthorizationStore implements AuthorizationStore {
   private static final String SHARED_PREFERENCE_NAME = "rhapsodysdk";

   private static final String PROPERTY_ACCESS_TOKEN = "access_token";
   private static final String PROPERTY_REFRESH_TOKEN = "refresh_token";
   private static final String PROPERTY_CATALOG = "catalog";
   private Context context;

   /**
    * Constructs a new {@link AuthorizationStore} which stores the user login in the Android {@link SharedPreferences}.
    * 
    * @param context
    *           the {@link Context}
    */
   public SharedPreferencesAuthorizationStore(Context context) {
      this.context = context;
   }

   @Override
   public AuthorizationInfo loadAuthorizationInfo() {
      SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
      AuthorizationInfo authorizationInfo = new AuthorizationInfo();
      authorizationInfo.accessToken = sharedPreferences.getString(PROPERTY_ACCESS_TOKEN, null);
      authorizationInfo.refreshToken = sharedPreferences.getString(PROPERTY_REFRESH_TOKEN, null);
      authorizationInfo.catalog = sharedPreferences.getString(PROPERTY_CATALOG, null);
      return authorizationInfo;
   }

   @Override
   public void saveAuthorizationInfo(AuthorizationInfo authorizationInfo) {
      SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
      Editor editor = sharedPreferences.edit();
      editor.putString(PROPERTY_ACCESS_TOKEN, authorizationInfo.accessToken);
      editor.putString(PROPERTY_REFRESH_TOKEN, authorizationInfo.refreshToken);
      editor.putString(PROPERTY_CATALOG, authorizationInfo.catalog);
      editor.apply();
   }

   @Override
   public void clearAuthorization() {
      SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
      Editor editor = sharedPreferences.edit();
      editor.remove(PROPERTY_ACCESS_TOKEN);
      editor.remove(PROPERTY_REFRESH_TOKEN);
      editor.remove(PROPERTY_CATALOG);
      editor.apply();
   }
}
