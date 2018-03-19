
public class OrganizationUnit extends ADEntity {

	private static int nbOU = 1;

	public OrganizationUnit(String name, String distinguishedName) {
		super(nbOU, name, distinguishedName);
		nbOU++;
	}
	
	public OrganizationUnit(String name) {
		super(nbOU, name);
		nbOU++;
	}

	public OrganizationUnit() {
		super(nbOU);
		nbOU++;
	}

	@Override
	public String toString() {
		return "OrganizationUnit [id=" + id + ", name=" + name + ", DN=" + distinguishedName + "]";
	}
	
	
	
}
