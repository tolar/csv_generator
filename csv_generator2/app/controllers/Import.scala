package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import scala.io.Source
import scala.collection.mutable.ListBuffer
import scala.collection.mutable

object Import extends Controller {

  case class Import(delimiter: String = ";", ignoreString: String = "#")

  val importForm = Form[Import](

    mapping(
      "delimiter" -> text,
      "ignoreString" -> text
    ) {
      (delimiter, ignoreString) => Import(delimiter, ignoreString)
    } {
      imp => Some(imp.delimiter, imp.ignoreString)
    }

  )

  def importFile = Action {
    implicit request =>
      Ok(views.html.importFile(importForm.fill(Import(";", "#"))))
  }

  def processImport = Action(parse.multipartFormData) {
    implicit request =>

    val id : Option[Import] = importForm.bindFromRequest().fold(
      errFrm => None,
      importData => Some(importData)
    )

    request.body.file("filePath").map { csvFile =>
      id.map { importData =>

        import java.io.File
        val filename = csvFile.filename
        val contentType = csvFile.contentType
        csvFile.ref.moveTo(new File("/tmp/csv.csv"), true)

        var values = mutable.TreeSet[String]()(Ordering[String])
        var rowNo = 0
        var columnNo = 0
        var rows = ListBuffer[Array[String]]()

//        var lineCounter: Int = 0

//        Breaks.breakable {
        for (line <- Source.fromFile("/tmp/csv.csv").getLines()) {
          /*            lineCounter += 1
                      if (lineCounter > 1) {
                        Ok(views.html.importFile(importForm)).flashing("errorKey" -> "import_too_many_rows")
                        Breaks.break
                      }*/
          if (!line.trim.startsWith(importData.ignoreString)) {
            val rowCells: Array[String] = ((line.split(importData.delimiter)).toArray[String])
            rows.+=(rowCells)
            values ++= rowCells.filter(v => !v.trim.isEmpty).toSet
            rowNo += 1
            columnNo = if (rowCells.size > columnNo) rowCells.size else columnNo
          }
        }
        //        }

        val gs = controllers.Application.getSessionValue(session)

        gs.cellValues = values
        gs.rowsNo = rowNo
        gs.columnsNo = columnNo

        rows = rows.map(row => row.padTo(columnNo, ""))

        gs.matrix = rows.toArray

        controllers.Application.updateSessionValue(gs, session)

        Steps.step1Form = Steps.step1Form.fill(Steps.Step1(gs.rowsNo, gs.columnsNo))
        Ok(views.html.step1(Steps.step1Form)).flashing("successKey" -> "import_successful")

      }.getOrElse {
        BadRequest("Form binding error.")
      }
    }.getOrElse {
      BadRequest("File not attached.")
    }

  }



}