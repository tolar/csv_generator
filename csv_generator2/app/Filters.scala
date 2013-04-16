import play.api.mvc._
import play.api.mvc.AsyncResult
import play.api.libs.concurrent.Execution.Implicits._

object Filters {

  val UUID: String = "UUID"

  object SessionIdFilter extends EssentialFilter {
    def apply(next: EssentialAction) = new EssentialAction {
      def apply(request: RequestHeader) = {

        def initCacheIdInSession(result: PlainResult): Result = {

          //val session = Session.decodeFromCookie(Cookies(result.header.headers.get(HeaderNames.COOKIE)).get(Session.COOKIE_NAME))
          val session = request.session

          if (!session.data.contains("UUID")) {
            val newUuid = java.util.UUID.randomUUID.toString()
            result.withSession(session + ("UUID" -> newUuid))
          } else {
            result
            //result.withSession(session)
          }

          //result


/*
          val uuid = session.get(UUID)

          uuid match {
            case None =>

            case Some(uuid) => {
              return result
            }
          }
*/

        }

        next(request).map {
          case plain: PlainResult => initCacheIdInSession(plain)
          case async: AsyncResult => async.transform(initCacheIdInSession)
        }
      }
    }
  }
}