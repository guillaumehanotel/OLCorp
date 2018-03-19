package com.ynov.olcorp.core;
import java.util.Properties;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class LDAPConnection {

	private DirContext dirContext;

	public LDAPConnection(Properties properties) {
		try {
			dirContext = new InitialDirContext(properties);
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

	public DirContext getDirContext() {
		return this.dirContext;
	}

	/**
	 * closes the LDAP connection with Domain controller
	 */
	public void closeLdapConnection() {
		try {
			if (dirContext != null)
				dirContext.close();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}

}
