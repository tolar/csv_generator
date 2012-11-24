package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import models._

object Login extends Controller {
  
    case class User(
        username: String, 
        password: String 
    )
    
    val loginForm = Form[User] (
        
        mapping (
            "username" -> text,
            "password" -> text
        )         
        {
          (username, password) => User(username, password)
        }
        {
          user => Some(user.username, user.password)
        }.verifying(
            Messages("username_does_not_exist"), 
            user => DAO.findUserByUsername(user.username).isEmpty
        )
       
    )
    
    def login = Action {
      Ok(views.html.login(loginForm));
    }
        

}