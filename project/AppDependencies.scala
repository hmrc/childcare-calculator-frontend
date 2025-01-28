
import sbt.*
import play.sbt.PlayImport.ws

object AppDependencies {

  private val bootstrapFrontendVersion = "9.7.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"        %% "bootstrap-frontend-play-30"         % bootstrapFrontendVersion,
    "uk.gov.hmrc.mongo"  %% "hmrc-mongo-play-30"                 % "2.3.0",
    "uk.gov.hmrc"        %% "play-frontend-hmrc-play-30"         % "11.8.0",
    "uk.gov.hmrc"        %% "tax-year"                           % "5.0.0"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapFrontendVersion % Test
  )

  val all: Seq[ModuleID] = compile ++ test
}
