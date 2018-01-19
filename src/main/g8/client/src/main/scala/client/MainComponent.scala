package client

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
  type SubState = List[Todo]
  val emptySubState: SubState = List.empty

  final case class State(
    sub: SubState,
    message: Message
  )

  final case class Props(
    preLoad: CallbackTo[Future[ErrorOr[SubState]]],
    createOrUpdate: Todo => Callback,
    delete: TodoId => Callback
  )

  class Backend($: BackendScope[Props, Option[State]]) {

    //Its required to explicitly rethrow exception. ScalaJs-react will not do it by default(see Callback.future doc)
    private def cb[T](cbf: CallbackTo[Future[T]]): Callback = cbf.map(_.onComplete(_.fold[Unit](throw _, identity[T])))

    private[MainComponent] val preLoad: Callback = cb($.props.flatMap(_.preLoad).flatMap { future =>
      CallbackTo.future(future.map(result => $.setState(Some(State(
        sub = result.getOrElse(emptySubState),
        message = result.left.toOption.map(ErrorMessage).getOrElse(NoMessage)
      ))).map(Future.successful))).map(_.flatten)
    })

    private def renderSubComponent(snapshot: StateSnapshot[SubState], props: Props): VdomElement =
      TodoListComponent(TodoListComponent.Props(
        snapshot = snapshot,
        createOrUpdate = props.createOrUpdate,
        delete = props.delete
      ))

    def render(p: Props, s: Option[State]): VdomElement = {
      val closeMessageBox: Callback = $.modState(_.map(_.copy(message = NoMessage)))
      MuiMuiThemeProvider()(
        s.fold[VdomElement](MuiLinearProgress(mode = DeterminateIndeterminate.indeterminate)()) { state =>
          <.div(
            renderSubComponent(StateSnapshot(state.sub)(ns => $.modState(_.map(_.copy(sub = ns)))), p),
            MuiSnackbar(
              autoHideDuration = 60000,
              message = state.message match {
                case NoMessage => ""
                case ErrorMessage(error) => <.div(
                  Mui.SvgIcons.ActionPanTool.apply(color = Mui.Styles.colors.redA700)(),
                  s"Error: $error"
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
