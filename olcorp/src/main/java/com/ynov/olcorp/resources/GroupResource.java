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
import com.ynov.olcorp.beans.Group;
import com.ynov.olcorp.beans.OrganizationUnit;
import com.ynov.olcorp.core.ActiveDirectory;


@Path("/groups")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GroupResource {

	private ActiveDirectory activeDirectory = ActiveDirectory.getInstance("Administrateur", "Passw0rd", "DOMAIN.LOCAL");
	
	@GET
	public ArrayList<ADEntity> getGroups() throws Exception {
		return activeDirectory.getGroups();
	}

	@POST
	@Path("/organizationunits/{ou_id}")
	public Group addOrganizationUnit(@PathParam("ou_id") int organizationUnit_id, Group group) throws Exception {
		return (Group)activeDirectory.createGroupInOrganizationUnit(organizationUnit_id, group.getName());
	}
	
	@PUT
	@Path("/{group_id}")
	public Group updateGroup(@PathParam("group_id") int group_id, Group group) {
		return (Group)activeDirectory.renameGroupInOrganizationUnit(group_id, group.getName());
	}
	
	@DELETE
	@Path("/{group_id}")
	public void deleteGroup(@PathParam("group_id") int group_id) {
		activeDirectory.deleteGroup(group_id);
	}
	
	
	
}
