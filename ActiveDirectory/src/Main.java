import java.util.ArrayList;

public class Main {

	public static void main(String[] args) throws Exception {
		
		String username = "Administrateur";
		String password = "Passw0rd";
		String domain = "DOMAIN.LOCAL";
		
		ActiveDirectory activeDirectory = new ActiveDirectory(username, password, domain);
		
		
		ArrayList<User> users = activeDirectory.getUsers();
		
		for (User user : users) {
			System.out.println(user);
		}
		

	}

}
