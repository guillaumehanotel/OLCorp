import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

public class GroupManager extends ResourceManager {

	public GroupManager(DirContext dirContext, SearchControls searchCtls, String domainBase) {
		super(dirContext, searchCtls, domainBase);
	}

	
	
	
}
