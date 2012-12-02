package controllers

import play.api.mvc.Action
import play.api.mvc.Controller
import play.api.data.Forms._
import play.api.data.Form
import scala.collection._
import play.api.mvc.RequestHeader
import models.GenerationSession
import models.Row

import play.data.DynamicForm

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
    step1Form = step1Form.fill(Step1(gs.rowsNo, gs.columnsNo))
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
      			   gs.rowsNo = step1.rows
      			   gs.columnsNo = step1.columns
      			   gs.reallocateMatrix
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
        //gs.cellValues = gs.cellValues.toList.sorted.
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
      rows: List[Row]
  )
  
  var step3Form = Form[Step3] (
    mapping (
        "rows" -> list(
            mapping("cells" -> list(text)
        	) { rows => Row(rows) } (Row.unapply))
    ) { cells => Step3(cells) }(Step3.unapply)
  )
  
  def step3 = Action { implicit request =>
    val gs = controllers.Application.getSessionValue(session)
   
    val matrix = gs.getRows
    
    step3Form = step3Form.fill(Step3(matrix))
    
  	Ok(views.html.step3(step3Form, gs.cellValues.toSeq, gs.columnsNo, gs.rowsNo))  	
  }

  def processStep3() = Action { implicit request =>
    
    val gs = controllers.Application.getSessionValue(session)
    step3Form.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.step3(step3Form, gs.cellValues.toSeq, gs.columnsNo, gs.rowsNo))
      },
      step3 => {    
        
        println("STEP3:" + step3)
        gs.setRows(step3.rows)
        Application.updateSessionValue(gs, session)  
        
        if (step3Form.bindFromRequest.data.contains("nextSubmit")) {
          Ok(views.html.step4(gs)) 
        } else {
          Ok(views.html.step2(step2Form, Application.getSessionValue(session).cellValues))
        }
      })
  }
  
  
  def processStep4() = Action { implicit request =>
    
    val gs = controllers.Application.getSessionValue(session)
    
    if (step3Form.bindFromRequest.data.contains("nextSubmit")) {
      Ok(views.html.step4(gs)) 
    } else {
      Ok(views.html.step3(step3Form, gs.cellValues.toSeq, gs.columnsNo, gs.rowsNo))
    }    
   
  }
    

  
 

}