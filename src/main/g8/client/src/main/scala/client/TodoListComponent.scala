package client

import java.time.Clock

import chandu0101.scalajs.react.components.materialui._
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.StateSnapshot
import japgolly.scalajs.react.vdom.VdomNode
import japgolly.scalajs.react.vdom.html_<^._
import model.Todo
import model.Todo.TodoId

object TodoListComponent {

  final case class Props(
    snapshot: StateSnapshot[List[Todo]],
    createOrUpdate: Todo => Callback,
    delete: TodoId => Callback,
    clock: Clock
  )

  final case class State(
    createOrUpdate: Option[Todo]
  )

  final class Backend($: BackendScope[Props, State]) {

    private val colNames = MuiTableRow()(
      MuiTableHeaderColumn()("ID"),
      MuiTableHeaderColumn()("Description"),
      MuiTableHeaderColumn()("Is Completed"),
      MuiTableHeaderColumn()("Created At"),
      MuiTableHeaderColumn()()
    )

    private def withSpanTitle(string: String) = <.span(^.title := string, string)

    private def renderRow(todo: Todo): VdomNode = MuiTableRow(key = todo.id.toString)(
      MuiTableRowColumn()(withSpanTitle(todo.id.toString)),
      MuiTableRowColumn()(withSpanTitle(todo.description)),
      MuiTableRowColumn()(withSpanTitle(todo.isCompleted.toString)),
      MuiTableRowColumn()(withSpanTitle(todo.createdAt.toString)),
      MuiTableRowColumn()(
        MuiFlatButton(label = "Update", onClick = openDialog(todo))(),
        MuiFlatButton(label = "Delete", onClick = handleDelete(todo.id))()
      )
    )

    private def open(todo: Todo) = $.modState(_.copy(createOrUpdate = Some(todo)))

    private def handleDelete(todoId: TodoId): ReactEvent => Callback =
      _ => $.props.flatMap(_.delete(todoId)) >> Callback.info(s"Deleted todo $todoId")

    private def openDialog(todo: Todo): ReactEvent => Callback =
      _ => open(todo) >> Callback.info("Opened")

    private def renderRows(todos: List[Todo]): List[VdomNode] =
      todos.sortBy(_.createdAt).map(renderRow)

    def render(p: Props, s: State): VdomElement = {
      <.div(
        MuiTable(
          height = "600px",
          fixedHeader = true,
          fixedFooter = true,
          selectable = false,
          multiSelectable = false
        )(
          MuiTableHeader(enableSelectAll = false, adjustForCheckbox = false, displaySelectAll = false)(
            colNames
          ),
          MuiTableBody(stripedRows = true, showRowHover = true, displayRowCheckbox = false)(
            renderRows(p.snapshot.value): _*
          ),
          MuiTableFooter(adjustForCheckbox = false)(
            colNames
          )
        ),
        MuiRaisedButton(label = "Create", onClick = openDialog(Todo.newTodo(p.clock)))(),
        s.createOrUpdate.fold[VdomElement](<.div())(todo => TodoEditorComponent(TodoEditorComponent.Props(
          snapshot = StateSnapshot(todo)(s => $.modState(_.copy(createOrUpdate = Some(s)))),
          submit = s => $.props.flatMap(_.createOrUpdate(s)),
          close = $.modState(_.copy(createOrUpdate = None))
        )))
      )
    }
  }

  private val component = ScalaComponent
    .builder[Props]("TodoListComponent")
    .initialState(State(None))
    .renderBackend[Backend]
    .build

  def apply(props: Props): Unmounted[Props, State, Backend] = component(props)
}
