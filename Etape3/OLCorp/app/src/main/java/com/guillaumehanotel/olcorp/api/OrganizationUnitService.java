package com.guillaumehanotel.olcorp.api;

import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrganizationUnitService {

    @GET("/pseudo_olcorp_api/webapi/organizationunits")
    Call<List<OrganizationUnit>> getAllOrganizationUnits();

    @POST("/pseudo_olcorp_api/webapi/organizationunits")
    Call<OrganizationUnit> createOrganizationUnit(@Body OrganizationUnit organizationUnit);

    @PUT("/pseudo_olcorp_api/webapi/organizationunits/{ou_id}")
    Call<OrganizationUnit> updateOrganizationUnit(@Path("ou_id") int ou_id, @Body OrganizationUnit organizationUnit);

    @DELETE("/pseudo_olcorp_api/webapi/organizationunits/{ou_id}")
    Call<ResponseBody> deleteOrganizationUnit(@Path("ou_id") int ou_id);

}
