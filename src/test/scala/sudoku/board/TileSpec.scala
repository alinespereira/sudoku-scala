import org.scalatest._

import sudoku.grid.Tile
import scala.util.Random
import sudoku.exception.IllegalTileGuess
import sudoku.grid.GuessRight
import sudoku.grid.GuessWrong

class TileSpec extends FunSpec with GivenWhenThen { spec =>
  val random = new Random

  describe("A tile") {
    it("might have no value") {
      val tile = new Tile
      assert(tile.getValue == 0)
    }

    it("might have a number") {
      val num: Int = spec.random.nextInt(9)
      val tile = new Tile(num)
      assert(tile.getValue == num)
    }

    it("should take guesses when not revealed") {
      Given("a tile")
      val num: Int = spec.random.nextInt(9)
      val tile = new Tile(num)

      When("it is not an initial game tile")
      Then("it should take guesses")
      assert(tile.guess(spec.random.nextInt).isSuccess)
      Then("it should not reveal itself")
      assert(tile.toString == Tile.hiddenTileStr)

      When("it is an initial game tile")
      tile.setInitial
      Then("it should not take guesses")
      assert(tile.guess(spec.random.nextInt).isFailure)
    }

    it("should properly validate") {
      Given("a tile")
      val num: Int = spec.random.nextInt(9)
      val tile = new Tile(num)

      When("guess the right number")
      assert(tile.guess(num).isSuccess)
      Then("it checks the guess as right")
      assert(tile.check == Some(GuessRight))

      When("guess the wrong number")
      assert(tile.guess(num + 1).isSuccess)
      Then("it checks the guess as wrong")
      assert(tile.check == Some(GuessWrong))

    }
  }
}
