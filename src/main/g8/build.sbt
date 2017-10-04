

name := "$appname;format="norm"$"

version := "$version$"

organization in ThisBuild := "$organization$.$project;format="norm"$"

scalaVersion := "$scala_version$"

crossPaths := false

resolvers += Resolver.sonatypeRepo("snapshots")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8", "-language:postfixOps")

import Versions._

Revolver.settings

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % Versions.logbackClassicV,
  "org.squbs" %% "squbs-unicomplex" % Versions.squbsV,
  "org.squbs" %% "squbs-actormonitor" % Versions.squbsV,
  "org.squbs" %% "squbs-actorregistry" % Versions.squbsV,
  "org.squbs" %% "squbs-httpclient" % Versions.squbsV,
  "org.squbs" %% "squbs-admin" % Versions.squbsV,
  "org.json4s" %% "json4s-native" %  Versions.json4sV,
  "de.heikoseeberger" %% "akka-http-json4s" % Versions.akkaHttpJson4sV,
  "org.squbs" %% "squbs-testkit" % Versions.squbsV % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % Versions.akkaHttpV % "test"
)

mainClass in (Compile, run) := Some("org.squbs.unicomplex.Bootstrap")

// enable scalastyle on compile
lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value

(compile in Compile) <<= (compile in Compile) dependsOn compileScalastyle

coverageMinimum := 100

coverageFailOnMinimum := true

xerial.sbt.Pack.packSettings

packMain := Map("run" -> "org.squbs.unicomplex.Bootstrap")

enablePlugins(DockerPlugin)

dockerfile in docker := {
  val jarFile: File = sbt.Keys.`package`.in(Compile, packageBin).value
  val classpath = (managedClasspath in Compile).value
  val mainclass = "org.squbs.unicomplex.Bootstrap"
  val jarTarget = s"/app/\${jarFile.getName}"
  // Make a colon separated classpath with the JAR file
  val classpathString = classpath.files.map("/app/" + _.getName)
    .mkString(":") + ":" + jarTarget
  new Dockerfile {
    // Base image
    from("java")
    // Add all files on the classpath
    add(classpath.files, "/app/")
    // Add the JAR file
    add(jarFile, jarTarget)
    // On launch run Java with the classpath and the main class
    entryPoint("java", "-cp", classpathString, mainclass)
  }
}