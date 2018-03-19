package com.ynov.olcorp.core;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.ynov.olcorp.beans.ADEntity;
import com.ynov.olcorp.beans.Group;
import com.ynov.olcorp.beans.OrganizationUnit;

public class GroupManager extends ResourceManager {

	private String baseFilter;
	private String[] returnAttributes = { "name" };
	
	public GroupManager(DirContext dirContext, SearchControls searchCtls, String domainBase) {
		super(dirContext, searchCtls, domainBase);
		
		this.baseFilter = "(objectClass=group)";
	}

	
	
	
	
	
	
	
	public ADEntity deleteGroup(Group group) {
		try {
			dirContext.destroySubcontext(group.getDistinguishedName());
			return group;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Rename Group
	 */
	public ADEntity renameGroupInOrganizationUnit(Group group, String newName) {
		int previousNameLength = group.getName().length();

		String previous_DN = group.getDistinguishedName();
		String new_DN = "CN=" + newName + previous_DN.substring(3+previousNameLength);
		
		try {
			dirContext.rename(previous_DN, new_DN);
			group.setName(newName);
			group.setDistinguishedName(new_DN);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return group;
	}
	
	
	/**
	 * Create Group
	 */
	public ADEntity createGroupInOrganizationUnit(OrganizationUnit organizationUnit, String name) {
		String organizationUnitDN = organizationUnit.getDistinguishedName();
		
		Attributes attributes = new BasicAttributes(true);
		Attribute objclass = new BasicAttribute("objectclass");
		ADEntity group = null;
		try {

			objclass.add("top");
			objclass.add("group");
			attributes.put(objclass);

			String DN = "CN=" + name + "," + organizationUnitDN;
		    dirContext.createSubcontext(DN, attributes);

		    group = new Group(name, DN);
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return group;
	}
	
	/**
	 * Get Groups
	 */
	public ArrayList<ADEntity> getGroups() throws Exception {

		searchCtls.setReturningAttributes(returnAttributes);

		NamingEnumeration<SearchResult> ou_found = dirContext.search(domainBase, baseFilter, searchCtls);

		ArrayList<ADEntity> groups = new ArrayList<>();
		
		// Tant qu'il y a des r�sultats dans la recherche
		while (ou_found.hasMoreElements()) {
			Group group = new Group();

			// Chaque �l�ment est un objet SearchResult
			SearchResult result = (SearchResult) ou_found.next();
			group.setDistinguishedName(result.getNameInNamespace());
			
			// On r�cup�re ses attributs
			Attributes attribs = result.getAttributes();

			// si il a des attributs
			if (null != attribs) {
				// On parcourt tous les attributs
				for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();) {
					Attribute atr = (Attribute) ae.next();
					String attributeID = atr.getID();

					// On parcourt les valeurs de l'attribut que l'on affiche
					for (Enumeration vals = atr.getAll(); vals.hasMoreElements();) {
						group.setName(String.valueOf(vals.nextElement()));
						//System.out.println(attributeID +": "+ vals.nextElement());
					}
				}
			}
			groups.add(group);
		}

		return groups;
	}
	
	
}
