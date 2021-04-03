package sudoku.grid

import scala.util.{Try, Failure, Success}
import sudoku.exception._

sealed trait TileStatus
case object Initial extends TileStatus
case object Guess extends TileStatus
case object GuessRight extends TileStatus
case object GuessWrong extends TileStatus

sealed trait SortStatus
case object Sorted extends SortStatus

object Tile {
  val hiddenTileStr: String = "-"
  val guessTileStr: String = "?"
  val rightTileStr: String = "✓"
  val wrongTileStr: String = "✗"
}

sealed class Tile {
  private var value: Option[Int] = None
  private var visible: Boolean = false
  private var status: Option[TileStatus] = None
  private var currentGuess: Option[Int] = None
  private var position: Option[Tuple2[Int, Int]] = None
  private var square: Option[Int] = None
  private var sorted: Option[SortStatus] = None

  def this(num: Int) {
    this()
    this.setValue(num)
  }

  def hide(): Unit = {
    this.visible = false
  }

  def show(): Unit = {
    this.visible = true
  }

  def toggleVisibility(): Unit = {
    this.visible = !this.visible
  }

  def setVisibility(v: Boolean): Unit = {
    this.visible = v
  }

  def setInitial(): Try[Int] = {
    this.value match {
      case Some(num) => {
        this.show()
        this.status = Some(Initial)
        Success(num)
      }
      case _ => Failure(SetInitialOnEmptyTile)
    }
  }

  def setInitial(num: Int): Unit = {
    this.setValue(num)
    this.status = Some(Initial)
    this.show
  }

  def setPosition(i: Int, j: Int): Unit = {
    this.position = Some((i, j))
  }

  def setPosition(ij: Tuple2[Int, Int]): Unit = {
    this.position = Some(ij)
  }

  def getPosition(): Tuple2[Int, Int] = {
    this.position.get
  }

  def setSquare(i: Int): Unit = {
    this.square = Some(i)
  }

  def getSquare(): Int = {
    this.square.get
  }

  def sort(): Unit = {
    this.sorted = Some(Sorted)
  }

  def unsort(): Unit = {
    this.sorted = None
  }

  def getSortStatus(): Option[SortStatus] = {
    this.sorted
  }

  def getValue(): Int = {
    this.value.getOrElse(0)
  }

  def getOptionValue(): Option[Int] = {
    this.value
  }

  def setValue(num: Int): Unit = {
    this.value = Some(num)
  }

  def unset(): Unit = {
    this.value = None
    this.status = None
    this.hide
    this.unsort
  }

  def swapValue(other: Tile): Try[Int] = {
    other.getOptionValue match {
      case Some(otherValue) => {
        other.setValue(this.getValue)
        this.setValue(otherValue)
        Success(this.getValue)
      }
      case _ => Failure(SwapWithEmptyTile)
    }

  }

  def getGuessOrValue(): Int = {
    this.currentGuess.getOrElse(this.getValue)
  }

  def getOptionGuessOrValue(): Option[Int] = {
    this.currentGuess.orElse(this.value)
  }

  def guess(num: Int): Try[Int] = {
    this.status match {
      case Some(Initial) =>
        Failure(new IllegalTileGuess)
      case _ => {
        this.currentGuess = Some(num)
        this.status = Some(Guess)
        this.show
        Success(num)
      }
    }
  }

  def getGuess(): Option[Int] = {
    this.currentGuess
  }

  def test(num: Int): Boolean = {
    this.getValue == num
  }

  def check(): Option[TileStatus] = {
    this.currentGuess.flatMap { g =>
      Some(if (this.getValue == g) GuessRight else GuessWrong)
    }
  }

  def tagRight(): Unit = {
    this.status match {
      case Some(Initial) =>
      case _             => this.status = Some(GuessRight)
    }
  }

  def tagWrong(): Unit = {
    this.status match {
      case Some(Initial) =>
      case _             => this.status = Some(GuessWrong)
    }
  }

  private def tag(): String = {
    this.status.get match {
      case Guess      => Tile.guessTileStr
      case GuessWrong => Tile.wrongTileStr
      case GuessRight => Tile.rightTileStr
      case _          => ""
    }
  }

  def untag(): Unit = {
    this.status.map { s =>
      s match {
        case Initial =>
        case _       => this.status = Some(Guess)
      }
    }
  }

  override def toString(): String = {
    this.status match {
      case Some(Initial) => s"[${this.getValue}]"
      case None          => s" ${Tile.hiddenTileStr} "
      case _             => s" ${this.currentGuess.get}${this.tag}"
    }
  }
}
