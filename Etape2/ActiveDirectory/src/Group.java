
public class Group extends ADEntity{

	
	private static int nbGroup = 1;

	public Group(String name, String distinguishedName) {
		super(nbGroup, name, distinguishedName);
		nbGroup++;
	}
	
	public Group(String name) {
		super(nbGroup, name);
		nbGroup++;
	}

	public Group() {
		super(nbGroup);
		nbGroup++;
	}
	
	@Override
	public String toString() {
		return "Group [id=" + id + ", name=" + name + ", DN=" + distinguishedName + "]";
	}
	
}
