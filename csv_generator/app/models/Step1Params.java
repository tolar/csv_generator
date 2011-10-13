package models;

import play.data.validation.Max;
import play.data.validation.Min;
import play.data.validation.Required;

public class Step1Params {
	
	@Required(message="row_count_required")
	@Min(value=1, message="row_count_greater_than_zero")
	@Max(value=50, message="row_count_less_than_fifty")
	public String rows;

	@Required(message="column_count_required")
	@Min(value=1, message="column_count_greater_than_zero")
	@Max(value=50, message="column_count_less_than_fifty")
	public String columns;

	@Override
	public String toString() {
		return "Step1Params [rows=" + rows + ", columns=" + columns + "]";
	}
	
	


}
