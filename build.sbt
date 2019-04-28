import xerial.sbt.Sonatype._

lazy val `play-events` = (project in file("."))
  .enablePlugins(ProtobufPlugin)
  .enablePlugins(play.sbt.routes.RoutesCompiler)
  .enablePlugins(GitVersioning)
  .enablePlugins(SbtTwirl)

name := "play-events"
version      := "1.0.0"
organization := "com.github.apuex"
maintainer   := "xtwxy@hotmail.com"

scalaVersion := "2.12.8"

crossScalaVersions := Seq("2.11.12", "2.12.8")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalacOptions ++= Seq("-unchecked", "-deprecation")

sources in (Compile, play.sbt.routes.RoutesKeys.routes) ++= ((unmanagedResourceDirectories in Compile).value * "playevents.routes").get

sources in (Test, play.sbt.routes.RoutesKeys.routes) ++= ((unmanagedResourceDirectories in Test).value * "routes").get

val akkaVersion = "2.5.22"
val playVersion = "2.7.2"

libraryDependencies ++= Seq(
  "com.google.protobuf" % "protobuf-java-util" % "3.6.1" % "provided",
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.93" % "provided",
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion % "provided",
  "com.typesafe.play" %% "play" % playVersion % "provided",
  "com.typesafe.play" %% "play-test" % playVersion % "test",
  "com.typesafe.play" %% "play-specs2" % playVersion % "test"
)

licenses := Seq("MIT License" -> url("http://opensource.org/licenses/MIT"))

publishTo := sonatypePublishTo.value

publishMavenStyle := true

sonatypeProjectHosting := Some(GitHubHosting("apuex", "play-events", "xtwxy@hotmail.com"))

fork in Test := true

