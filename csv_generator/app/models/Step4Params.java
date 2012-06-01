package models;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.Max;
import play.data.validation.Min;
import play.data.validation.Required;
import play.i18n.Messages;

public class Step4Params {

	public static List<Delimiter> delimiters = new ArrayList<Delimiter>();

	static {
		final Delimiter del1 = new Delimiter(";", "; - " + Messages.get("semicolon"));
		final Delimiter del2 = new Delimiter(",", ", - " + Messages.get("comma"));

		delimiters.add(del1);
		delimiters.add(del2);
	}

	@Required(message="delimiter_required")
	public String delimiter;

	@Required(message="file_name_required")
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
