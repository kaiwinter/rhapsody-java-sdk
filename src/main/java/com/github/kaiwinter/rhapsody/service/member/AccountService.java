package com.github.kaiwinter.rhapsody.service.member;

import com.github.kaiwinter.rhapsody.model.AccountData;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Query;

/**
 * Wrapper of the Member REST API.
 * 
 * @see <a href="https://developer.rhapsody.com/api#member-apis">https://developer.rhapsody.com/api#member-apis</a>
 */
public interface AccountService {

   /**
    * Returns the member account. Accounts will be returned only for valid members.
    * 
    * @param authorization
    *           the access token
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @param callBack
    *           callback to which the result is passed
    */
   @GET("/v1/me/account")
   void getAccount( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty, //
      Callback<AccountData> callBack);
}