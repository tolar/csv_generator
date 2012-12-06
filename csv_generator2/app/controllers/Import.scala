package controllers

import play.api.mvc._
import play.api.mvc.Action
import play.api.mvc.Controller
import models.DAO
import models.User
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.beans.XMLDecoder
import java.beans.XMLEncoder
import play.api.cache.Cache
import play.api.Play.current
import models.GenerationSession
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import scala.io.Source
import scala.collection.SortedSet

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
      
      for(line <- Source.fromFile("/tmp/csv.csv").getLines()) {
        println(line)
        values ++= (line.split(";")).toSet // TODO delimiter from form 
        rowNo += 1
        columnNo = if (values.size > columnNo) values.size else columnNo
      }
      
      val gs = controllers.Application.getSessionValue(session)
      
      gs.cellValues ++= values
      gs.rowsNo = rowNo
      gs.columnsNo = columnNo
      
      controllers.Application.updateSessionValue(gs, session)
      
      Ok(views.html.index()).flashing("successKey" -> "import_successful")
    	 
      
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
    		  "error" -> "Missing file")
    }
  }


}