import java.util.ArrayList;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

public class ActiveDirectory {

	private Properties properties;
	private DirContext dirContext;
	private SearchControls searchCtls;
	private String domainBase;

	private LDAPConnection connection;
	private UserManager userManager;
	private GroupManager groupManager;
	private OrganizationUnitManager organizationUnitManager;

	public ActiveDirectory(String username, String password, String domainController) {
		properties = new Properties();

		properties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		properties.put(Context.PROVIDER_URL, "LDAP://" + domainController);
		properties.put(Context.SECURITY_PRINCIPAL, username + "@" + domainController);
		properties.put(Context.SECURITY_CREDENTIALS, password);

		// Connection au LDAP
		connection = new LDAPConnection(properties);
		// Récupération du contexte
		dirContext = connection.getDirContext();

		// Base du domaine par défaut pour la recherche
		domainBase = getDomainBase(domainController);

		// initializing search controls
		searchCtls = new SearchControls();
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);

		// Instanciation des managers
		this.userManager = new UserManager(dirContext, searchCtls, domainBase);
		this.groupManager = new GroupManager(dirContext, searchCtls, domainBase);
		this.organizationUnitManager = new OrganizationUnitManager(dirContext, searchCtls, domainBase);

	}

	public ArrayList<User> getUsers() throws Exception {
		return this.userManager.getUsers();
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
