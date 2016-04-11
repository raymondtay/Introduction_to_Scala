scalaVersion := "2.11.2"

val scalazVersion = "7.1.0"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalaz" %% "scalaz-effect" % scalazVersion,
  "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
  "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion % "test",
  "org.scalacheck" %% "scalacheck" % "1.13.0" // i like this for REPL stuff
  // "org.scalacheck" %% "scalacheck" % "1.13.0" % "test"
)

scalacOptions += "-feature"

initialCommands in console := "import scalaz._, Scalaz._; import tryouts._; import ToIsItTrueOps._; import InstancesOfIsItTrue._"


