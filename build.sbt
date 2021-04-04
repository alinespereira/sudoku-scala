ThisBuild / scalaVersion := "2.12.10"

Global / onChangedBuildSource := ReloadOnSourceChanges

val TensorflowVersion = "0.4.1"
val JodaTimeVersion = "2.10.10"
val AkkaVersion = "2.6.13"

lazy val sudoku = (project in file("."))
  .settings(
    name := "Sudoku",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "3.0.5" % Test,
      "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
      "org.platanios" %% "tensorflow" % TensorflowVersion classifier "darwin-cpu-x86_64",
      "joda-time" % "joda-time" % JodaTimeVersion,
      "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion
    )
  )

mainClass in (Compile, run) := Some("sudoku.ui.MainGame")
