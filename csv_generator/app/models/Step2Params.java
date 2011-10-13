package models;

import net.sf.oval.constraint.Length;
import play.data.validation.Max;
import play.data.validation.Min;
import play.data.validation.Required;

public class Step2Params {
	
	@Required(message="cell_value_must_not_be_empty")
	@Length(max=20, message="cell_value_must_not_be_longer_than_twenty")
	public String cellValue;

	@Override
	public String toString() {
		return "Step2Params [cellValue=" + cellValue + "]";
	}
	
	
	

}
