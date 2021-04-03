package sudoku.learn

import scala.util.Random
import java.nio.file.Paths
import org.joda.time.DateTime

import org.platanios.tensorflow.api._
import org.platanios.tensorflow.api.learn._
import org.platanios.tensorflow.api.learn.layers._
import org.platanios.tensorflow.api.learn.hooks._
import org.platanios.tensorflow.api.core.client.FeedMap
import org.platanios.tensorflow.api.learn.estimators.InMemoryEstimator
import org.platanios.tensorflow.api.config.TensorBoardConfig

object TF {
  val random = new Random

  // def main(args: Array[String]): Unit = {
  //   nn
  // }

  def generateData(): Tuple3[Double, Double, Tensor[Float]] = {
    val w = random.nextFloat
    val b = random.nextFloat

    val xData = (0.0 to 10.0 by 0.01).toVector
    val yData = xData.map(w * _ + b + 0.05 * random.nextGaussian)
    val tensorData =
      xData.zip(yData).map { case (x, y) => Tensor(x.toFloat, y.toFloat) }
    (w, b, Tensor(tensorData: _*))
  }

  def customLoss(
      actual: Output[Float],
      predicted: Output[Float]
  ): Output[Float] = {
    tf.sum(predicted)
  }

  def nn(): InMemoryEstimator[Output[
    Float
  ], (Output[Float], Output[Float]), Output[Float], Output[
    Float
  ], Float, (Output[Float], (Output[Float], Output[Float]))] = {
    val (_, _, data) = generateData
    val dataX = tf.data.datasetFromTensorSlices(data(---, 0).expandDims(-1))
    val dataY = tf.data.datasetFromTensorSlices(data(---, 1).expandDims(-1))
    val trainData =
      dataX
        .zip(dataY)
        .repeat()
        .shuffle(100)
        .batch(256)
        .prefetch(10)

// Create the MLP model.
    val input = Input(FLOAT32, Shape(-1, 1))
    val trainInput = Input(FLOAT32, Shape(-1, 1))
    val layer =
      Linear[Float]("OutputLayer", 1)
    // val layer =
    //   Linear[Float]("Layer_0/Weight", 1) >>
    //     AddBias[Float]("Layer_0/bias") >>
    //     Linear[Float]("OutputLayer", 1)
    val loss =
      L2Loss[Float, Float]("Loss/L2") >>
        HistogramSummary("Loss/L2", "Loss") >>
        Mean[Float]("L2/Mean") >>
        ScalarSummary[Float]("Loss/Summary", "Loss")
    val optimizer = tf.train.GradientDescent(1e-6f)
    val model =
      Model.simpleSupervised(input, trainInput, layer, loss, optimizer)

    // Create an estimator and train the model.
    val date = DateTime.now.toString
      .replaceAll("[\\-:]", "")
      .split('.')
      .head
    val summariesDir = Paths.get(s"/tmp/summaries/$date")
    val estimator = InMemoryEstimator(
      modelFunction = model,
      configurationBase = Configuration(Some(summariesDir)),
      stopCriteria = StopCriteria(maxSteps = Some(1000)),
      trainHooks = Set(
        SummarySaver(summariesDir, StepHookTrigger(5)),
        CheckpointSaver(summariesDir, StepHookTrigger(5)),
        LossLogger(trigger = StepHookTrigger(5)),
        StepRateLogger(
          log = false,
          summaryDir = summariesDir,
          trigger = StepHookTrigger(5)
        )
      ),
      tensorBoardConfig = TensorBoardConfig(summariesDir)
    )

    estimator.train(() => trainData)
    estimator
  }

  def linear(): Unit = {
    val inputs = tf.placeholder[Float](Shape(-1, 1))
    val outputs = tf.placeholder[Float](Shape(-1, 1))
    val (predictions, weight, bias) = tf.nameScope("Linear") {
      val weight =
        tf.variable[Float]("weight", Shape(1, 1), tf.ZerosInitializer)
      val bias =
        tf.variable[Float]("bias", Shape(1, 1), tf.ZerosInitializer)
      (inputs * weight + bias, weight, bias)
    }
    val loss = tf.sum(tf.square(predictions - outputs))
    val optimizer = tf.train.AdaGrad(1.0f)
    val trainOp = optimizer.minimize(loss)

    val (w, b, data) = generateData

    val session = Session()
    session.run(targets = tf.globalVariablesInitializer())

    val feedMap = Seq(
      FeedMap(inputs, data(---, 0).expandDims(-1)),
      FeedMap(outputs, data(---, 1).expandDims(-1))
    )

    for (it <- 0 to 100) {
      var (currentLoss, currentWeight, currentBias) = session.run(
        feeds = feedMap,
        fetches = (loss, weight.value, bias.value),
        targets = Set(trainOp)
      )
      if (it % 10 == 0) {
        println(s"Iteration: $it")
        println(s"\tcurrentLoss: ${currentLoss.entriesIterator.toSeq(0)}")
        println(s"\tcurrentWeight: ${currentWeight.entriesIterator.toSeq(0)}")
        println(s"\tcurrentBias: ${currentBias.entriesIterator.toSeq(0)}")
      }
    }

    println(s"weight: $w")
    println(s"bias: $b")
  }
}
