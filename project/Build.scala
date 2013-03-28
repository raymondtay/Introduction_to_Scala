import sbt._
import sbt.Keys._

object IntroToScalaBuild extends Build {
    lazy val demo = Project(
    id = "introduction-to-scala",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
        name := "Scala",
        version := "0.1-SNAPSHOT",
        scalaVersion := "2.10.0",
        scalacOptions ++= Seq("-feature", "-deprecation", "-language:postfixOps"),
        resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
        ) )
}

