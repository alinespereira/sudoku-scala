package sudoku.ui.menus

import scala.swing._

object MainMenu extends SimpleSwingApplication {
  lazy val ui: Panel = new FlowPanel {
    border = Swing.EmptyBorder(5, 5, 5, 5)

    contents += new BoxPanel(Orientation.Vertical) {
      contents ++= Seq(
        new Button("Hello"),
        new Button("World")
      )
    }
  }

  def top: Frame = new MainFrame {
    title = "Main Menu"
    contents = ui
  }
}
