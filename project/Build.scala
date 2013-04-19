import sbt._
import sbt.Keys._

object IntroToScalaBuild extends Build {
    import ScalaTest._

    lazy val demo = Project(
    id = "introduction-to-scala",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
        name := "Scala",
        version := "0.1-SNAPSHOT",
        scalaVersion := "2.10.0",
        scalacOptions ++= Seq("-feature", "-deprecation", "-language:postfixOps","-language:higherKinds", "-language:implicitConversions"),
        resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
        libraryDependencies ++= testDeps
        ) )
}
object ScalaTest {
    val testDeps = Seq("org.scalatest" % "scalatest_2.10" % "1.9.1" % "test")
}
