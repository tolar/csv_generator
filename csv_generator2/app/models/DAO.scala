package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(
    id: Pk[Long] = NotAssigned, 
    username: String,
    email: String,
    passwordHash: String,
    generationSession: Option[String]
    )

object DAO {

  def insertUser(username: String, email: String, passwordHash: String) {
    DB.withConnection { implicit conn =>
      SQL("insert into T_CSV_USER(USERNAME, EMAIL, PASSWORD_HASH) values ({username}, {email}, {passwordHash})")
      .on('username -> username, 'email -> email, 'passwordHash -> passwordHash )
      .executeInsert()      
    }
  }
  
  def findUserByUsername (username: String) : Option[User] = {
    DB.withConnection { implicit conn =>
      SQL("select * from T_CSV_USER where USERNAME = {username}")
      .on('username -> username)
      .as(User.simple.singleOpt)
    }
  }
}

object User {
  val simple = {
    get[Pk[Long]]("T_CSV_USER.ID") ~
    get[String]("T_CSV_USER.USERNAME") ~
    get[String]("T_CSV_USER.EMAIL") ~
    get[String]("T_CSV_USER.PASSWORD_HASH") ~
    get[Option[String]]("T_CSV_USER.GENERATION_SESSION") map {
      case id ~ username ~ email ~ password_hash ~ generation_session => User(id, username, email, password_hash, generation_session)
    }
  }
}