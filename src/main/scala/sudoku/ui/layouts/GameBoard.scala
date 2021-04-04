package sudoku.ui.layouts

import scala.swing._
import scala.swing.GridBagPanel._

object GameBoard extends SimpleSwingApplication {
  // lazy val ui: Panel = new GridPanel(2, 2) {

  //   contents += SudokuBoard.ui
  //   contents += SudokuRow.ui
  //   contents += SudokuCol.ui
  //   contents += SudokuSquare.ui

  // }

  lazy val ui: Panel = new GridBagPanel {
    val boardConstraint = new Constraints {
      gridx = 0
      gridy = 0
      gridwidth = 9
      gridheight = 9
    }
    layout(SudokuBoard.ui) = boardConstraint

    val rowConstraint = new Constraints {
      gridx = 0
      gridy = 9
      gridwidth = 9
      gridheight = 3
    }
    layout(SudokuRow.ui) = rowConstraint

    val colConstraint = new Constraints {
      gridx = 9
      gridy = 0
      gridwidth = 3
      gridheight = 9
    }
    layout(SudokuCol.ui) = colConstraint

    val squareConstraint = new Constraints {
      gridx = 9
      gridy = 9
      gridwidth = 3
      gridheight = 3
    }
    layout(SudokuSquare.ui) = squareConstraint
  }

  def top: Frame = new MainFrame {
    title = "Game Board"
    contents = ui
  }
}
