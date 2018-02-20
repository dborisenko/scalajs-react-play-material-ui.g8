package client

import io.circe.Decoder
import io.circe.parser.parse
import io.circe.syntax._
import model.Todo.TodoId
import model.{ Failure, Todo }
import org.scalajs.dom.XMLHttpRequest
import org.scalajs.dom.ext.{ Ajax, AjaxException }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApplicationService {

  private def parseOutput[T: Decoder](string: String): ErrorOr[T] =
    parse(string).flatMap(_.as[T]).fold(error => Left(error.getMessage), result => Right(result))

  private def parseOutput[T: Decoder](r: XMLHttpRequest): Either[String, T] =
    parseOutput(r.responseText)

  private def raiseErrorFromFuture[T](f: Future[T]): Future[ErrorOr[T]] = f.map[ErrorOr[T]](Right(_)).recoverWith {
    case AjaxException(xhr) => Future.successful[ErrorOr[T]](parseOutput[Failure](xhr).flatMap(f => Left(f.message)))
    case error => Future.successful[ErrorOr[T]](Left(error.getMessage))
  }

  private lazy val headers: Map[String, String] = Map("Content-Type" -> "application/json")

  def createOrUpdate(todo: Todo): Future[ErrorOr[Todo]] = raiseErrorFromFuture(Ajax.post(
    url = "/api/todo", data = todo.asJson.noSpaces, headers = headers
  )).map(_.flatMap(parseOutput[Todo]))

  def delete(todoId: TodoId): Future[ErrorOr[Unit]] = raiseErrorFromFuture(Ajax.delete(
    url = "/api/todo/" + todoId, headers = headers
  )).map(_.map(_ => ()))

  def list(): Future[ErrorOr[List[Todo]]] = raiseErrorFromFuture(Ajax.get(
    url = "/api/todo", headers = headers
  )).map(_.flatMap(parseOutput[List[Todo]]))
}
