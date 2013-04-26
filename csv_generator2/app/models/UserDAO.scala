package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

class User (
    val id: Pk[Long] = NotAssigned, 
    val username: String,
    val email: Option[String],
    passwordHash: String,
    var generationSession: Option[String]
)

object User {
  val simple = {
    get[Pk[Long]]("T_CSV_USER.ID") ~
    get[String]("T_CSV_USER.USERNAME") ~
    get[Option[String]]("T_CSV_USER.EMAIL") ~
    get[String]("T_CSV_USER.PASSWORD_HASH") ~
    get[Option[String]]("T_CSV_USER.GENERATION_SESSION") map {
      case id ~ username ~ email ~ password_hash ~ generation_session => new User(id, username, email, password_hash, generation_session)
    }
  }
}


object DAO {

  def insertUser(username: String, email: String, passwordHash: String) {
    DB.withConnection { implicit conn =>
      SQL("insert into T_CSV_USER(USERNAME, EMAIL, PASSWORD_HASH) values ({username}, {email}, {passwordHash})")
      .on('username -> username, 'email -> email, 'passwordHash -> passwordHash )
      .executeInsert()      
    }
  }

  def updateUser(id: Long, email: String, passwordHash: String) {
    DB.withConnection { implicit conn =>
      SQL("update T_CSV_USER set EMAIL = {email}, PASSWORD_HASH = {passwordHash} where ID = {id}")
        .on('email -> email, 'passwordHash -> passwordHash, 'id -> id)
        .executeUpdate()
    }
  }
  
  def findUserById (id: Long) : Option[User] = {
    DB.withConnection { implicit conn =>
      SQL("select * from T_CSV_USER where ID = {id}")
      .on('id -> id)
      .as(User.simple.singleOpt)
    }
  }

  def findUserByEmail (email: String) : Option[User] = {
    DB.withConnection { implicit conn =>
      SQL("select * from T_CSV_USER where EMAIL = {email}")
      .on('email -> email)
      .as(User.simple.singleOpt)
    }
  }

  def setUserPassword(id: Long, newPasswordHash: String) {
    DB.withConnection { implicit conn =>
      SQL("update T_CSV_USER set PASSWORD_HASH = {newPasswordHash} where ID = {id}")
        .on('newPasswordHash -> newPasswordHash, 'id -> id)
        .executeUpdate()
    }
  }
  
  def findUserByUsername (username: String) : Option[User] = {
    DB.withConnection { implicit conn =>
      SQL("select * from T_CSV_USER where USERNAME = {username}")
      .on('username -> username)
      .as(User.simple.singleOpt)
    }
  }
  
  def findUserByUsernameAndPasswordhash (username: String, passwordHash: String) : Option[User] = {
    DB.withConnection { implicit conn =>
      SQL("select * from T_CSV_USER where USERNAME = {username} and PASSWORD_HASH = {passwordHash}")
      .on('username -> username, 'passwordHash -> passwordHash)
      .as(User.simple.singleOpt)
    }
  } 
  
  def updateUserSession(id: Long, session: String) {
    DB.withConnection { implicit conn =>
      SQL("update T_CSV_USER set GENERATION_SESSION = {session} where ID = {id}")
      .on('session -> session, 'id -> id)
      .executeUpdate()
    }
  }
  
}

