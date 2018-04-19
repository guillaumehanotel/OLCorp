package com.guillaumehanotel.olcorp.api;

import com.guillaumehanotel.olcorp.beans.OrganizationUnit;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface OrganizationUnitService {

    @GET("/pseudo_olcorp_api/webapi/organizationunits")
    Call<List<OrganizationUnit>> getAllOrganizationUnits();
}
