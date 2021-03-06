name := """play-scala-fileupload-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.10"

crossScalaVersions := Seq("2.11.12", "2.12.10")

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.3" % Test
libraryDependencies += ws
libraryDependencies += guice
