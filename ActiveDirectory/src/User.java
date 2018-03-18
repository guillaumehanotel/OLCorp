
public class User extends ADEntity {

	private static int nbUser = 1;
	
	private String firstname;
	private String lastname;
	private String mail;
	private String telephone;


	public User(String name, String firstname, String lastname, String mail, String telephone) {
		super(nbUser, name);		
		nbUser++;

		this.firstname = firstname;
		this.lastname = lastname;
		this.mail = mail;
		this.telephone = telephone;
	}
	
	public User(String name) {
		super(nbUser, name);
		nbUser++;
	}

	public User() {
		super(nbUser);
		nbUser++;
	}

	
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", firstname=" + firstname + ", lastname=" + lastname + ", mail=" + mail
				+ ", telephone=" + telephone + ", DN=" + distinguishedName + "]";
	}
	
	
	
	
}
