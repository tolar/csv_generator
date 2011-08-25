package models;

import play.data.validation.Required;
import play.data.validation.*;

public class User {

	@Required(message="Uživatelské jméno je povinné")
	@MinSize(value=6, message="Uživatelské jméno musí mít alespoň 6 znaků")
	public String username;

	@Required(message="Heslo je povinné")
	@MinSize(value=6, message="Heslo musí mít alespoň 6 znaků")
	public String password;


	public String confirmPassword;


}
