name := """play-scala-macwire-di-example"""

version := "2.6.x"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.12"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % "test"
libraryDependencies += "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided"

libraryDependencies += "org.reactivemongo" %% "play2-reactivemongo" % "0.12.4" excludeAll ExclusionRule("com.typesafe.play", "play-iteratees_2.11")
libraryDependencies +=  "com.typesafe.play" %% "play-ws" % "2.5.18"
libraryDependencies +=  "com.typesafe.play" %% "play" % "2.5.18"
