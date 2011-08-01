package models;

import java.io.Serializable;

import play.data.validation.Min;
import play.data.validation.Max;
import play.data.validation.Required;

public class GenerationSession implements Serializable  {

	@Required
	@Min(value=1, message="Počet řádků musí být větší než 0")
	@Max(value=50, message="Počet řádků nesmí být větší než 50")
	public int rows;

	@Required
	@Min(value=1, message="Počet sloupců musí být větší než 0")
	@Max(value=50, message="Počet sloupců nesmí být větší než 50")
	public int columns;

	@Override
	public String toString() {
		return "GenerationSession [rows=" + rows + ", columns=" + columns + "]";
	}




}
