package com.guillaumehanotel.olcorp.api;

import com.guillaumehanotel.olcorp.beans.User;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


public interface UserService {

    @GET("/pseudo_olcorp_api/webapi/users")
    Call<List<User>> getAllUsers();



}
