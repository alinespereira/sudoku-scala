package sudoku.ui

import scala.swing._
import scala.swing.event._

import sudoku.ui.menus._
import sudoku.ui.layouts._

object MainGame extends SimpleSwingApplication {
  def top: Frame = new MainFrame {
    title = "Sudoku"
    // contents = MainMenu.ui
    contents = GameBoard.ui

    size = new Dimension(500, 500)
    centerOnScreen
  }
}
