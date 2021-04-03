package sudoku.grid

import scala.collection.mutable.Set

trait Validator {
  def validate(tiles: Iterable[Tile]): Boolean = {
    tiles
      .map(_.getOptionValue)
      .filter(_.isDefined)
      .groupBy(identity)
      .filter { case (_, v) => v.size > 1 }
      .size == 0
  }

  def validateGuess(tiles: Iterable[Tile]): Boolean = {
    tiles
      .map(_.getOptionGuessOrValue)
      .filter(_.isDefined)
      .groupBy(identity)
      .filter { case (_, v) => v.size > 1 }
      .size == 0
  }

  def unusedValues(
      tiles: Iterable[Tile],
      validValues: Iterable[Int]
  ): Option[Set[Int]] = {
    val allowed: Set[Int] = validValues
      .filterNot(v => tiles.map(_.getValue).exists(_ == v))
      .to[Set]
    allowed.size match {
      case 0 => None
      case _ => Some(allowed)
    }
  }
}
