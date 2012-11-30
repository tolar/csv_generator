package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.data.Forms._
import play.api.data.Form
import scala.collection._
import play.api.mvc.RequestHeader

object Steps extends Controller {
  
  case class Step1 (
    rows: Int,
    columns: Int
  )
  
  var step1Form = Form[Step1] (
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
    val gs = controllers.Application.getSessionValue(session)
    println("GS:" + gs)
    step1Form = step1Form.fill(Step1(gs.rows, gs.columns))
    println("STEP_FORM:" + step1Form.data)
    Ok(views.html.step1(step1Form))
  } 
  
  def processStep1 = Action { implicit request =>
          	step1Form.bindFromRequest.fold(
      			errors => {      				
      				BadRequest(views.html.step1(errors)) 
      			},
      			step1 => {
      			   val gs = controllers.Application.getSessionValue(session)
      			   gs.rows = step1.rows
      			   gs.columns = step1.columns
                   Application.updateSessionValue(gs, session)
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
    println("step2RemoveValue")
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
  
  
  case class Step3 (
      matrix: List[List[String]]
  )
  
  var step3Form = Form[Step3] {
    mapping (
        "matrix" -> list(list(text))
    )
    {
      (matrix) => Step3(matrix)
    }
    {
      step3 => Some(step3.matrix)
    }
  }
  
  def step3 = Action { implicit request =>
    val gs = controllers.Application.getSessionValue(session)

    var matrix = List[List[String]]()
    for (rowIndex <- 0 until gs.rows) {
      var cells = List[String]()
      for (colIndex <- 0 until gs.columns) {
        cells ::= ""  
      }
      matrix ::= cells
    }
    
    step3Form = step3Form.fill(Step3(matrix))
    
  	Ok(views.html.step3(step3Form, gs.cellValues.toSeq, gs.columns, gs.rows))  	
  }

  def processStep3Prev = Action { implicit request =>
    val gs = controllers.Application.getSessionValue(session)
    step3Form.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.step3(step3Form, gs.cellValues.toSeq, gs.columns, gs.rows))
      },
      step3 => {        
        gs.matrix = step3.matrix
        Application.updateSessionValue(gs, session)  
        Ok(views.html.step2(step2Form, Application.getSessionValue(session).cellValues))
      })
  }
  
  
  def processStep3Next = Action { implicit request =>
    val gs = controllers.Application.getSessionValue(session)
    step3Form.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.step3(step3Form, gs.cellValues.toSeq, gs.columns, gs.rows))
      },
      step3 => {        
        gs.matrix = step3.matrix
        Application.updateSessionValue(gs, session)  
        Ok(views.html.step4())
      })
  }  
  
  def step4 = Action { implicit request =>
  	Ok(views.html.step4())  	
  }
    

  
 

}