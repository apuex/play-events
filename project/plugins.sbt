
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.2")
//addSbtPlugin("com.github.gseitz" % "sbt-protobuf" % "0.6.3")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.20")
libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.9.0-M5"

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "2.0.0-M2")
