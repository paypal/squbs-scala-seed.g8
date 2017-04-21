

name := "squbs-seed"

version := "0.0.1-SNAPSHOT"

organization in ThisBuild := "org.squbs.sample"

scalaVersion := "2.11.8"

crossPaths := false

resolvers += Resolver.sonatypeRepo("snapshots")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8", "-language:postfixOps")

val squbsV = "0.9.0-SNAPSHOT"

val akkaV = "2.4.16"

val akkaHttpV = "10.0.1"

Revolver.settings

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "org.squbs" %% "squbs-unicomplex" % squbsV,
  "org.squbs" %% "squbs-actormonitor" % squbsV,
  "org.squbs" %% "squbs-actorregistry" % squbsV,
  "org.squbs" %% "squbs-httpclient" % squbsV,
  "org.squbs" %% "squbs-admin" % squbsV,
  "org.json4s" %% "json4s-native" % "3.5.0",
  "de.heikoseeberger" %% "akka-http-json4s" % "1.11.0",
  "org.squbs" %% "squbs-testkit" % squbsV % "test",
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV % "test"
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
  val jarTarget = s"/app/${jarFile.getName}"
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