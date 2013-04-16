package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import scala.io.Source
import collection.JavaConverters._

object Import extends Controller {
  
  case class Import (filePath: String)
  
  val importForm = Form[Import] (
        
        mapping (
            "filepath" -> text
        )         
        {
          (filePath) => Import(filePath)
        }
        {
          imp => Some(imp.filePath)
        }
       
    )  
  
  def importFile = Action { implicit request =>
    Ok(views.html.importFile(importForm))
  }  
  
  def processImport = Action(parse.multipartFormData) { implicit request =>
    request.body.file("filePath").map { csvFile =>
      import java.io.File
      val filename = csvFile.filename 
      val contentType = csvFile.contentType
      csvFile.ref.moveTo(new File("/tmp/csv.csv"), true)
      
      var values = Set[String]()
      var rowNo = 0
      var columnNo = 0
      var rows = List[Array[String]]()
      
      for(line <- Source.fromFile("/tmp/csv.csv").getLines()) {
        println(line)
        val rowCells: Array[String] = ((line.split(";")).toArray[String]) // TODO delimiter from form
        rows.++(rowCells)
        values ++= rowCells.toSet
        rowNo += 1
        columnNo = if (rowCells.size > columnNo) rowCells.size else columnNo
    
      }
      
      val gs = controllers.Application.getSessionValue(session)

      gs.cellValues = values.asJava
      gs.rowsNo = rowNo
      gs.columnsNo = columnNo
      gs.matrix = rows.toArray

      controllers.Application.updateSessionValue(gs, session)
      
      Steps.step1Form = Steps.step1Form.fill(Steps.Step1(gs.rowsNo, gs.columnsNo))
      Ok(views.html.step1(Steps.step1Form)).flashing("successKey" -> "import_successful")
       
      
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
    		  "error" -> "Missing file")
    }
  }


}