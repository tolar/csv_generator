package models;

import net.sf.oval.constraint.Length;
import play.data.validation.Max;
import play.data.validation.Min;
import play.data.validation.Required;

public class Step2Params {
	
	@Required(message="Hodnota buňky nesmí být prázdná")
	@Length(max=20, message="Hodnota buňky nesmí být delší než 20 znaků")
	public String cellValue;

	@Override
	public String toString() {
		return "Step2Params [cellValue=" + cellValue + "]";
	}
	
	
	

}
