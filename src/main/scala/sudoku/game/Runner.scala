package sudoku.game

import sudoku.grid.Board

import scala.io.StdIn._
import scala.util.{Try, Success, Failure}

sealed trait GameEvent
case object Help extends GameEvent
case object NewGame extends GameEvent
case object ExitGame extends GameEvent
case object Guess extends GameEvent
case object ShowValidationTags extends GameEvent
case object ClearValidationTags extends GameEvent
case object Restart extends GameEvent

object Runner {
  private val board: Board = new Board
  private var tagged: Boolean = false
  private val options: Map[Char, GameEvent] = Map(
    'h' -> Help,
    'n' -> NewGame,
    'e' -> ExitGame,
    'g' -> Guess,
    'v' -> ShowValidationTags,
    'c' -> ClearValidationTags,
    'r' -> Restart
  )

  def run(): Unit = {
    this.mainMenu
  }

  private def mainMenu(): Unit = {
    println("Sudoku")
    println(s"\t(h)elp")
    println(s"\t(n)ew")
    println(s"\t(e)xit")
    val opt = this.readOption
    this.processEvent(opt)
  }

  private def gameMenu(): Option[GameEvent] = {
    println(s"Do you want to")
    println("\t(g)uess,")
    if (this.tagged) println("\t(c)lear validation,")
    else println("\t(v)alidate guesses,")
    println("\t(r)estart this game,")
    println("\tstart a (n)ew game or")
    println("\t(e)xit?")
    val opt = this.readOption
    this.processEvent(opt)
  }

  private def readOption(): Try[GameEvent] = Try {
    print("Pick an option: ")
    val opt = readChar
    options(opt.toLower)
  }

  private def processEvent(e: Try[GameEvent]): Option[GameEvent] = {
    e match {
      case Success(action) => {
        action match {
          case Help                => this.showHelp
          case NewGame             => this.play
          case ExitGame            =>
          case Guess               => this.guess
          case ShowValidationTags  => this.tagged = true
          case ClearValidationTags => this.tagged = false; this.board.untag
          case Restart             => this.board.clearGuesses
        }
        return Some(action)
      }
      case Failure(_) => this.mainMenu
    }
    None
  }

  private def showHelp(): Unit = {
    println("Haaaaalp!")
  }

  private def play(): Unit = {
    this.board.initRandom
    this.gameLoop
  }

  private def gameLoop(): Unit = {
    println(this.board)
    if (this.board.isFullyGuessed && this.board.isGuessValid) {
      println("You won!")
      this.mainMenu
    } else {
      this.gameMenu.map { event =>
        event match {
          case ExitGame =>
          case _        => this.gameLoop
        }
      }
    }
  }

  private def guess(): Unit = {
    print(s"Pick a row: ")
    val row = readInt
    print(s"Pick a col: ")
    val col = readInt
    print(s"Pick a number: ")
    val num = readInt
    this.board(row, col).guess(num)
    if (this.tagged) this.board.tag
  }
}
