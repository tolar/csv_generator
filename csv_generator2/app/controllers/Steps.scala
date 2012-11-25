package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.data.Forms._
import play.api.data._

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
      			  Redirect(routes.Application.index).flashing("successKey" -> "step2")
      			}
    	)
  }

}