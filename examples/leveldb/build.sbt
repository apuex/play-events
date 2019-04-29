name := """leveldb"""
organization := "com.example"
version := "1.0-SNAPSHOT"
maintainer   := "xtwxy@hotmail.com"

lazy val leveldb = (project in file("."))
  .enablePlugins(ProtobufPlugin)
  .enablePlugins(PlayScala)

scalaVersion := "2.12.8"
val akkaVersion = "2.5.22"

libraryDependencies ++= Seq(
  guice,
  "com.github.apuex" %% "play-events" % "1.0.0",
  "com.google.protobuf" % "protobuf-java" % "3.7.0",
  "com.google.protobuf" % "protobuf-java-util" % "3.7.0",
  "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.93" % "provided",
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion % "provided",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
)


assemblyJarName in assembly := s"${name.value}-assembly-${version.value}.jar"
mainClass in assembly := Some("play.core.server.ProdServerStart")
fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

assemblyMergeStrategy in assembly := {
  case manifest if manifest.contains("MANIFEST.MF") =>
    // We don't need manifest files since sbt-assembly will create
    // one with the given settings
    MergeStrategy.discard
  case PathList("META-INF", "io.netty.versions.properties") =>
    MergeStrategy.rename
  case referenceOverrides if referenceOverrides.contains("reference-overrides.conf") =>
    // Keep the content for all reference-overrides.conf files
    MergeStrategy.concat
  case x =>
    // For all the other files, use the default sbt-assembly merge strategy
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}
       