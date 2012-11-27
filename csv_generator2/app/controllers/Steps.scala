package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.data.Forms._
import play.api.data.Form
import scala.collection._

object Steps extends Controller {
  
  case class Step1 (
    rows: Int,
    columns: Int
  )
  
  val step1Form = Form[Step1] (
        mapping (
            "rows" -> number(min=1, max=50),
            "columns" -> number(min=1, max=50)  
        ) 
        
        {
          (rows, columns) => Step1(rows, columns)
        }
        {
          step1 => Some(step1.rows, step1.columns)
        }
  )
  
  def step1 = Action { implicit request =>
    Ok(views.html.step1(step1Form))
  } 
  
  def processStep1 = Action { implicit request =>
          	step1Form.bindFromRequest.fold(
      			errors => {      				
      				BadRequest(views.html.step1(errors)) 
      			},
      			user => {
      			   Ok(views.html.step2(step2Form, Application.getSessionValue(session).cellValues))
      			}
    	)
  }
  
  case class Step2 (
      value: String
  )
  
  val step2Form = Form[Step2] (
      mapping (
          "value" -> text(minLength = 1)
      )
      {
        (value) => Step2(value)
      }
      {
        step2 => Some(step2.value)
      }
  )
  
  
  def step2 = Action { implicit request =>
  	Ok(views.html.step2(step2Form, Application.getSessionValue(session).cellValues))  	
  }

  def step2AddValue = Action { implicit request =>
    step2Form.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.step2(errors, Application.getSessionValue(session).cellValues))
      },
      step2 => {
        val gs = controllers.Application.getSessionValue(session)
        gs.cellValues += step2.value
        Application.updateSessionValue(gs, session)
        Ok(views.html.step2(step2Form, Application.getSessionValue(session).cellValues))
      })
  }
  
  
  def step2RemoveValue = Action { implicit request =>
    step2Form.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.step2(errors, Application.getSessionValue(session).cellValues))
      },
      step2 => {
        val gs = controllers.Application.getSessionValue(session)
        gs.cellValues -= step2.value
        Application.updateSessionValue(gs, session)
        Ok(views.html.step2(step2Form, Application.getSessionValue(session).cellValues))
      })
  }
  
 

}