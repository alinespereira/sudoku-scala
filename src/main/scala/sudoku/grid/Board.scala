package sudoku.grid

import scala.util.Random
import scala.collection.AbstractIterator
import scala.util.{Try, Failure, Success}
import sudoku.exception.CouldNotCompleteBoard
import scala.collection.mutable.Set

sealed trait GameStatus
case object InitialGame extends GameStatus
case object WaitingMove extends GameStatus
case object Tie extends GameStatus
case object GameOver extends GameStatus
case object Win extends GameStatus

sealed class Board(side: Int = 3) extends Iterable[Tile] with Validator {
  self =>
  private val boardSize = side * side
  private val boardTiles = side * side * side * side
  val squares = Array.fill[Square](this.boardSize)(new Square(side))
  private val validValues = (1 to this.boardSize).toArray
  private val indexRange = 0 until this.boardSize

  override def iterator: Iterator[Tile] = new AbstractIterator[Tile] {
    private var current: Int = 0
    def hasNext: Boolean = { current < self.boardTiles }
    def next(): Tile = {
      val i = current / self.boardSize
      val j = current % self.boardSize
      current += 1
      self(i, j)
    }
  }

  private def setTileCoords(): Unit = {
    for (i <- this.indexRange)
      for (j <- this.indexRange)
        this(i, j).setPosition(i, j)
    for ((s, i) <- this.squares.zipWithIndex)
      for (t <- s)
        t.setSquare(i)
  }
  this.setTileCoords

  def initRandom(): Unit = {
    this.map(_.unset)
    val grid = Game.getRandomGrid
    this.setValues(grid)
  }

  def getNextEmptyTile(): Option[Tile] = {
    this.filter(_.getOptionValue.isEmpty) match {
      case List()            => None
      case tiles: List[Tile] => Some(tiles.head)
    }
  }

  def allowedValues(i: Int, j: Int): Option[Set[Int]] = {
    val squareI = i / this.side
    val squareJ = j / this.side

    var values = for {
      square <- this.squares(squareI * this.side + squareJ).allowedValues
      row <- this.unusedValues(this.getRow(i), this.validValues)
      col <- this.unusedValues(this.getCol(i), this.validValues)
    } yield square.intersect(row).intersect(col)

    values.get.size match {
      case 0 => None
      case _ => values
    }
  }

  def getRow(row: Int): IndexedSeq[Tile] = {
    for (j <- this.indexRange) yield this(row, j)
  }

  def getCol(col: Int): IndexedSeq[Tile] = {
    for (i <- this.indexRange) yield this(i, col)
  }

  def isValid(): Boolean = {
    for (i <- this.indexRange) {
      if (!(this.isRowValid(i) && this.isColValid(i)))
        return false
    }
    for (s <- this.squares) {
      if (!s.isValid) {
        return false
      }
    }
    true
  }

  def scoreSummary(): Map[String, List[Int]] = {
    Map(
      "rows" -> this.indexRange.filter(this.isRowValid(_)).toList,
      "columns" -> this.indexRange.filter(this.isColValid(_)).toList,
      "squares" -> this.indexRange.filter(this.squares(_).isValid).toList
    )
  }

  def score(): Int = {
    this.scoreSummary.values.flatten.size
  }

  def isFullValid(): Boolean = {
    this.isFull && this.isValid
  }

  def isRowValid(i: Int): Boolean = {
    this.validate(this.getRow(i))
  }

  def isColValid(i: Int): Boolean = {
    this.validate(this.getCol(i))
  }

  def isTileValid(i: Int, j: Int): Boolean = {
    val squareI = i / this.side
    val squareJ = j / this.side
    this.isRowValid(i) && this
      .isColValid(j) && this.squares(squareI * this.side + squareJ).isValid
  }

  def isTileValid(tile: Tile): Boolean = {
    val (i, j) = tile.getPosition
    val squareI = i / this.side
    val squareJ = j / this.side
    this.isRowValid(i) && this
      .isColValid(j) && this.squares(squareI * this.side + squareJ).isValid
  }

  def isTileGuessValid(tile: Tile): Boolean = {
    val (i, j) = tile.getPosition
    this.validateGuess(this.getRow(i)) &&
    this.validateGuess(this.getCol(j)) &&
    this.squares(tile.getSquare).isValidGuess
  }

  private def isFull(): Boolean = {
    this
      .filter(_.getOptionValue.isEmpty)
      .size == 0
  }

  def isFullyGuessed(): Boolean = {
    this
      .filter(_.getOptionGuessOrValue.isEmpty)
      .size == 0
  }

  def isGuessValid(): Boolean = {
    this.indexRange
      .map { i =>
        this.validateGuess(this.getRow(i)) &&
        this.validateGuess(this.getCol(i)) &&
        this.squares(i).isValidGuess
      }
      .reduce(_ && _)
  }

  def getStatus(): GameStatus = {
    this.isFullyGuessed match {
      case true =>
        this.isGuessValid match {
          case true  => Win
          case false => GameOver
        }
      case false => InitialGame
    }
  }

  def apply(i: Int, j: Int): Tile = {
    val squareI = i / this.side
    val squareJ = j / this.side
    val ii = i % this.side
    val jj = j % this.side
    this.squares(squareI * this.side + squareJ)(ii, jj)
  }

  def tag(): Unit = {
    this.map { tile =>
      tile.getGuess.map { _ =>
        {
          if (this.isTileGuessValid(tile)) tile.tagRight
          else tile.tagWrong
        }
      }

    }
  }

  def untag(): Unit = {
    this.map(_.untag)
  }

  def clearGuesses(): Unit = {
    this.map { tile => tile.getGuess.map { _ => tile.unset } }
  }

  private def setValues(values: Array[Array[Option[Int]]]): Unit = {
    for ((row, i) <- values.zipWithIndex) {
      for ((cell, j) <- row.zipWithIndex) {
        cell match {
          case Some(value) => this(i, j).setInitial(value)
          case None        =>
        }
      }
    }
  }

  override def toString(): String = {
    var str =
      s"""     |${this.indexRange.map(i => s"  $i  ").mkString}\n""" +
        s"""-----|${(0 until 5 * this.indexRange.length)
          .map(_ => "-")
          .mkString}\n"""

    for (i <- this.indexRange) {
      str += s"  ${i}  |"
      for (j <- this.indexRange) {
        str += s" ${this(i, j)} "
      }
      str += "\n"
    }
    str
  }
}
