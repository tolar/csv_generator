package models;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.Max;
import play.data.validation.Min;
import play.data.validation.Required;

public class Step4Params {

	public static List<Delimiter> delimiters = new ArrayList<Delimiter>();

	static {
		final Delimiter del1 = new Delimiter(";", "; - středník");
		final Delimiter del2 = new Delimiter(",", ", - čárka");

		delimiters.add(del1);
		delimiters.add(del2);
	}

	@Required(message="Oddělovač je povinný")
	public String delimiter;

	@Required(message="Jméno souboru je povinné")
	public String fileName;


	static class Delimiter {

		private String value;
		private String description;

		public Delimiter(String value, String description) {
			super();
			this.value = value;
			this.description = description;
		}


	}





}
