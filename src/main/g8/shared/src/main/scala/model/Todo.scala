package model

import java.time.{ Clock, Instant }
import java.util.UUID

import io.circe.generic.JsonCodec
import io.circe.{ Decoder, Encoder }
import io.circe.java8.time._
import model.Todo.TodoId
import shapeless.tag
import shapeless.tag.@@

@JsonCodec
final case class Todo(
  id: TodoId,
  description: String,
  isCompleted: Boolean,
  createdAt: Instant
)

object Todo {

  sealed trait TodoIdTag
  type TodoId = UUID @@ TodoIdTag

  object TodoId {
    def newTodoId: TodoId = apply(UUID.randomUUID())
    def apply(id: UUID): TodoId = tag[TodoIdTag][UUID](id)
    def apply(id: String): TodoId = apply(UUID.fromString(id))
  }

  private[Todo] implicit lazy val TodoIdDecoder: Decoder[TodoId] = Decoder.decodeUUID.map(TodoId.apply)
  private[Todo] implicit lazy val TodoIdEncoder: Encoder[TodoId] = Encoder.encodeUUID.contramap(identity)

  def newTodo(clock: Clock): Todo = Todo(
    id = TodoId.newTodoId,
    description = "",
    isCompleted = false,
    createdAt = clock.instant
  )
}
