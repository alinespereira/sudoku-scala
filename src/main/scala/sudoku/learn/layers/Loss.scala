package sudoku.learn.layers

import scala.math.sqrt

import org.platanios.tensorflow.api._
import org.platanios.tensorflow.api.learn.Mode
import org.platanios.tensorflow.api.learn.layers.{Loss}
import org.platanios.tensorflow.api.ops.Math

class SudokuLoss {}
// case class SudokuLoss[
//     Predictions: TF: IsIntOrLong: IsNotQuantized,
//     L: TF: IsIntOrLong
// ](
//     override val name: String
// ) extends Loss[(Output[Predictions], Output[Predictions]), L](name) {
//   override val layerType: String = "SudokuLoss"

//   override def forwardWithoutContext(
//       input: (Output[Predictions], Output[Predictions])
//   )(implicit mode: Mode): Output[L] = {
//     this.boardLoss(input._1).castTo[L]
//   }

//   def boardLoss[T: TF: IsIntOrLong](
//       input: Output[T],
//       name: String = "SudokuLoss"
//   ): Output[T] = {

//     Op.Builder[Output[T], Output[T]](
//       opType = "SudokuLoss",
//       name = name,
//       input = input
//     ).setGradientFn(this.boardLossGradient(_, _)(TF[T], IsIntOrLong[T]))
//       .build()
//       .output
//   }

//   protected def boardLossGradient[T: TF: IsIntOrLong](
//       op: Op[Output[T], Output[T]],
//       outputGradient: Output[T]
//   ): Output[T] = {
//     val sideLength: Int = sqrt(op.input.size).toInt
//     val squareSideLength: Int = sqrt(sideLength).toInt
//     val board = op.input.reshape(Shape(sideLength, sideLength))
//     tf.sum(
//       Tensor((0 until sideLength).map(board(_, ---).unique._1.size))
//     ) +
//       tf.sum(
//         Tensor((0 until sideLength).map(board(---, _).unique._1.size))
//       ) +
//       tf.sum(
//         Tensor(for (i <- 0 until squareSideLength) {
//           for (j <- 0 until squareSideLength) {
//             board(
//               i * squareSideLength :: (i + 1) * squareSideLength,
//               j * squareSideLength :: (j + 1) * squareSideLength
//             ).unique._1.size
//           }
//         })
//       )
//   }
// }
