package com.ynov.olcorp.core;
import java.util.ArrayList;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

import com.ynov.olcorp.beans.ADEntity;
import com.ynov.olcorp.beans.Group;
import com.ynov.olcorp.beans.OrganizationUnit;
import com.ynov.olcorp.beans.User;

public class ActiveDirectory {

	private Properties properties;
	private DirContext dirContext;
	private SearchControls searchCtls;
	private String domainBase;

	private LDAPConnection connection;
	private UserManager userManager;
	private GroupManager groupManager;
	private OrganizationUnitManager organizationUnitManager;
	
	private ArrayList<ADEntity> users;
	private ArrayList<ADEntity> groups;
	private ArrayList<ADEntity> organizationUnits;
	
	
	private static ActiveDirectory instance;

	public static ActiveDirectory getInstance(String username, String password, String domainController) {
		if(instance == null) {
			try {
				instance = new ActiveDirectory(username, password, domainController);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	
	
	private ActiveDirectory(String username, String password, String domainController) throws Exception {
		properties = new Properties();

		properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, "LDAP://" + domainController);
		properties.put(Context.SECURITY_PRINCIPAL, username + "@" + domainController);
		properties.put(Context.SECURITY_CREDENTIALS, password);

		// Connection au LDAP
		connection = new LDAPConnection(properties);
		// R�cup�ration du contexte
		dirContext = connection.getDirContext();

		// Base du domaine par d�faut pour la recherche
		domainBase = getDomainBase(domainController);

		// initializing search controls
		searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		// Instanciation des managers
		this.userManager = new UserManager(dirContext, searchCtls, domainBase);
		this.groupManager = new GroupManager(dirContext, searchCtls, domainBase);
		this.organizationUnitManager = new OrganizationUnitManager(dirContext, searchCtls, domainBase);

		this.users = this.userManager.getUsers();
		this.groups = this.groupManager.getGroups();
		this.organizationUnits = this.organizationUnitManager.getOrganizationUnits();
	}

	

	
	// CRUD ORGANIZATION UNIT
	public ArrayList<ADEntity> getOrganizationUnits() throws Exception {
		return this.organizationUnits;
	}
	public ADEntity createOrganizationUnit(String name)throws Exception {
		ADEntity ou =  this.organizationUnitManager.createOrganizationUnit(name);
		this.organizationUnits.add(ou);
		return ou;
	}
	public ADEntity renameOrganizationUnit(int id, String name) {
		OrganizationUnit organizationUnit = this.getOrganizationUnitById(id);
		return this.organizationUnitManager.renameOrganizationUnit(organizationUnit, name);
	}
	public void deleteOrganizationUnit(int id) {
		OrganizationUnit organizationUnit = this.getOrganizationUnitById(id);
		this.organizationUnits.remove(this.organizationUnitManager.deleteOrganizationUnit(organizationUnit));
	}
	
	
	// CRUD GROUP
	public ArrayList<ADEntity> getGroups() throws Exception {
		return this.groups;
	}
	public ADEntity createGroupInOrganizationUnit(int organizationUnit_id, String name) {
		OrganizationUnit organizationUnit = this.getOrganizationUnitById(organizationUnit_id);
		Group group = (Group)this.groupManager.createGroupInOrganizationUnit(organizationUnit, name);
		this.groups.add(group);
		return group;
	}
	public ADEntity renameGroupInOrganizationUnit(int group_id, String name) {
		Group group = this.getGroupById(group_id);
		return this.groupManager.renameGroupInOrganizationUnit(group, name);
	}
	public void deleteGroup(int id) {
		Group group = this.getGroupById(id);
		this.groups.remove(this.groupManager.deleteGroup(group));
	}
	
	
	
	// CRUD USER 
	public ArrayList<ADEntity> getUsers() throws Exception {
		return this.users;
	}
	public ADEntity createUserInGroupAndInOrganizationUnit(int ou_id, int group_id, String name, String firstname, String lastname, String mail, String telephone) {
		OrganizationUnit ou = this.getOrganizationUnitById(ou_id);
		Group group = this.getGroupById(group_id);
		User user = new User(name, firstname, lastname, mail, telephone);
		this.users.add((User)this.userManager.createUserInGroupAndInOrganizationUnit(ou, group, user));
		return user;
	}
	public ADEntity modifyUser(int user_id, String name, String firstname, String lastname, String mail, String telephone) {
		User user = this.getUserById(user_id);
		return this.userManager.modifyUser(user, name, firstname, lastname, mail, telephone);
	}
	
	public void deleteUser(int id) {
		User user = this.getUserById(id);
		this.users.remove(this.userManager.deleteUser(user));
	}
	
	
	public ADEntity addUserToGroup(int group_id, int user_id) {
		Group group = this.getGroupById(group_id);
		User user = this.getUserById(user_id);
		return this.userManager.addUserToGroup(group, user);
	}
	
	public ADEntity removeUserToGroup(int group_id, int user_id) {
		Group group = this.getGroupById(group_id);
		User user = this.getUserById(user_id);
		return this.userManager.removeUserToGroup(group, user);
	}
	
	
	
	
	
	
	public OrganizationUnit getOrganizationUnitById(int id) {
		for (ADEntity adEntity : this.organizationUnits) 
			if(adEntity.getId() == id) 
				return (OrganizationUnit)adEntity;
		return null;
	}
	public Group getGroupById(int id) {
		for (ADEntity adEntity : this.groups) 
			if(adEntity.getId() == id) 
				return (Group)adEntity;
		return null;
	}
	public User getUserById(int id) {
		for (ADEntity adEntity : this.users) 
			if(adEntity.getId() == id) 
				return (User)adEntity;
		return null;
	}
	


	/**
	 * creating a domain base value from domain controller name
	 */
	private static String getDomainBase(String base) {
		char[] namePair = base.toUpperCase().toCharArray();
		String dn = "DC=";
		for (int i = 0; i < namePair.length; i++) {
			if (namePair[i] == '.') {
				dn += ",DC=" + namePair[++i];
			} else {
				dn += namePair[i];
			}
		}
		return dn;
	}
	
	public void closeConnecton() {
		this.connection.closeLdapConnection();
	}

}
