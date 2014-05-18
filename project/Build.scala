import sbt._
import sbt.Keys._

object IntroToScalaBuild extends Build {
    import OpenCL._
    import TestingDeps._
    import AkkaDeps._

    lazy val demo = Project(
    id = "introduction-to-scala",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
        name := "Scala",
        version := "0.1-SNAPSHOT",
        scalaVersion := "2.10.4",
        scalacOptions ++= Seq("-feature", "-deprecation", "-language:postfixOps","-language:higherKinds", "-language:implicitConversions"),
        resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo),
        resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
        scalacOptions in Test ++= Seq("-Yrangepos"),
        libraryDependencies ++= testDeps,
        libraryDependencies ++= Seq(jocl),
        libraryDependencies ++= Seq(actors, actorCluster),
        libraryDependencies ++= Seq(scalaReflect),
        libraryDependencies ++= Seq(actortestkit),
        libraryDependencies ++= Seq(persistence)
        ) )
}
object TestingDeps {
    val scalaTest = "org.scalatest" % "scalatest_2.10" % "2.0.M6" % "test"
    val junit4Interface = "com.novocode" % "junit-interface" % "0.10-M4" % "test"
    val junit4 = "junit" % "junit" % "4.11" % "test"
    val specs2 = "org.specs2" %% "specs2" % "2.3.10" % "test"
    val testDeps = Seq(specs2, scalaTest, junit4, junit4Interface)
}

object AkkaDeps {
    val persistence = "com.typesafe.akka" %% "akka-persistence-experimental" % "2.3.1"
    val actors = "com.typesafe.akka" %% "akka-actor" % "2.3.2"
    val actorCluster = "com.typesafe.akka" %% "akka-cluster" % "2.3.2"
    val actortestkit = "com.typesafe.akka" %% "akka-testkit" % "2.3.2" % "test"
    val scalaReflect =  "org.scala-lang" % "scala-reflect" % "2.10.3"
}

object OpenCL {
    val jocl = "com.nativelibs4java" % "javacl" % "1.0.0-RC3"
}

