package sudoku.grid

import scala.io.Source
import scala.collection.mutable.Map
import scala.util.Random

object Game {
  val grids: Map[String, Array[Array[Option[Int]]]] =
    Map[String, Array[Array[Option[Int]]]]()
  val random = new Random
  this.parseGrids

  def getRandomGrid(): Array[Array[Option[Int]]] = {
    this.grids(this.getRandomGridName)
  }

  private def getRandomGridName(): String = {
    this.grids.keys.toVector(this.random.nextInt(this.grids.size))
  }

  private def parseGrids(): Unit = {
    for (data <- Source.fromResource("sudoku.txt").getLines.grouped(10)) {
      this.grids(data.head) = data.tail.map { row =>
        row
          .map(_.toString.toInt)
          .map(n => if (n == 0) None else Some(n))
          .toArray
      }.toArray
    }
  }
}
