import org.scalatest._
import sudoku.grid.Board
import scala.util.Random
import sudoku.grid.InitialGame

class BoardSpec extends FunSpec with GivenWhenThen { self =>
  val random = new Random

  describe("A board") {
    it("is valid when empty") {
      val board = new Board
      assert(board.isValid)
    }

    it("should be full and valid upon initRandom") {
      val board = new Board
      board.initRandom
      assert(board.isFullValid)
    }

    it("should be guess valid upon start") {
      val board = new Board
      board.initRandom
      assert(board.getStatus == InitialGame)
    }
  }
}
