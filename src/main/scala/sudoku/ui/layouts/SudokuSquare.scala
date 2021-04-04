package sudoku.ui.layouts

import scala.swing._

object SudokuSquare extends SimpleSwingApplication {
  lazy val ui: Panel = new GridPanel(3, 3) {

    for (i <- 1 to columns)
      for (j <- 1 to rows)
        contents += new Button(s"$i, $j")

  }

  def top: Frame = new MainFrame {
    title = "Sudoku Square"
    contents = ui
  }
}
