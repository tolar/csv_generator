package models

import play.api.db._
import play.api.Play.current

import org.scalaquery.ql._
import org.scalaquery.ql.TypeMapper._
import org.scalaquery.ql.basic.{ BasicTable => Table }

import org.scalaquery.ql.extended.MySQLDriver.Implicit._

import org.scalaquery.session._

object Users extends Table[(Long, String, String, String)]("t_csv_user") {

  lazy val database = Database.forDataSource(DB.getDataSource())

  def id = column[Long]("id", O PrimaryKey)
  def username = column[String]("username", O NotNull)
  def passwordHash = column[String]("password_hash", O NotNull)
  def generationSession = column[String]("generation_session")
  def * = id ~ username ~ passwordHash ~ generationSession

  def insert(username: String, passwordHash: String) = database.withSession { implicit db: Session =>
    println(Users.insertStatement)
  	username ~ passwordHash insert(username, passwordHash)
  }
 
  

}


  

