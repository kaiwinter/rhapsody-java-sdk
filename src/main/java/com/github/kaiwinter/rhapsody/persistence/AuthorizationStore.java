package com.github.kaiwinter.rhapsody.persistence;

import com.github.kaiwinter.rhapsody.persistence.model.AuthorizationInfo;

/**
 * Implementations of this class persists the {@link AuthorizationInfo} of a logged in user. This can be used to log in
 * the user automatically after a first successful login.
 */
public interface AuthorizationStore {

   /**
    * Loads the previously saved authentication information. If no values were saved the returned object contains
    * <code>null</code> values.
    * 
    * @return the {@link AuthorizationInfo}
    */
   AuthorizationInfo loadAuthorizationInfo();

   /**
    * Saves the authentication information for later use.
    * 
    * @param authorizationInfo
    *           the {@link AuthorizationInfo}
    */
   void saveAuthorizationInfo(AuthorizationInfo authorizationInfo);

   /**
    * Removes the authentication information from Store.
    */
   void clearAuthorization();
}