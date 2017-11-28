credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

val hmrcRepoHost = java.lang.System.getProperty("hmrc.repo.host", "https://nexus-preview.tax.service.gov.uk")

resolvers ++= Seq(
  "hmrc-snapshots" at hmrcRepoHost + "/content/repositories/hmrc-snapshots",
  "hmrc-releases" at hmrcRepoHost + "/content/repositories/hmrc-releases",
  "typesafe-releases" at hmrcRepoHost + "/content/repositories/typesafe-releases",
  Resolver.url("hmrc-sbt-plugin-releases", url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns)
)

addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "1.6.0")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.12")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "0.9.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.2.0")

addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.9")

addSbtPlugin("net.ground5hark.sbt" % "sbt-concat" % "0.1.9")

addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "1.0.4")

addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.2")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "0.8.3")

addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "1.0.0")