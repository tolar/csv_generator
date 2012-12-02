package models

import scala.collection.SortedSet

class GenerationSession {
  
  var rowsNo: Int = 0;
  var columnsNo: Int = 0;
  var cellValues: SortedSet[String] = SortedSet[String]()(Ordering[String])
  var matrix: Array[Array[String]] = Array.ofDim[String](rowsNo, columnsNo);
  var delimiter: String = ";";
  var filename: String = "file.csv";
  
  def reallocateMatrix = {
    val newMatrix = Array.ofDim[String](rowsNo, columnsNo)
  
	if (matrix.length > 0 && matrix(0).length > 0) {
	    
	    var rowMin = newMatrix.length
	    if (matrix.length < newMatrix.length) {
	      rowMin = matrix.length 
	    }
	    var colMin = newMatrix(0).length
	    if (matrix(0).length < newMatrix(0).length) {
	    	colMin =  matrix(0).length
	    }
	    
	    for (i <- 0.to(rowMin-1))  {
	      for (j <- 0.to(colMin-1)) {
	        newMatrix(i)(j) = matrix(i)(j)
	      }
	    }
    
    }
    
    matrix = newMatrix
  }
  
  def getRows: List[Row] = {
    var rows = List[Row]()
    for (i <- 0.to(rowsNo-1)) {
      rows = rows :+ Row(matrix(i).toList)  
    }
    rows
  }
  
  def setRows(rows: List[Row]) = {
    for (i <- 0.to(rows.length-1)) {
      matrix(i) = rows(i).cell.toArray
    }
  }
  
  
  /*
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

*/
}