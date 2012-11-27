import play.GlobalSettings._

import play.api._
import play.api.mvc.RequestHeader
import play.api.mvc.Handler
import play.api.mvc.Session
import play.api.mvc.Action
import play.api.mvc.PlainResult
import controllers.Application.UUID

object Global extends GlobalSettings {
    
  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    super.onRouteRequest(request).map { handler =>
      handler match {
        case a: Action[_] => WithCacheIdInSession(a)
        case _ => handler
      }
    }
  }    

  def WithCacheIdInSession[A](action: Action[A]): Action[A] = {
    Action(action.parser) { request =>
      val result = action(request)
      result match {
        case r: PlainResult => r.withSession(initCacheIdInSession(request.session))
        case _ => result
      }
    }
  }


  def initCacheIdInSession(session: Session): Session = {
    var uuid = session.get(UUID);
    uuid match {
      case None => {
        val uuid = java.util.UUID.randomUUID.toString()
        session + (UUID -> uuid);
      }
      case Some(uuid) => {
        session
      }
    }
  }

}