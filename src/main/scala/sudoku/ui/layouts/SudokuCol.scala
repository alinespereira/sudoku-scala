package sudoku.ui.layouts

import scala.swing._

object SudokuCol extends SimpleSwingApplication {
  lazy val ui: Panel = new GridPanel(9, 1) {

    for (j <- 1 to rows)
      contents += new Button(s"$j") {
        enabled = false
      }

  }

  def top: Frame = new MainFrame {
    title = "Sudoku Col"
    contents = ui
  }
}
