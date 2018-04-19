package com.guillaumehanotel.olcorp.api;

import com.guillaumehanotel.olcorp.beans.Group;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GroupService {

    @GET("/pseudo_olcorp_api/webapi/groups")
    Call<List<Group>> getAllGroups();

}
