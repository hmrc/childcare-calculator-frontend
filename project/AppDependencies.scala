import sbt._
import play.sbt.PlayImport.ws

object AppDependencies {

  val bootstrapVersion = "10.7.0"
  val playVersion      = "play-30"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"       %% s"bootstrap-frontend-$playVersion" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% s"hmrc-mongo-$playVersion"         % "2.12.0",
    "uk.gov.hmrc"       %% s"play-frontend-hmrc-$playVersion" % "13.7.0",
    "uk.gov.hmrc"       %% "tax-year"                         % "6.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% s"bootstrap-test-$playVersion" % bootstrapVersion % "test"
  )

  def apply(): Seq[ModuleID] = compile ++ test
}
