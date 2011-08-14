package models;

import play.data.validation.Max;
import play.data.validation.Min;
import play.data.validation.Required;

public class Step2Params {
	
	@Required(message="Hodnota buňky nesmí být prázdná")
	public String cellValue;

	@Override
	public String toString() {
		return "Step2Params [cellValue=" + cellValue + "]";
	}
	
	
	

}
