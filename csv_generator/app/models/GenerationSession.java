package models;

import java.io.Serializable;

import play.data.validation.Min;
import play.data.validation.Max;
import play.data.validation.Required;

public class GenerationSession implements Serializable  {
	
	@Required
	@Min(value=1, message="Počet řádků musí být větší než 0")
	@Max(value=50, message="Počet řádků nesmí být větší než 50")
	private int rows;
	
	@Required
	@Min(value=1, message="Počet sloupců musí být větší než 0")
	@Max(value=50, message="Počet sloupců nesmí být větší než 50")	
	private int columns;

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}
	
	
	

}
