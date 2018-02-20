package client

import chandu0101.scalajs.react.components.Implicits._
import chandu0101.scalajs.react.components.materialui.{ MuiCheckbox, MuiDialog, MuiFlatButton, MuiTextField }
import japgolly.scalajs.react.component.Scala.Unmounted
import japgolly.scalajs.react.extra.StateSnapshot
import japgolly.scalajs.react.vdom.html_<^._
import japgolly.scalajs.react.vdom.{ VdomElement, VdomNode }
import japgolly.scalajs.react.{ BackendScope, Callback, ReactEvent, ReactEventFromInput, ReactMouseEvent, ScalaComponent }
import model.Todo

object TodoEditorComponent {

  final case class Props(
    snapshot: StateSnapshot[Todo],
    submit: Todo => Callback,
    close: Callback
  )

  final class Backend($: BackendScope[Props, Unit]) {

    private val close: Callback = $.props.flatMap(_.close)

    private def submit(todo: Todo): Callback = $.props.flatMap(_.submit(todo))

    private def modState(f: Todo => Todo): Callback = $.props.flatMap(_.snapshot.modState(f))

    private def actions(todo: Todo): VdomNode = <.div(
      MuiFlatButton(key = "cancel", label = "Cancel", secondary = true, onClick = handleDialogCancel)(),
      MuiFlatButton(key = "submit", label = "Submit", secondary = true, onClick = handleDialogSubmit(todo))()
    )

    private val handleDialogCancel: ReactEvent => Callback = _ => close

    private def handleDialogSubmit(todo: Todo): ReactEvent => Callback = _ => submit(todo) >> close

    private def onInputChange(f: (Todo, String) => Todo) = (_: ReactEventFromInput, newValue: String) =>
      modState(f(_, newValue))

    private val onIsCompletedSwitch: (ReactMouseEvent, Boolean) => Callback =
      (_, isCompleted) => modState(_.copy(isCompleted = isCompleted))

    def render(props: Props): VdomElement = {
      val todo = props.snapshot.value
      MuiDialog(
        title = "To Do:",
        actions = actions(todo),
        open = true,
        modal = true
      )(
          <.div(
            ^.display.flex,
            ^.flexDirection.column,
            <.div(
              MuiTextField(floatingLabelText = "ID", disabled = true, value = todo.id.toString)(),
              <.br(),
              MuiTextField(floatingLabelText = "Description", value = todo.description,
                onChange = onInputChange((s, n) => s.copy(description = n)), multiLine = true)()
            ),
            MuiCheckbox(name = "isCompleted", value = "isCompleted", label = "Is Completed", checked = todo.isCompleted,
              onCheck = onIsCompletedSwitch)()
          )
        )
    }
  }

  private val component = ScalaComponent
    .builder[Props]("TodoEditorComponent")
    .renderBackend[Backend]
    .build

  def apply(props: Props): Unmounted[Props, Unit, Backend] = component(props)
}
