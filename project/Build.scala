import sbt._
import sbt.Keys._

object IntroToScalaBuild extends Build {
    import ScalaTest._
    import OpenCL._

    lazy val demo = Project(
    id = "introduction-to-scala",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
        name := "Scala",
        version := "0.1-SNAPSHOT",
        scalaVersion := "2.10.0",
        scalacOptions ++= Seq("-feature", "-deprecation", "-language:postfixOps","-language:higherKinds", "-language:implicitConversions"),
        resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
        libraryDependencies ++= Seq(testDeps, jocl)
        ) )
}

object ScalaTest {
    val testDeps = "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"
}

object OpenCL {
    val jocl = "com.nativelibs4java" % "javacl" % "1.0.0-RC3"
}

