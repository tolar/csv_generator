package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.data.Forms._
import play.api.data._

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
      			  Redirect(routes.Steps.step2).flashing("successKey" -> "step2")
      			}
    	)
  }
  
  case class Step2 (
      values: List[String],
      newValue: String
  )
  
  val step2Form = Form[Step2] (
      mapping (
          "values" -> list(text),
          "newValue" -> text
      )
      {
        (values, newValue) => Step2(values, newValue)
      }
      {
        step2 => Some(step2.values, step2.newValue)
      }
  )
  
  def step2 = Action { implicit request =>
    Ok(views.html.step2(step2Form, Seq[String]() ))
  }
  
  def processStep2 = Action { implicit request =>
    Ok(views.html.index())
  }

}