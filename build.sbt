import xerial.sbt.Sonatype._

lazy val `play-events` = (project in file("."))
  .enablePlugins(play.sbt.routes.RoutesCompiler)
  .enablePlugins(GitVersioning)
  .enablePlugins(SbtTwirl)

name := "play-events"
version      := "1.0.2"
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
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.93" % "provided",
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion % "provided",
  "com.typesafe.play" %% "play" % playVersion % "provided",
  "com.typesafe.play" %% "play-test" % playVersion % "test",
  "com.typesafe.play" %% "play-specs2" % playVersion % "test"
)

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)

fork in Test := true

publishTo := sonatypePublishTo.value
publishMavenStyle := true

licenses := Seq("GPL3" -> url("https://www.gnu.org/licenses/gpl-3.0.txt"))

sonatypeProfileName := "com.github.apuex"


sonatypeProjectHosting := Some(GitHubHosting("apuex", "play-events", "xtwxy@hotmail.com"))

homepage := Some(url("https://github.com/apuex/play-events"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/apuex/play-events.git"),
    "scm:git@github.com:apuex/play-events.git"
  )
)

developers := List(
  Developer(id="apuex", name="Wangxy", email="xtwxy@hotmail.com", url=url("https://github.com/apuex"))
)

/*
Command Line Usage

Publish a GPG-signed artifact to Sonatype:

$ sbt publishSigned

Do close and promote at once:

$ sbt sonatypeRelease
*/




