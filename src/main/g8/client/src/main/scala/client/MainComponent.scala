package client

import java.time.Clock

import chandu0101.scalajs.react.components.Implicits._
import chandu0101.scalajs.react.components.materialui.MuiSvgIcon._
import chandu0101.scalajs.react.components.materialui.{ DeterminateIndeterminate, Mui, MuiLinearProgress, MuiMuiThemeProvider, MuiSnackbar }
import client.Message.{ ErrorMessage, InfoMessage, NoMessage }
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.StateSnapshot
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.{ BackendScope, Callback, CallbackTo, ReactTouchEvent, ScalaComponent }
import model.Todo
import model.Todo.TodoId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

sealed trait Message {
  def isDisplayable: Boolean = false
}

object Message {

  case object NoMessage extends Message

  final case class ErrorMessage(error: String) extends Message {
    override def isDisplayable: Boolean = true
  }

  final case class InfoMessage(info: String) extends Message {
    override def isDisplayable: Boolean = true
  }

}

object MainComponent {
  val emptyTodoList: List[Todo] = List.empty

  final case class State(
    todos: List[Todo],
    message: Message
  )

  final case class Props(
    loadList: () => Future[ErrorOr[List[Todo]]],
    createOrUpdate: Todo => Future[ErrorOr[Todo]],
    delete: TodoId => Future[ErrorOr[Unit]],
    clock: Clock
  )

  class Backend(scope: BackendScope[Props, Option[State]]) {

    //Its required to explicitly rethrow exception. ScalaJs-react will not do it by default(see Callback.future doc)
    private def cb[T](cbf: CallbackTo[Future[T]]): Callback = cbf.map(_.onComplete(_.fold[Unit](throw _, identity[T])))

    private[MainComponent] val preLoad: Callback = cb(scope.props.flatMap(v => CallbackTo(v.loadList())).flatMap { future =>
      CallbackTo.future(future.map(result => scope.setState(Some(State(
        todos = result.getOrElse(emptyTodoList),
        message = result.left.toOption.map(ErrorMessage).getOrElse(NoMessage)
      ))).map(Future.successful))).map(_.flatten)
    })

    private def defaultHandler[T](successMessage: String): ErrorOr[List[Todo]] => CallbackTo[Unit] = {
      case Left(error) =>
        scope.modState(_.map(_.copy(message = ErrorMessage(error))))
      case Right(result) =>
        scope.modState(_.map(_.copy(message = InfoMessage(successMessage), todos = result)))
    }

    private def wrapToListCall[T](loadList: () => Future[ErrorOr[List[Todo]]], successMessage: String)(func: => Future[ErrorOr[T]]): CallbackTo[Future[Unit]] =
      CallbackTo.future(func.flatMap {
        case Left(error) => Future.successful(Left(error))
        case Right(_) => loadList()
      }.map(defaultHandler(successMessage)))

    private def deleteServiceCall(props: Props)(todoId: TodoId): CallbackTo[Future[Unit]] =
      wrapToListCall(props.loadList, "Deleted " + todoId)(props.delete(todoId))

    private def createOrUpdateServiceCall(props: Props)(todo: Todo): CallbackTo[Future[Unit]] =
      wrapToListCall(props.loadList, "Created " + todo.id)(props.createOrUpdate(todo))

    private def renderSubComponent(snapshot: StateSnapshot[List[Todo]], props: Props): VdomElement =
      TodoListComponent(TodoListComponent.Props(
        snapshot = snapshot,
        createOrUpdate = todo => createOrUpdateServiceCall(props)(todo).map(_ => ()),
        delete = todoId => deleteServiceCall(props)(todoId).map(_ => ()),
        clock = props.clock
      ))

    def render(p: Props, s: Option[State]): VdomElement = {
      val closeMessageBox: Callback = scope.modState(_.map(_.copy(message = NoMessage)))
      MuiMuiThemeProvider()(
        s.fold[VdomElement](MuiLinearProgress(mode = DeterminateIndeterminate.indeterminate)()) { state =>
          <.div(
            renderSubComponent(StateSnapshot(state.todos)(ns => scope.modState(_.map(_.copy(todos = ns)))), p),
            MuiSnackbar(
              autoHideDuration = 60000,
              message = state.message match {
                case NoMessage => ""
                case ErrorMessage(error) => <.div(
                  Mui.SvgIcons.ActionPanTool.apply(color = Mui.Styles.colors.redA700)(),
                  "Error: " + error
                )
                case InfoMessage(info) => info
              },
              action = "Close",
              onRequestClose = (_: String) => closeMessageBox,
              onActionTouchTap = (_: ReactTouchEvent) => closeMessageBox,
              open = state.message.isDisplayable
            )()
          )
        }
      )
    }
  }

  private val component = ScalaComponent.builder[Props]("MainComponent")
    .initialState[Option[State]](None)
    .renderBackend[Backend]
    .componentDidMount(_.backend.preLoad)
    .build

  def apply(props: Props): Unmounted[Props, Option[State], Backend] = component(props)
}
