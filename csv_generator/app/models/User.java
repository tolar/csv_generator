package models;

import play.data.validation.*;

import javax.persistence.*;

import org.bouncycastle.jce.provider.JCEMac.MD5;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import play.data.binding.*;

import play.db.jpa.*;
import play.libs.Codec;

@Entity(name="T_CSV_USER")
public class User extends Model {

	@Required(message="Uživatelské jméno je povinné")
	@MinSize(value=6, message="Uživatelské jméno musí mít alespoň 6 znaků")
	public String username;

	@Required(message="Heslo je povinné")
	@MinSize(value=6, message="Heslo musí mít alespoň 6 znaků")
	@Transient
	public String password;

	@Required(message="Druhé zadání hesla je povinné")
	@Equals(value="password", message="Zadaná hesla nejsou stejná")
	@Transient
	public String confirmPassword;
	
	@Column(name="PASSWORD_HASH")
	public String passwordHash;
	
	@Column(name="GENERATION_SESSION")
	public String generationSession;


	public User(String username, String password, String confirmPassword) {
		super();
		this.username = username;
		this.password = password;
		this.confirmPassword = confirmPassword;
	}


	public void generatePassHash() {
		this.passwordHash = Codec.hexMD5(this.password);
	}

	

}
