package models;

import play.data.validation.Max;
import play.data.validation.Min;
import play.data.validation.Required;

public class Step4Params {
	
	@Required(message="Oddělovač je povinný")
	public String delimiter;

	@Required(message="Jméno souboru je povinné")
	public String fileName;


	
	


}
