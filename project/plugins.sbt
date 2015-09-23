logLevel := Level.Warn

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.7.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.3.3")

addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.1.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-less" % "1.1.0")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"