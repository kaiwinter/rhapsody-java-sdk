package com.github.kaiwinter.rhapsody.service.member;

import com.github.kaiwinter.rhapsody.model.AccountData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Wrapper of the Member REST API.
 * 
 * @see https://developer.rhapsody.com/api#member-apis
 */
public interface AccountService {

   /**
    * Returns the member account. Accounts will be returned only for valid members.
    * 
    * @param authorization
    *           the access token
    * @param pretty
    *           if <code>true</code> pretty prints the JSON
    * @return asynchronous result
    */
   @GET("/v1/me/account")
   Call<AccountData> getAccountAsync( //
      @Header("Authorization") String authorization, //
      @Query("pretty") boolean pretty);
}