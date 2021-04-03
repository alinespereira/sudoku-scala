import org.scalatest._
import sudoku.grid.Square
import scala.util.Random

class SquareSpec extends FunSpec with GivenWhenThen { self =>
  val random = new Random
  describe("A square") {
    it("should be valid when empty") {
      val square = new Square
      square.initEmpty
      assert(square.isValid)
    }

    it("should be valid when there is no repeated values") {
      val square = new Square
      val lim = 1.max(self.random.nextInt(square.getSize))

      When("some tiles are empty")
      val nums1 = self.random.shuffle(Range(1, lim).toList)
      square.setValues(nums1)
      Then("it is valid for the filled tiles")
      assert(square.isValid)

      square.initEmpty
      When("no tiles are empty")
      val nums2 = self.random.shuffle(Range(1, square.size + 1).toList)
      square.setValues(nums2)
      Then("it is valid for all the tiles")
      assert(square.isValid)

      When("any tile is repeated")
      val nums3 = self.random.shuffle(Range(1, lim).toList) ++
        Array.fill[Int](1)(lim)
      square.setValues(nums3)
      Then("it is not valid for the filled tiles")
      assert(!square.isValid)
    }
  }
}
