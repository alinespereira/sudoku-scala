// package sudoku.grid.future

// import scala.util.Random
// import scala.collection.AbstractIterator
// import scala.util.{Try, Failure, Success}
// import sudoku.exception.CouldNotCompleteBoard
// import scala.collection.mutable.Set

// sealed trait GameStatus
// case object InitialGame extends GameStatus
// case object WaitingMove extends GameStatus
// case object Tie extends GameStatus
// case object GameOver extends GameStatus
// case object Win extends GameStatus

// sealed class Board(side: Int = 3) extends Iterable[Tile] with Validator {
//   self =>
//   private val boardSize = side * side
//   private val boardTiles = side * side * side * side
//   val squares = Array.fill[Square](this.boardSize)(new Square(side))
//   private val validValues = (1 to this.boardSize).toArray
//   private val indexRange = 0 until this.boardSize

//   override def iterator: Iterator[Tile] = new AbstractIterator[Tile] {
//     private var current: Int = 0
//     def hasNext: Boolean = { current < self.boardTiles }
//     def next(): Tile = {
//       val i = current / self.boardSize
//       val j = current % self.boardSize
//       current += 1
//       self(i, j)
//     }
//   }

//   private def setTileCoords(): Unit = {
//     for (i <- this.indexRange)
//       for (j <- this.indexRange)
//         this(i, j).setPosition(i, j)
//     for ((s, i) <- this.squares.zipWithIndex)
//       for (t <- s)
//         t.setSquare(i)
//   }
//   this.setTileCoords

//   def initRandom(): Unit = {
//     this.fillDiagonal
//     // for (i <- this.indexRange) {
//     //   this.sortLine(this.getRow(i))
//     //   this.sortLine(this.getCol(i))
//     // }
//     this.fillEmptyRandom
//   }

//   def sortLine(line: IndexedSeq[Tile]): Unit = {
//     val registered = Set[Int]()
//     line.zipWithIndex
//       .map {
//         case (tile, idx) => {
//           if (registered.add(tile.getValue)) {
//             tile.sort
//             tile.setInitial
//           } else {
//             this.boxAndAdjacentSwap(tile, registered) match {
//               case Some(swap) => registered.add(swap)
//               case _          =>
//             }
//           }
//         }
//       }
//   }

//   def boxAndAdjacentSwap(tile: Tile, registered: Set[Int]): Option[Int] = {
//     val other =
//       this
//         .squares(tile.getSquare)
//         .filter { t =>
//           t.getSortStatus.isEmpty &&
//           t.getOptionValue.isDefined &&
//           t != tile &&
//           !registered.contains(t.getValue)
//         }
//     if (other.size > 0) {
//       tile.swapValue(other.head) match {
//         case Success(num) => {
//           tile.sort
//           Some(num)
//         }
//         case _ => {
//           tile.unset
//           None
//         }
//       }
//     } else {
//       tile.unset
//       None
//     }
//   }

//   def fillRandom(): Unit = {
//     val numbers = Random.shuffle(
//       (1 to this.boardSize)
//         .map(_ => this.validValues)
//         .flatten
//         .toList
//     )
//     for ((tile, num) <- this.zip(numbers)) {
//       tile.unsort
//       tile.setValue(num)
//       tile.setInitial
//     }
//   }

//   def fillDiagonal(): Unit = {
//     val diagSquares = (0 until this.boardSize by this.side + 1).toSet
//     for (s <- diagSquares) {
//       this.squares(s).initRandom
//       this.squares(s).foreach(_.show)
//     }
//   }

//   def fillEmptyRandom(): Unit = {
//     this.filter(_.getOptionValue.isEmpty).map { tile =>
//       {
//         val (i, j) = tile.getPosition
//         this.allowedValues(i, j) match {
//           case Some(values) => {
//             tile.setValue(Random.shuffle(values).head)
//             tile.setInitial
//           }
//           case None =>
//         }
//       }
//     }
//   }

//   def fillNext(i: Int, j: Int): Boolean = {
//     this.allowedValues(i, j) match {
//       case Some(values) => {
//         this(i, j).setValue(Random.shuffle(values).head)
//         this(i, j).show

//         this.getNextEmptyTile match {
//           case Some(tile) => {
//             val (nextI, nextJ) = tile.getPosition
//             if (this.fillNext(nextI, nextJ)) return true
//             this(i, j).unset
//           }
//           case _ =>
//         }
//       }
//       case None => false
//     }
//     false
//   }

//   def getNextEmptyTile(): Option[Tile] = {
//     this.filter(_.getOptionValue.isEmpty) match {
//       case List()            => None
//       case tiles: List[Tile] => Some(tiles.head)
//     }
//   }

//   // def fillNextRandom(i: Int, j: Int): Unit = {
//   //   this(i, j).show
//   //   val allowedValues = Random.shuffle(this.allowedValues(i, j).toList)
//   //   println(s"""$i, $j: ${allowedValues.mkString(" - ")}""")
//   //   for (num <- allowedValues) {
//   //     this(i, j).setValue(num)
//   //     if (this.isValid) {
//   //       return
//   //     }
//   //   }

//   //     this(i, j).unset
//   //     val ii = (i * this.boardSize + j - 1) / this.boardSize
//   //     val jj = (i * this.boardSize + j - 1) % this.boardSize
//   //     this.fillNextRandom(ii, jj)
//   // }

//   // def fillNextRandom(): Unit = {
//   //   val rnd = new Random
//   //   for (i <- this.indexRange) {
//   //     for (j <- this.indexRange) {
//   //       this(i, j).getOptionValue match {
//   //         case None => {
//   //           val allowedValues = Random.shuffle(this.allowedValues(i, j).toList)
//   //           for (num <- allowedValues) {
//   //             this(i, j).setValue(num)
//   //             this.isTileValid(i, j) match {
//   //               case true  => this.fillNextRandom
//   //               case false => this(i, j).unset
//   //             }
//   //           }
//   //         }
//   //         case _ => {}
//   //       }
//   //       this(i, j).getOptionValue match {
//   //         case Some(_) => this(i, j).show
//   //         case _       => {}
//   //       }
//   //     }
//   //   }
//   // }

//   def allowedValues(i: Int, j: Int): Option[Set[Int]] = {
//     val squareI = i / this.side
//     val squareJ = j / this.side

//     var values = for {
//       square <- this.squares(squareI * this.side + squareJ).allowedValues
//       row <- this.unusedValues(this.getRow(i), this.validValues)
//       col <- this.unusedValues(this.getCol(i), this.validValues)
//     } yield square.intersect(row).intersect(col)

//     values.get.size match {
//       case 0 => None
//       case _ => values
//     }
//   }

//   def getRow(row: Int): IndexedSeq[Tile] = {
//     for (j <- this.indexRange) yield this(row, j)
//   }

//   def getCol(col: Int): IndexedSeq[Tile] = {
//     for (i <- this.indexRange) yield this(i, col)
//   }

//   def isValid(): Boolean = {
//     for (i <- this.indexRange) {
//       if (!(this.isRowValid(i) && this.isColValid(i)))
//         return false
//     }
//     for (s <- this.squares) {
//       if (!s.isValid) {
//         return false
//       }
//     }
//     true
//   }

//   def scoreSummary(): Map[String, List[Int]] = {
//     Map(
//       "rows" -> this.indexRange.filter(this.isRowValid(_)).toList,
//       "columns" -> this.indexRange.filter(this.isColValid(_)).toList,
//       "squares" -> this.indexRange.filter(this.squares(_).isValid).toList
//     )
//   }

//   def score(): Int = {
//     this.scoreSummary.values.flatten.size
//   }

//   def isFullValid(): Boolean = {
//     this.isFull && this.isValid
//   }

//   def isRowValid(i: Int): Boolean = {
//     this.validate(this.getRow(i))
//   }

//   def isColValid(i: Int): Boolean = {
//     this.validate(this.getCol(i))
//   }

//   def isTileValid(i: Int, j: Int): Boolean = {
//     val squareI = i / this.side
//     val squareJ = j / this.side
//     this.isRowValid(i) && this
//       .isColValid(j) && this.squares(squareI * this.side + squareJ).isValid
//   }

//   private def isFull(): Boolean = {
//     this
//       .filter(_.getOptionValue.isEmpty)
//       .size == 0
//   }

//   def isFullyGuessed(): Boolean = {
//     this
//       .filter(_.getOptionGuessOrValue.isEmpty)
//       .size == 0
//   }

//   def isGuessValid(): Boolean = {
//     this.indexRange
//       .map { i =>
//         !(this.validateGuess(this.getRow(i)) &&
//           this.validateGuess(this.getCol(i)) &&
//           this.squares(i).isValidGuess)
//       }
//       .reduce(_ && _)
//   }

//   def getStatus(): GameStatus = {
//     this.isFullyGuessed match {
//       case true =>
//         this.isGuessValid match {
//           case true  => Win
//           case false => GameOver
//         }
//       case false => InitialGame
//     }
//   }

//   def apply(i: Int, j: Int): Tile = {
//     val squareI = i / this.side
//     val squareJ = j / this.side
//     val ii = i % this.side
//     val jj = j % this.side
//     this.squares(squareI * this.side + squareJ)(ii, jj)
//   }

//   override def toString(): String = {
//     var str = ""

//     for (i <- this.indexRange) {
//       for (j <- this.indexRange) {
//         str += s"  ${this(i, j)}  "
//       }
//       str += "\n"
//     }
//     str
//   }
// }
