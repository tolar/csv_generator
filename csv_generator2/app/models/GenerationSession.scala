package models

import scala.collection.{mutable, SortedSet}

case class GenerationSession(rowsNoI: Int, columnsNoI: Int, cellValuesI: SortedSet[String],matrixI: Array[Array[String]], delimiterI: String, filenameI: String) {

  def this() = this(0, 0, mutable.TreeSet[String]()(Ordering[String]), Array.ofDim[String](0, 0), ";", "file.csv" )

  var rowsNo: Int = rowsNoI
  var columnsNo: Int = columnsNoI
  var cellValues: SortedSet[String] = cellValuesI
  var matrix: Array[Array[String]] = matrixI
  var delimiter: String = delimiterI
  var filename: String = filenameI

/*  def apply() = {
    this.rowsNo = 0
    this.columnsNo = 0
    this.cellValues = mutable.TreeSet[String]()(Ordering[String])
    this.matrix =  Array.ofDim[String](rowsNo, columnsNo)
    this.delimiter = ";"
    this.filename = "file.csv"
  }*/

/*  def GenerationSession(rowsNo: Int, columnsNo: Int, cellValues: SortedSet[String],matrix: Array[Array[String]], delimiter: String, filename: String) = {
    this.rowsNo = rowsNo
    this.columnsNo = columnsNo
    this.cellValues = mutable.TreeSet[String]()(Ordering[String])
    this.matrix =  Array.ofDim[String](rowsNo, columnsNo)
    this.delimiter = delimiter
    this.filename = filename
  }*/



  def reallocateMatrix = {
    val newMatrix = Array.ofDim[String](rowsNo, columnsNo)

    if (matrix.length > 0 && matrix(0).length > 0) {

      var rowMin = newMatrix.length
      if (matrix.length < newMatrix.length) {
        rowMin = matrix.length
      }
      var colMin = newMatrix(0).length
      if (matrix(0).length < newMatrix(0).length) {
        colMin = matrix(0).length
      }

      for (i <- 0.to(rowMin - 1)) {
        for (j <- 0.to(colMin - 1)) {
          newMatrix(i)(j) = matrix(i)(j)
        }
      }

    }

    matrix = newMatrix
  }

  def getRows: List[Row] = {
    var rows = List[Row]()
    for (i <- 0.to(rowsNo - 1)) {
      rows = rows :+ Row(matrix(i).toList)
    }
    rows
  }

  def setRows(rows: List[Row]) = {
    for (i <- 0.to(rows.length - 1)) {
      matrix(i) = rows(i).cells.toArray
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

