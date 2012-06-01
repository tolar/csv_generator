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

@Entity(name="T_CSV_FEEDBACK")
public class Feedback extends Model {

	@Required(message="feedback_description")
	public String description;
	
	public Date submitted;
	
	public String username;

	public Feedback(String description, String username) {
		super();
		this.description = description;
		this.submitted = new Date();
		this.username = username;
	}
	

}
