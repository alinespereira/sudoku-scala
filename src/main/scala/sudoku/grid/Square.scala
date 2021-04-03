package sudoku.grid

import scala.util.Random
import scala.collection.mutable.Set

sealed class Square(side: Int = 3) extends Iterable[Tile] with Validator {
  private val squareSize = side * side
  private val tiles = Array.fill[Tile](this.squareSize)(new Tile)
  private val validValues = Array.range(1, this.squareSize + 1)

  override def iterator: Iterator[Tile] = tiles.iterator

  def setValues(values: Iterable[Int]) = {
    for ((num, idx) <- values.slice(0, this.squareSize).zipWithIndex) {
      if (validValues.contains(num)) {
        this.tiles(idx) match {
          case null    => this.tiles(idx) = new Tile(num)
          case t: Tile => this.tiles(idx).setValue(num)
        }
      }
    }
  }

  def initEmpty(): Unit = {
    for (i <- 0 until this.squareSize) {
      this.tiles(i) = new Tile
    }
  }

  def initRandom(): Unit = {
    this.setValues(Random.shuffle(this.validValues))
  }

  override def toString(): String = {
    var str = ""

    for (i <- 0 until this.side) {
      for (j <- 0 until this.side) {
        str += s"  ${this(i, j)}  "
      }
      str += "\n"
    }
    str
  }

  def getSize(): Int = {
    this.squareSize
  }

  private def filterTiles(value: Int): Array[Tile] = {
    this.tiles.filter(_.getGuessOrValue == value)
  }

  def allowedValues(): Option[Set[Int]] = {
    this.unusedValues(this.tiles, this.validValues)
  }

  def isValid(): Boolean = {
    this.validate(this.tiles)
  }

  def isValidGuess(): Boolean = {
    this.validateGuess(this.tiles)
  }

  def apply(i: Int, j: Int): Tile = {
    this.tiles(this.side * i + j)
  }
}
