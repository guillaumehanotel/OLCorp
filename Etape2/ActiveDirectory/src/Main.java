import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String username = "Administrateur";
		String password = "Passw0rd";
		String domain = "DOMAIN.LOCAL";
		
		ActiveDirectory activeDirectory = new ActiveDirectory(username, password, domain);
		
	
		
		/** ORGANIZATION UNIT **/
		
		// printArray(activeDirectory.getOrganizationUnits());
		
		// CREATE ORGANIZATION UNIT
		// activeDirectory.createOrganizationUnit("toto");

		// RENAME ORGANIZATION UNIT
		//activeDirectory.renameOrganizationUnit(3, "lalala");
		
		// DELETE ORGANIZATION UNIT
		//activeDirectory.deleteOrganizationUnit(3);
		
		
		
		
		/** GROUP **/
		
		//printArray(activeDirectory.getGroups());
		
		// CREATE GROUP IN ORGANIZATION UNIT
		//activeDirectory.createGroupInOrganizationUnit(3, "Nanatsu No Taizai");
		
		// RENAME GROUP 
		//activeDirectory.renameGroupInOrganizationUnit(53, "Meliodas");
		
		// DELETE GROUP 
		//activeDirectory.deleteGroup(50);
		
		
		
		
		/** USER **/
		
		//printArray(activeDirectory.getUsers());
		
		// CREATE USER IN GROUP AND IN ORGANIZATION UNIT
		//activeDirectory.createUserInGroupAndInOrganizationUnit(3, 50, "TheBoss", "Guillaume", "Hanotel", "gh@ynov.com", "0685423674");
		
		// MODIFY USER 
		//activeDirectory.modifyUser(5, "King", "Harlequin", "Forest", "kg@gmail.com", "0647158536");
		
		// DELETE USER 
		//activeDirectory.deleteUser(5);
		
		
		
		// ADD USER TO GROUP
		//activeDirectory.addUserToGroup(51, 5);

		// REMOVE USER TO GROUP
		//activeDirectory.removeUserToGroup(51, 5);
		
		
		
	}

	
	public static void printArray(ArrayList<ADEntity> list) {
		for (Object o : list) {
			System.out.println(o);
		}
		System.out.println();
	}
	
	
}
