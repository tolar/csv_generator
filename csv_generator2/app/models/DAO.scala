package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

object DAO {

  def insertUser(username: String, passwordHash: String) {
    DB.withConnection { implicit conn =>
      SQL("insert into T_CSV_USER(USERNAME, PASSWORD_HASH) values ({username}, {passwordHash})").on((username, passwordHash)).executeInsert()      
    }
  }

}