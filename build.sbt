

name := "squbs-seed"

version := "0.0.1-SNAPSHOT"

organization in ThisBuild := "org.squbs.sample"

scalaVersion := "2.11.7"

crossPaths := false

resolvers += Resolver.sonatypeRepo("snapshots")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8", "-language:postfixOps")

val squbsV = "0.7.1-SNAPSHOT"

val akkaV = "2.3.13"

Revolver.settings

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "org.squbs" %% "squbs-unicomplex" % squbsV,
  "org.squbs" %% "squbs-actormonitor" % squbsV,
  "org.squbs" %% "squbs-httpclient" % squbsV,
  "org.squbs" %% "squbs-admin" % squbsV,
  "org.squbs" %% "squbs-testkit" % squbsV % "test",
  "io.spray" %% "spray-testkit" % "1.3.3" % "test"
)

mainClass in (Compile, run) := Some("org.squbs.unicomplex.Bootstrap")

// enable scalastyle on compile
lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value

(compile in Compile) <<= (compile in Compile) dependsOn compileScalastyle

coverageMinimum := 100

coverageFailOnMinimum := true
