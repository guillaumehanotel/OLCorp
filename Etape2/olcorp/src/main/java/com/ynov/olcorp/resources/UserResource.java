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
import com.ynov.olcorp.beans.User;
import com.ynov.olcorp.core.ActiveDirectory;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

	
	private ActiveDirectory activeDirectory = ActiveDirectory.getInstance("Administrateur", "Passw0rd", "DOMAIN.LOCAL");
	
	@GET
	public ArrayList<ADEntity> getUsers() throws Exception {
		return activeDirectory.getUsers();
	}
	
	@POST
	@Path("/groups/{group_id}/organizationunits/{ou_id}")
	public User addOUser(@PathParam("group_id") int group_id, @PathParam("ou_id") int organizationUnit_id, User user) throws Exception {
		return (User)activeDirectory.createUserInGroupAndInOrganizationUnit(organizationUnit_id, group_id, user.getName(), user.getFirstname(), user.getLastname(), user.getMail(), user.getTelephone());
	}
	
	
	@PUT
	@Path("/{user_id}")
	public User updateUser(@PathParam("user_id") int user_id, User user) {
		return (User)activeDirectory.modifyUser(user_id, user.getName(), user.getFirstname(), user.getLastname(), user.getMail(), user.getTelephone());
	}
	
	
	@DELETE
	@Path("/{user_id}")
	public void deleteUser(@PathParam("user_id") int user_id) {
		activeDirectory.deleteUser(user_id);
	}

	
	

	@POST
	@PathParam("/{user_id}/groups/{group_id}")
	public void addUserToGroup(@PathParam("user_id") int user_id, @PathParam("group_id") int group_id) {
		activeDirectory.addUserToGroup(group_id, user_id);
	}
	
	
	@DELETE
	@PathParam("/{user_id}/groups/{group_id}")
	public void removeUserToGroup(@PathParam("user_id") int user_id, @PathParam("group_id") int group_id) {
		activeDirectory.removeUserToGroup(group_id, user_id);
	}
	
	
	
	
	
}
