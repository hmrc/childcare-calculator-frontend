import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport.ws

object AppDependencies {

  val bootstrapFrontendVersion = "8.4.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"        %% "bootstrap-frontend-play-30"         % bootstrapFrontendVersion,
    "uk.gov.hmrc.mongo"  %% "hmrc-mongo-play-30"                 % "1.7.0",
    "uk.gov.hmrc"        %% "play-frontend-hmrc-play-30"         % "8.3.0",
    "uk.gov.hmrc"        %% "tax-year"                           % "4.0.0"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        "org.mockito"                   %   "mockito-core"              % "5.5.0",
        "uk.gov.hmrc"                   %%  "bootstrap-test-play-30"    % bootstrapFrontendVersion,
        "com.fasterxml.jackson.module"  %%  "jackson-module-scala"      % "2.15.2",
        "com.github.tomakehurst"        %   "wiremock"                  % "2.35.1",
        "com.github.tomakehurst"        %   "wiremock-jre8"             % "2.35.1",
        "org.scalatestplus"             %%  "scalatestplus-scalacheck"  % "3.1.0.0-RC2",
        "org.pegdown"                   %   "pegdown"                   % "1.6.0",
        "org.jsoup"                     %   "jsoup"                     % "1.16.1",
        "org.mockito"                   %   "mockito-all"               % "1.10.19"
      ).map(_ % scope)
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}