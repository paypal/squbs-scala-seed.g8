logLevel := Level.Warn

addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")

addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.12")

addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.5.0")

resolvers += Resolver.sonatypeRepo("releases")