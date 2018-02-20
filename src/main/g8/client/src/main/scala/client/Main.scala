package client

import java.time.Clock

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.{ JSExportTopLevel, JSImport }
import scalacss.ProdDefaults._
import scalacss.ScalaCssReact._

@JSImport("material-ui", JSImport.Namespace, "MaterialUi")
@js.native
object MaterialUi extends js.Object

@JSImport("material-ui/styles", JSImport.Namespace, "MaterialUiStyles")
@js.native
object MaterialUiStyles extends js.Object

@JSImport("material-ui/svg-icons/index", JSImport.Namespace, "MaterialUiSvgIcons")
@js.native
object MaterialUiSvgIcons extends js.Object

@JSExportTopLevel("Main")
object Main {
  GlobalStyles.addToDocument()
  // This is needed to mount material-ui library into $g.mui JavaScript global variable ($g.mui = require("material-ui"))
  js.Dynamic.global.mui = MaterialUi
  js.Dynamic.global.mui.Styles = MaterialUiStyles
  js.Dynamic.global.mui.SvgIcons = MaterialUiSvgIcons

  private val service: ApplicationService = new ApplicationService
  private val clock: Clock = Clock.systemUTC()

  MainComponent(MainComponent.Props(
    loadList = service.list,
    createOrUpdate = service.createOrUpdate,
    delete = service.delete,
    clock = clock
  )).renderIntoDOM(dom.document.getElementById("root"))
}
