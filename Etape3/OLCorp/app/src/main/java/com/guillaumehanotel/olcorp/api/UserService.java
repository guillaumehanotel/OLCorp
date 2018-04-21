package com.guillaumehanotel.olcorp.api;

import com.guillaumehanotel.olcorp.beans.User;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface UserService {

    @GET("/pseudo_olcorp_api/webapi/users")
    Call<List<User>> getAllUsers();

    @POST("/pseudo_olcorp_api/webapi/users/groups/{group_id}/organizationunits/{ou_id}")
    Call<User> createUser(@Path("group_id") int group_id, @Path("ou_id") int ou_id, @Body User user);


}
