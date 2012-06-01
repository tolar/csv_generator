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

	@Required(message="username_required")
	@MinSize(value=6, message="username_must_have_six_characters")
	public String username;

	@Required(message="password_required")
	@MinSize(value=6, message="password_must_have_six_characters")
	@Transient
	public String password;

	@Required(message="password_confirmation_required")
	@Equals(value="password", message="password_not_same")
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
