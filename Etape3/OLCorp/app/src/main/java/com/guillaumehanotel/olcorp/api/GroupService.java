package com.guillaumehanotel.olcorp.api;

import com.guillaumehanotel.olcorp.beans.Group;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GroupService {

    @GET("/pseudo_olcorp_api/webapi/groups")
    Call<List<Group>> getAllGroups();

    @POST("/pseudo_olcorp_api/webapi/groups/organizationunits/{ou_id}")
    Call<Group> createGroup(@Path("ou_id") int ou_id, @Body Group group);

}
