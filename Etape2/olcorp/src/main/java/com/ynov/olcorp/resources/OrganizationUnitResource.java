package com.ynov.olcorp.resources;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ynov.olcorp.beans.ADEntity;
import com.ynov.olcorp.beans.OrganizationUnit;
import com.ynov.olcorp.core.ActiveDirectory;

@Path("/organizationunits")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrganizationUnitResource {

	private ActiveDirectory activeDirectory = ActiveDirectory.getInstance("Administrateur", "Passw0rd", "DOMAIN.LOCAL");
	
	@GET
	public ArrayList<ADEntity> getOrganizationUnits() throws Exception {
		return activeDirectory.getOrganizationUnits();
	}
	
	@POST
	public OrganizationUnit addOrganizationUnit(OrganizationUnit organizationUnit) throws Exception {
		return (OrganizationUnit)activeDirectory.createOrganizationUnit(organizationUnit.getName());
	}
	
	@PUT
	@Path("/{ou_id}")
	public OrganizationUnit updateOrganizationUnit(@PathParam("ou_id") int organizationUnit_id, OrganizationUnit organizationUnit) throws Exception {
		return (OrganizationUnit)activeDirectory.renameOrganizationUnit(organizationUnit_id, organizationUnit.getName());
	}
	
	@DELETE 
	@Path("/{ou_id}")
	public void deleteOrganizationUnits(@PathParam("ou_id") int ou_id) {
		activeDirectory.deleteOrganizationUnit(ou_id);
	}
	
	
	
	
	
}
