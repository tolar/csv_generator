package controllers

import play.api.mvc._
import play.api.mvc.Controller
import play.api.data.Forms._
import play.api.data.Form
import scala.collection.immutable.Map
import models.GenerationSession
import models.Row
import play.api.libs.iteratee.Enumerator
import collection.JavaConverters._

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
    step1Form = step1Form.fill(Step1(gs.rowsNo, gs.columnsNo))
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
      			   Ok(views.html.step2(step2Form, Application.getSessionValue(session).cellValues.asScala.toSet ))
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
  	Ok(views.html.step2(step2Form, Application.getSessionValue(session).cellValues.asScala.toSet))
  }

  def step2AddValue = Action { implicit request =>
    step2Form.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.step2(errors, Application.getSessionValue(session).cellValues.asScala.toSet))
      },
      step2 => {
        val gs = controllers.Application.getSessionValue(session)
        gs.cellValues.add(step2.value)
        //gs.cellValues = gs.cellValues.toList.sorted.
        Application.updateSessionValue(gs, session)
        Ok(views.html.step2(step2Form, Application.getSessionValue(session).cellValues.asScala.toSet))
      })
  }
  
  
  def step2RemoveValue = Action { implicit request =>    
    step2Form.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.step2(errors, Application.getSessionValue(session).cellValues.asScala.toSet))
      },
      step2 => {
        val gs = controllers.Application.getSessionValue(session)
        gs.cellValues.remove(step2.value)
        Application.updateSessionValue(gs, session)
        Ok(views.html.step2(step2Form, Application.getSessionValue(session).cellValues.asScala.toSet))
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
    
  	Ok(views.html.step3(step3Form, gs.cellValues.asScala.toSeq, gs.columnsNo, gs.rowsNo))
  }
  
  case class Step4 (
    delimiter: String,
    filename: String
  )
  
  var step4Form = Form[Step4] (
      mapping(
          "delimiter" -> text,
          "filename" -> text
      ) (Step4.apply)(Step4.unapply)
  )

  def processStep3() = Action { implicit request =>
    
    val gs = controllers.Application.getSessionValue(session)
    step3Form.bindFromRequest.fold(
      errors => {
        BadRequest(views.html.step3(step3Form, gs.cellValues.asScala.toSeq, gs.columnsNo, gs.rowsNo))
      },
      step3 => {    
                
        gs.setRows(step3.rows)
        Application.updateSessionValue(gs, session)  
        
        if (step3Form.bindFromRequest.data.contains("nextSubmit")) {
          step4Form = step4Form.fill(Step4(",", "file.csv"))
          Ok(views.html.step4(step4Form, gs)) 
        } else {
          Ok(views.html.step2(step2Form, Application.getSessionValue(session).cellValues.asScala.toSet))
        }
      })
  }
  
  
  def processStep4() = Action { implicit request =>
    
    val gs = controllers.Application.getSessionValue(session)
    
    if (step4Form.bindFromRequest.data.contains("generateSubmit")) {
      step4Form.bindFromRequest.fold(
    		  errors => BadRequest(views.html.step4(step4Form, gs)),
    		  step4 => generateFile(gs, step4) 
      )
       
    } else {
      val matrix = gs.getRows
      step3Form = step3Form.fill(Step3(matrix))
      Ok(views.html.step3(step3Form, gs.cellValues.asScala.toSeq, gs.columnsNo, gs.rowsNo))
    }    
   
  }
  
  def generateFile(gs: GenerationSession, step4: Step4) : SimpleResult[Array[Byte]] = { 
    
  	var sb = new StringBuilder();
	for (i <- 0.until(gs.matrix.length)) {
		for (j <- 0.until(gs.matrix(i).length)) {
			sb.append(gs.matrix(i)(j)).append(step4.delimiter);
		}
		sb.append("\r\n");
	}
	
	val csvFileBytes = sb.toString.getBytes()	
	
	val csvFileContent: Enumerator[Array[Byte]] = Enumerator(csvFileBytes)
	SimpleResult(
	    header = ResponseHeader(200, Map(
	        CONTENT_LENGTH -> csvFileBytes.length.toString,
	        CONTENT_TYPE -> "text/csv",
	        CONTENT_DISPOSITION -> ("attachment; filename=" + step4.filename)
	        )),
	    body = csvFileContent
	)
	
	
  }
    

  
 

}