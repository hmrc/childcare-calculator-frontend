import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport.ws

object AppDependencies {

  val bootstrapFrontendVersion = "9.0.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"        %% "bootstrap-frontend-play-30"         % bootstrapFrontendVersion,
    "uk.gov.hmrc.mongo"  %% "hmrc-mongo-play-30"                 % "2.2.0",
    "uk.gov.hmrc"        %% "play-frontend-hmrc-play-30"         % "10.5.0",
    "uk.gov.hmrc"        %% "tax-year"                           % "4.0.0"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        "uk.gov.hmrc"                   %%  "bootstrap-test-play-30"    % bootstrapFrontendVersion,
      ).map(_ % scope)
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}