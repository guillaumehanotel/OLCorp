import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

public class OrganizationUnitManager extends ResourceManager {

	public OrganizationUnitManager(DirContext dirContext, SearchControls searchCtls, String domainBase) {
		super(dirContext, searchCtls, domainBase);
		
	}

}
