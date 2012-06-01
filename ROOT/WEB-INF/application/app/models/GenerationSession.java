package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import play.data.validation.Min;
import play.data.validation.Max;
import play.data.validation.Required;

public class GenerationSession implements Serializable  {

	public String rows;

	public String columns;

	public Set<String> cellValues = new TreeSet<String>();

	public String[][] matrix;

	public String delimiter = ";";

	public String fileName = "file.csv";

	public GenerationSession() {
		super();
	}

	public Step1Params getStep1Params() {
		Step1Params params = new Step1Params();
		params.columns = columns;
		params.rows = rows;
		return params;
	}

	public Step2Params getStep2Params() {
		Step2Params params = new Step2Params();
		return params;
	}

	public Step4Params getStep4Params() {
		Step4Params params = new Step4Params();
		params.delimiter = delimiter;
		params.fileName = fileName;
		return params;
	}

	public void reallocateMatrix() {

		String[][] newMatrix = new String[Integer.parseInt(rows)][Integer.parseInt(columns)];

		if (matrix != null) {
			int rowMin = matrix.length < newMatrix.length ? matrix.length : newMatrix.length;
			int colMin = matrix[0].length < newMatrix[0].length ? matrix[0].length : newMatrix[0].length;
			for (int i = 0; i < rowMin; i++) {
				for (int j = 0; j < colMin; j++) {
					newMatrix[i][j] = matrix[i][j];
				}
			}
		}

		matrix = newMatrix;

	}

	@Override
	public String toString() {
		return "GenerationSession [rows=" + rows + ", columns=" + columns
				+ ", cellValues=" + cellValues + "]";
	}






}
