import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

public class ResourceManager {

	protected DirContext dirContext;
	protected SearchControls searchCtls;
	protected String domainBase;
	
	public ResourceManager(DirContext dirContext, SearchControls searchCtls, String domainBase) {
		this.dirContext = dirContext;
		this.searchCtls = searchCtls;
		this.domainBase = domainBase;
	}

	

}
