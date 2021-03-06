package controllers

import play.api.mvc._
import play.api.i18n.Messages
import play.api.data._
import play.api.data.Forms._
import models._
import org.apache.commons.codec.digest.DigestUtils

object Registration extends Controller {
  
    case class RegUser(
        username: String, 
        email: String,
        password: String, 
        passwordConfirm: String
    )
    
    val registrationForm : Form[RegUser] = Form (
        mapping (
            "username" -> text(minLength = 5),
            "email" -> email,
            "password" -> tuple (
                "main" -> text(minLength = 6),
                "again" -> text(minLength = 6)
            ).verifying (
                Messages("password_not_same"), passwords => passwords._1 == passwords._2)            
        ) 
        
        {
          (username, email, passwords) => RegUser(username, email, passwords._1, "")
        }
        {
          user => Some(user.username, user.email, (user.password, ""))
        }.verifying(
            Messages("username_already_exists"), 
            user => DAO.findUserByUsername(user.username).isEmpty
        )
       
    )
    
    def showRegistrationForm = Action { implicit request =>
      Ok(views.html.registration(registrationForm.fill(RegUser("","","",""))))
    }
    
    def registration = Action { implicit request => 
      	registrationForm.bindFromRequest.fold(
      			errors => {      				
      				BadRequest(views.html.registration(errors)) 
      			},
      			user => {
      			  DAO.insertUser(user.username, user.email, DigestUtils.md5Hex(user.password))
      			  Redirect(routes.Application.index).flashing("successKey" -> "registration_successfull")
      			}
    	)
    }      
      

}