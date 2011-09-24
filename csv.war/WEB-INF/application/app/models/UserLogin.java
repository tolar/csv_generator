package models;

import play.data.validation.Required;

public class UserLogin {

	@Required(message="Uživatelské jméno je povinné")
	public String username;

	@Required(message="Heslo je povinné")
	public String password;

	public UserLogin(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	
}
