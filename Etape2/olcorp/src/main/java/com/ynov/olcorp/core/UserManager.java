package com.ynov.olcorp.core;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import com.ynov.olcorp.beans.ADEntity;
import com.ynov.olcorp.beans.Group;
import com.ynov.olcorp.beans.OrganizationUnit;
import com.ynov.olcorp.beans.User;

public class UserManager extends ResourceManager {

	private String baseFilter;
	private String[] returnAttributes = { "sAMAccountName", "name", "givenName", "cn", "mail", "sn",
			"telephoneNumber" };
	private HashMap<String, String> userAttributesMapping;

	public UserManager(DirContext dirContext, SearchControls searchCtls, String domainBase) {
		super(dirContext, searchCtls, domainBase);
		this.baseFilter = "(&(objectCategory=Person)(objectClass=User))";
		this.userAttributesMapping = new HashMap<>();

		userAttributesMapping.put("name", "Name");
		userAttributesMapping.put("givenName", "Firstname");
		userAttributesMapping.put("sn", "Lastname");
		userAttributesMapping.put("mail", "Mail");
		userAttributesMapping.put("telephoneNumber", "Telephone");

	}
	
	public ADEntity removeUserToGroup(Group group, User user) {
		
		String groupDN = group.getDistinguishedName();
		String userDN = user.getDistinguishedName();
		
		int groupNameLength = group.getName().length();
		
		try {
			// Ajout de l'utilisateur au groupe
			BasicAttribute member = new BasicAttribute("member", userDN);
			Attributes attrs = new BasicAttributes();
			attrs.put(member);
			dirContext.modifyAttributes(groupDN, LdapContext.REMOVE_ATTRIBUTE, attrs);
			
			//user.setDistinguishedName("CN=" + user.getName() + groupDN.substring(3+groupNameLength));
			
		}catch (Exception e) {
			e.printStackTrace();
		}	
		return user;
		
	}
	

	public ADEntity addUserToGroup(Group group, User user) {
		String groupDN = group.getDistinguishedName();
		String userDN = user.getDistinguishedName();
		
		try {
			// Ajout de l'utilisateur au groupe
			BasicAttribute member = new BasicAttribute("member", userDN);
			Attributes attrs = new BasicAttributes();
			attrs.put(member);
			dirContext.modifyAttributes(groupDN, LdapContext.ADD_ATTRIBUTE, attrs);
			
			//user.setDistinguishedName("CN=" + user.getName() + "," + groupDN);
			
		}catch (Exception e) {
			e.printStackTrace();
		}	
		return user;
	}
	
	
	
	
	public ADEntity deleteUser(User user) {
		try {
			dirContext.destroySubcontext(user.getDistinguishedName());
			return user;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ADEntity modifyUser(User user, String name, String firstname, String lastname, String mail,
			String telephone) {

		ModificationItem[] modifs = new ModificationItem[4];
		
		Attribute modif0 = new BasicAttribute("givenName", firstname);
		Attribute modif1 = new BasicAttribute("sn", lastname);
		Attribute modif2 = new BasicAttribute("mail", mail);
		Attribute modif3 = new BasicAttribute("telephoneNumber", telephone);
		
		modifs[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, modif0);
		modifs[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, modif1);
		modifs[2] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, modif2);
		modifs[3] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, modif3);
		
		try {

			dirContext.modifyAttributes(user.getDistinguishedName(), modifs);
			
			user.setFirstname(firstname);
			user.setLastname(lastname);
			user.setMail(mail);
			user.setTelephone(telephone);

			// Si le nom principal change
			if (!name.equals(user.getName())) {
				int previousNameLength = user.getName().length();
				String previous_DN = user.getDistinguishedName();
				String new_DN = "CN=" + name + previous_DN.substring(3 + previousNameLength);

				dirContext.rename(previous_DN, new_DN);
				user.setName(name);
				user.setDistinguishedName(new_DN);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return user;
	}

	public ADEntity createUserInGroupAndInOrganizationUnit(OrganizationUnit ou, Group group, User user) {
		String groupDN = group.getDistinguishedName();

		Attributes attributes = new BasicAttributes(true);
		Attribute objclass = new BasicAttribute("objectclass");

		try {

			objclass.add("top");
			objclass.add("person");
			objclass.add("organizationalPerson");
			objclass.add("user");
			attributes.put(objclass);

			attributes.put(new BasicAttribute("givenName", user.getFirstname()));
			attributes.put(new BasicAttribute("sn", user.getLastname()));
			attributes.put(new BasicAttribute("mail", user.getMail()));
			attributes.put(new BasicAttribute("telephoneNumber", user.getTelephone()));

			String name = user.getName();

			String userDN = "CN=" + name + "," + ou.getDistinguishedName();
			dirContext.createSubcontext(userDN, attributes);

			user.setDistinguishedName(userDN);

			// Ajout de l'utilisateur au groupe
			BasicAttribute member = new BasicAttribute("member", userDN);
			Attributes attrs = new BasicAttributes();
			attrs.put(member);
			dirContext.modifyAttributes(groupDN, LdapContext.ADD_ATTRIBUTE, attrs);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return user;

	}

	public ArrayList<ADEntity> getUsers() throws Exception {

		searchCtls.setReturningAttributes(returnAttributes);

		NamingEnumeration<SearchResult> users_found = dirContext.search(domainBase, baseFilter, searchCtls);

		ArrayList<ADEntity> users = new ArrayList<>();

		// Tant qu'il y a des r�sultats dans la recherche
		while (users_found.hasMoreElements()) {
			User user = new User();

			// Chaque �l�ment est un objet SearchResult
			SearchResult result = (SearchResult) users_found.next();
			user.setDistinguishedName(result.getNameInNamespace());

			// On r�cup�re ses attributs
			Attributes attribs = result.getAttributes();

			// si il a des attributs
			if (null != attribs) {
				// On parcourt tous les attributs
				for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();) {
					Attribute atr = (Attribute) ae.next();
					String attributeID = atr.getID();

					// si l'attribut LDAP a une correspondance dans la classe User
					if (userAttributesMapping.containsKey(attributeID)) {

						// On parcourt les valeurs de l'attribut que l'on affiche
						for (Enumeration vals = atr.getAll(); vals.hasMoreElements();) {
							// On r�cup�re le String de l'attribut Objet associ� � l'attribut LDAP
							String userObjectAttribute = userAttributesMapping.get(attributeID);
							try {
								// On 'cr��' le bon setter de la classe User en fct de l'attribut trouv�
								Method set_method = user.getClass().getMethod("set" + userObjectAttribute,
										String.class);
								// Puis on l'appelle
								set_method.invoke(user, vals.nextElement());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}

			users.add(user);
		}

		return users;
	}

}
