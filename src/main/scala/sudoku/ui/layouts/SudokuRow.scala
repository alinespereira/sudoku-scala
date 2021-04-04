package sudoku.ui.layouts

import scala.swing._

object SudokuRow extends SimpleSwingApplication {
  lazy val ui: Panel = new GridPanel(1, 9) {

    for (i <- 1 to columns)
      contents += new Button(s"$i")

  }

  def top: Frame = new MainFrame {
    title = "Sudoku Row"
    contents = ui
  }
}
