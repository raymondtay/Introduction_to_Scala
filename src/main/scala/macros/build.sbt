name := "macros"

scalaVersion := "2.11.7"

val macros_implementations = project

lazy val root = (project in file(".")).aggregate(macros_implementations).dependsOn(macros_implementations)

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.7"

libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.11.7"


addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)

