package sudoku.ui.layouts

import scala.swing._

object SudokuBoard extends SimpleSwingApplication {
  lazy val ui: Panel = new GridPanel(9, 9) {

    for (i <- 1 to rows)
      for (j <- 1 to columns)
        contents += new Button(s"$i, $j")

  }

  def top: Frame = new MainFrame {
    title = "Sudoku Board"
    contents = ui
  }
}
