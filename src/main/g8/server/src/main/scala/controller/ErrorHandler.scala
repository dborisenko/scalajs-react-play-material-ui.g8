package controller

import javax.inject.Singleton

import model.Failure
import org.slf4j.{ Logger, LoggerFactory }
import play.api.http.HttpErrorHandler
import play.api.mvc.Results._
import play.api.mvc.{ RequestHeader, Result }

import scala.concurrent.Future

@Singleton
class ErrorHandler extends HttpErrorHandler {
  private val logger: Logger = LoggerFactory.getLogger(getClass)

  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    logger.error(s"Raised Client Error: request=$request, statusCode=$statusCode, message=$message")
    Future.successful(Status(statusCode)(Failure(message)))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    logger.error(s"Raised Server Error: request=$request, exception=$exception", exception)
    Future.successful(InternalServerError(Failure(exception.getMessage)))
  }
}
