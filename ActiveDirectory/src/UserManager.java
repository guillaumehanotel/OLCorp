import java.lang.reflect.Method;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

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

	public ArrayList<User> getUsers() throws Exception {

		searchCtls.setReturningAttributes(returnAttributes);

		NamingEnumeration<SearchResult> users_found = dirContext.search(domainBase, baseFilter, searchCtls);

		ArrayList<User> users = new ArrayList<>();

		// Tant qu'il y a des résultats dans la recherche
		while (users_found.hasMoreElements()) {
			User user = new User();

			// Chaque élément est un objet SearchResult
			SearchResult result = (SearchResult) users_found.next();
			// On récupère ses attributs
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
							// On récupère le String de l'attribut Objet associé à l'attribut LDAP
							String userObjectAttribute = userAttributesMapping.get(attributeID);
							try {
								// On 'créé' le bon setter de la classe User en fct de l'attribut trouvé
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
