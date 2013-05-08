package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import models._
import org.apache.commons.codec.digest.DigestUtils

object Login extends Controller {
  
    case class LogUser(
        username: String, 
        password: String 
    )
    
    val loginForm = Form[Login.LogUser] (
        
        mapping (
            "username" -> text,
            "password" -> text
        )         
        {
          (username, password) => LogUser(username, password)
        }
        {
          user => Some(user.username, user.password)
        }.verifying(
            Messages("invalid_username_password"), 
            user => checkCredentials(user) 
        )
       
    )
    
    def showLoginForm = Action { implicit request =>
      val formData = LogUser("", "")
      Ok(views.html.login(loginForm.fill(formData)))
    }
    
    def login = Action { implicit request =>
		loginForm.bindFromRequest.fold(
			errors => {      				
				BadRequest(views.html.login(errors)) 
			},
			user => {
        val dbUser: User = DAO.findUserByUsername(user.username).get
        //val session = Application.connect(request.session, dbUser.id.toString)
				Redirect("/index")
					.withSession(session + ("logged" -> dbUser.id.toString) + ("username" -> dbUser.username))
					.flashing("successKey" -> "user_was_logged_in")
			}
		) 
    }
    
    def checkCredentials(user: LogUser) : Boolean = {
      !DAO.findUserByUsernameAndPasswordhash(user.username, DigestUtils.md5Hex(user.password)).isEmpty
    }
        

}