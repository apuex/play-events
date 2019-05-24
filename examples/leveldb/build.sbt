name := """leveldb"""
organization := "com.example"
version := "1.0-SNAPSHOT"
maintainer := "xtwxy@hotmail.com"

lazy val leveldb = (project in file("."))
  .enablePlugins(PlayScala)

scalaVersion := "2.12.8"
val akkaVersion = "2.5.22"

libraryDependencies ++= Seq(
  guice,
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "com.thesamet.scalapb" %% "scalapb-json4s" % "0.9.0-M1",
  "com.github.apuex" %% "play-events" % "1.0.2",
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-query" % akkaVersion,
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.1" % Test
)

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
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
