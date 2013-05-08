package controllers

import play.api.mvc._
import play.api.i18n.Messages
import play.api.data._
import play.api.data.Forms._
import models._
import org.apache.commons.codec.digest.DigestUtils
import com.typesafe.plugin._
import play.api.Play.current

object Profile extends Controller {
  
    case class ProfileData(
        email: String,
        password: String, 
        passwordConfirm: String
    )
    
    val profileForm : Form[ProfileData] = Form (
        mapping (
            "email" -> email,
            "password" -> tuple (
                "main" -> text(minLength = 6),
                "again" -> text(minLength = 6)
            ).verifying (
                Messages("password_not_same"), passwords => passwords._1 == passwords._2
            )
        )
        {
          (email, passwords) => ProfileData(email, passwords._1, passwords._2)
        }
        {
          profileData => Some(profileData.email, (profileData.password, profileData.passwordConfirm))
        }
    )
    
    def showProfileForm = Action { implicit request =>
      val dbUser = Application.connectedUser(session).get
      val formData = ProfileData(dbUser.email.get, "", "")
      Ok(views.html.profile(profileForm.fill(formData)))
    }

    def updateProfile = Action {
      implicit request =>

        val dbUser = Application.connectedUser(session).get

        profileForm.bindFromRequest.fold(
          errors => {
            BadRequest(views.html.profile(errors))
          },
          user => {
            DAO.updateUser(dbUser.id.get, user.email, DigestUtils.md5Hex(user.password))
            Redirect(routes.Application.index).flashing("successKey" -> "profile_update_successfull")
          }
        )
    }


  case class ResetData(email: String)

  val resetForm: Form[ResetData] = Form (
    mapping (
      "email" -> email
      )
    {
      (email) => ResetData(email)
    }
    {
      resetData => Some(resetData.email)
    }
    verifying(
      Messages("no_user_with_email"),
      resetData => DAO.findUserByEmail(resetData.email).isDefined
    )
  )


    def showResetPasswordForm = Action { implicit request =>
      Ok(views.html.resetPassword(resetForm))
    }



  def resetPassword = Action { implicit request =>

      resetForm.bindFromRequest.fold(
        errors => {
          BadRequest(views.html.resetPassword(errors))
        },
        resetData => {

          DAO.findUserByEmail(resetData.email)  match {
            case Some(user) => {

              val newPassword = generatePassword

              val mail = use[MailerPlugin].email
              mail.setSubject("CSV Generator password reset")
              mail.addRecipient(user.email.get)
              mail.addFrom("CSV Generator <info@csvgenerator.cz>")

              mail.send(
                Messages("mail_text_plain", newPassword),
                Messages("mail_text_html", newPassword))

              DAO.setUserPassword(user.id.get, DigestUtils.md5Hex(newPassword))

              Redirect(routes.Application.index).flashing("successKey" -> "password_reset")
            }
            case None => {
              Redirect(routes.Profile.showResetPasswordForm).flashing("errorKey" -> "no_user_with_email")
            }
          }
        }
      )



    }

    private val random = new scala.util.Random

    def generatePassword : String =
      randomAlphanumericString(6)

    // Generate a random string of length n from the given alphabet
    def randomString(alphabet: String)(n: Int): String =
      Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(n).mkString

    // Generate a random alphabnumeric string of length n
    def randomAlphanumericString(n: Int) =
      randomString("abcdefghijklmnopqrstuvwxyz0123456789")(n)




      

}