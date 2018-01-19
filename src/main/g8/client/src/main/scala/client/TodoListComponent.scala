package client

import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.StateSnapshot
import japgolly.scalajs.react.vdom.VdomElement
import japgolly.scalajs.react.{ BackendScope, Callback, ScalaComponent }
import model.Todo
import model.Todo.TodoId

object TodoListComponent {

  final case class State()

  final case class Props(
    snapshot: StateSnapshot[List[Todo]],
    createOrUpdate: Todo => Callback,
    delete: TodoId => Callback
  )

  final class Backend($: BackendScope[Props, State]) {
    def render(): VdomElement = ???
  }

  private val component = ScalaComponent
    .builder[Props]("ScheduleListComponent")
    .initialState(State())
    .renderBackend[Backend]
    .build

  def apply(props: Props): Unmounted[Props, State, Backend] = component(props)
}
