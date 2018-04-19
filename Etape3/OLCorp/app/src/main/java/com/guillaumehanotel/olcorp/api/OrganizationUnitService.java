package com.guillaumehanotel.olcorp.api;

import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface OrganizationUnitService {

    @GET("/pseudo_olcorp_api/webapi/organizationunits")
    Call<List<OrganizationUnit>> getAllOrganizationUnits();

    @POST("/pseudo_olcorp_api/webapi/organizationunits")
    Call<OrganizationUnit> createOrganizationUnit(@Body OrganizationUnit organizationUnit);


}
