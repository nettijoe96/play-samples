name := """play-scala-macwire-di-example"""

version := "2.6.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.12"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % "test"
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"
//libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.5.18"
