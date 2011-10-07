package models;

import play.data.validation.Max;
import play.data.validation.Min;
import play.data.validation.Required;

public class Step1Params {
	
	@Required(message="Počet řádků je povinný")
	@Min(value=1, message="Počet řádků musí být větší než 0")
	@Max(value=50, message="Počet řádků nesmí být větší než 50")
	public String rows;

	@Required(message="Počet sloupců je povinný")
	@Min(value=1, message="Počet sloupců musí být větší než 0")
	@Max(value=50, message="Počet sloupců nesmí být větší než 50")
	public String columns;

	@Override
	public String toString() {
		return "Step1Params [rows=" + rows + ", columns=" + columns + "]";
	}
	
	


}
