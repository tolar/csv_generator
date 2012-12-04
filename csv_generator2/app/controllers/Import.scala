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
  
  def processImport = Action(parse.multipartFormData) { request =>
    request.body.file("filePath").map { csvFile =>
      import java.io.File
      val filename = csvFile.filename 
      val contentType = csvFile.contentType
      csvFile.ref.moveTo(new File("/tmp/picture"))
      Ok("File uploaded")
    }.getOrElse {
      Redirect(routes.Application.index).flashing(
    		  "error" -> "Missing file")
    }
  }


}