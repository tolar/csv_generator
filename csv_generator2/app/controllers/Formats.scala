package controllers

import play.api.libs.json._
import play.api.libs.json.JsObject
import models.GenerationSession
import play.api.libs.json.JsArray
import play.api.libs.json.JsString
import play.api.libs.json.JsNumber
import scala.collection.mutable

/**
 * @author: Vaclav Tolar, (vaclav_tolar@kb.cz, vaclav.tolar@cleverlance.com, vaclav.tolar@gmail.com)
 *          Date: 2013-05-01
 */
object Formats {
  implicit object GenerationSessionFormat extends Format[GenerationSession] {
    def reads(json: JsValue): JsSuccess[GenerationSession] = JsSuccess(GenerationSession (
      (json \ "rowsNo").as[Int],
      (json \ "columnsNo").as[Int],
      (json \ "cellValues").as[mutable.TreeSet[String]],
      (json \ "matrix").asOpt[Array[Array[String]]].getOrElse(Array.ofDim[String]((json \ "rowsNo").as[Int], (json \ "columnsNo").as[Int])),
      (json \ "delimiter").as[String],
      (json \ "filename").as[String]
    ))

    def writes(gs: GenerationSession): JsValue = JsObject(Seq(
      "rowsNo" -> JsNumber(gs.rowsNo),
      "columnsNo" -> JsNumber(gs.columnsNo),
      "cellValues" -> JsArray(gs.cellValues.toSeq.map(Json.toJson(_))),
      "matrix" -> JsArray(gs.matrix.map(Json.toJson(_))),
      "delimiter" -> JsString(gs.delimiter),
      "filename" -> JsString(gs.filename)
    ))
  }

}
