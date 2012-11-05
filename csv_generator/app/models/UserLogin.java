package models;

import play.data.validation.Required;

public class UserLogin {

	@Required(message="username_required")
	public String username;

	@Required(message="password_required")
	public String password;

	public UserLogin(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	
}
