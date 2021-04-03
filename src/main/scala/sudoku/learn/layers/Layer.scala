package sudoku.learn.layers

import org.platanios.tensorflow.api._
import org.platanios.tensorflow.api.learn.layers._

object SudokuLayer extends Layer[Output[Float], Output[Float]]("SudokuLayer") {
  override val layerType: String = "SudokuLayer"

  override def forwardWithoutContext(
      input: Output[Float]
  )(implicit mode: tf.learn.Mode): Output[Float] = {
    input
  }
}
