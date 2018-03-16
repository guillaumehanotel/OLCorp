
public class User {

	private String name;
	private String firstname;
	private String lastname;
	private String mail;
	private String telephone;

	
	

	public User(String name, String firstname, String lastname, String mail, String telephone) {
		this.name = name;
		this.firstname = firstname;
		this.lastname = lastname;
		this.mail = mail;
		this.telephone = telephone;
	}
	
	public User(String name) {
		this.name = name;
	}

	public User() {}



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
		return "User [name=" + name + ", firstname=" + firstname + ", lastname=" + lastname + ", mail=" + mail
				+ ", telephone=" + telephone + "]";
	}
	
	
	
	
}
