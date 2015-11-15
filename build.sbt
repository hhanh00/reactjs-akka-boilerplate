enablePlugins(JavaAppPackaging)

scalaVersion := "2.11.7"

version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % "2.4.0",
	"com.typesafe.akka" %% "akka-slf4j" % "2.4.0",
	"com.typesafe.akka" %% "akka-stream-experimental" % "2.0-M1",
	"com.typesafe.akka" %% "akka-http-core-experimental" % "2.0-M1",
	"com.typesafe.akka" %% "akka-http-experimental" % "2.0-M1",
	"com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.0-M1",
	"org.apache.commons" % "commons-lang3" % "3.3.2",
	"org.scalaz" %% "scalaz-core" % "7.1.3",
	"com.jsuereth" %% "scala-arm" % "1.4",
	"org.slf4j" % "slf4j-api" % "1.7.12",
	"org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.3",
	"org.apache.logging.log4j" % "log4j-api" % "2.3",
	"org.apache.logging.log4j" % "log4j-core" % "2.3",
	"com.typesafe.akka" %% "akka-testkit" % "2.4.0" % "test",
	"org.scalatest" %% "scalatest" % "2.2.4" % "test"
	)

lazy val root = Project("reactjs-boilerplate", file("."))

test in assembly := {}

mainClass in assembly := Some("com.hanh.Server")
