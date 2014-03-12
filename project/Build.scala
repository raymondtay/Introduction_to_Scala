import sbt._
import sbt.Keys._

object IntroToScalaBuild extends Build {
    import OpenCL._
    import TestingDeps._

    lazy val demo = Project(
    id = "introduction-to-scala",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
        name := "Scala",
        version := "0.1-SNAPSHOT",
        scalaVersion := "2.10.0",
        scalacOptions ++= Seq("-feature", "-deprecation", "-language:postfixOps","-language:higherKinds", "-language:implicitConversions"),
        resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo),
        resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
        scalacOptions in Test ++= Seq("-Yrangepos"),
        libraryDependencies ++= testDeps,
        libraryDependencies ++= Seq(jocl)
        ) )
}
object TestingDeps {
    val scalaTest = "org.scalatest" % "scalatest_2.10" % "2.0.M6" % "test"
    val junit4Interface = "com.novocode" % "junit-interface" % "0.10-M4" % "test"
    val junit4 = "junit" % "junit" % "4.11" % "test"
    val specs2 = "org.specs2" %% "specs2" % "2.3.10" % "test"
    val testDeps = Seq(specs2, scalaTest, junit4, junit4Interface)
}

object OpenCL {
    val jocl = "com.nativelibs4java" % "javacl" % "1.0.0-RC3"
}

