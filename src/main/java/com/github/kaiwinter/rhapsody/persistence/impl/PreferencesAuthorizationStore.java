package com.github.kaiwinter.rhapsody.persistence.impl;

import java.util.prefs.Preferences;

import com.github.kaiwinter.rhapsody.persistence.AuthorizationStore;
import com.github.kaiwinter.rhapsody.persistence.model.AuthorizationInfo;

/**
 * Uses the Java {@link Preferences} mechanism to store the authentication information.
 * 
 * <p>
 * <b>Note: this will not run on Android.</b>
 * </p>
 */
public final class PreferencesAuthorizationStore implements AuthorizationStore {

    private static final String PROPERTY_ACCESS_TOKEN = "access_token";
    private static final String PROPERTY_REFRESH_TOKEN = "refresh_token";
    private static final String PROPERTY_CATALOG = "catalog";

    @Override
    public AuthorizationInfo loadAuthorizationInfo() {
        Preferences preferences = Preferences.userNodeForPackage(getClass());
        AuthorizationInfo authorizationInfo = new AuthorizationInfo();
        authorizationInfo.accessToken = preferences.get(PROPERTY_ACCESS_TOKEN, null);
        authorizationInfo.refreshToken = preferences.get(PROPERTY_REFRESH_TOKEN, null);
        authorizationInfo.catalog = preferences.get(PROPERTY_CATALOG, null);

        return authorizationInfo;
    }

    @Override
    public void saveAuthorizationInfo(AuthorizationInfo authorizationInfo) {
        Preferences userNodeForPackage = Preferences.userNodeForPackage(getClass());
        userNodeForPackage.put(PROPERTY_ACCESS_TOKEN, authorizationInfo.accessToken);
        userNodeForPackage.put(PROPERTY_REFRESH_TOKEN, authorizationInfo.refreshToken);
        userNodeForPackage.put(PROPERTY_CATALOG, authorizationInfo.catalog);
    }

    @Override
    public void clearAuthorization() {
        Preferences preferences = Preferences.userNodeForPackage(getClass());
        preferences.remove(PROPERTY_ACCESS_TOKEN);
        preferences.remove(PROPERTY_REFRESH_TOKEN);
        preferences.remove(PROPERTY_CATALOG);
    }
}
