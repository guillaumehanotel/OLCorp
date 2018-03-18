import java.util.ArrayList;
import java.util.Enumeration;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class OrganizationUnitManager extends ResourceManager {

	private String baseFilter;
	private String[] returnAttributes = { "name" };

	public OrganizationUnitManager(DirContext dirContext, SearchControls searchCtls, String domainBase) {
		super(dirContext, searchCtls, domainBase);

		this.baseFilter = "(objectClass=organizationalUnit)";
	}
	
	
	/**
	 * Delete OrganizationUnit
	 */
	public ADEntity deleteOrganizationUnit(OrganizationUnit organizationUnit) {
		try {
			dirContext.destroySubcontext(organizationUnit.getDistinguishedName());
			return organizationUnit;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Rename OrganizationUnit
	 */
	public ADEntity renameOrganizationUnit(OrganizationUnit organizationUnit, String newName) {
		int previousNameLength =  organizationUnit.getName().length();
		try {
			String previousDN = organizationUnit.getDistinguishedName();
			String newDN = "OU=" + newName + previousDN.substring(3+previousNameLength);
			
			dirContext.rename(previousDN, newDN);
			organizationUnit.setName(newName);
			organizationUnit.setDistinguishedName(newDN);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return organizationUnit;
	}
	
	
	/**
	 * Create OrganizationUnit
	 */
	public ADEntity createOrganizationUnit(String name) {
		Attributes attributes = new BasicAttributes(true);
		Attribute objclass = new BasicAttribute("objectclass");
		ADEntity organization_unit = null;
		try {

			objclass.add("top");
			objclass.add("organizationalUnit");
			attributes.put(objclass);

			String DN = "OU="+name+",DC=DOMAIN,DC=LOCAL";
		    dirContext.createSubcontext(DN, attributes);

		    organization_unit = new OrganizationUnit(name, DN);
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return organization_unit;
	}

	
	/**
	 * Get OrganizationUnits
	 */
	public ArrayList<ADEntity> getOrganizationUnits() throws Exception {

		searchCtls.setReturningAttributes(returnAttributes);

		NamingEnumeration<SearchResult> ou_found = dirContext.search(domainBase, baseFilter, searchCtls);

		ArrayList<ADEntity> organization_units = new ArrayList<>();

		// Tant qu'il y a des résultats dans la recherche
		while (ou_found.hasMoreElements()) {
			OrganizationUnit organizationUnit = new OrganizationUnit();

			// Chaque élément est un objet SearchResult
			SearchResult result = (SearchResult) ou_found.next();
			organizationUnit.setDistinguishedName(result.getNameInNamespace());
			// On récupère ses attributs
			Attributes attribs = result.getAttributes();

			// si il a des attributs
			if (null != attribs) {
				// On parcourt tous les attributs
				for (NamingEnumeration ae = attribs.getAll(); ae.hasMoreElements();) {
					Attribute atr = (Attribute) ae.next();
					String attributeID = atr.getID();

					// On parcourt les valeurs de l'attribut que l'on affiche
					for (Enumeration vals = atr.getAll(); vals.hasMoreElements();) {
						organizationUnit.setName(String.valueOf(vals.nextElement()));
					}
				}
			}
			organization_units.add(organizationUnit);
		}

		return organization_units;
	}

}
