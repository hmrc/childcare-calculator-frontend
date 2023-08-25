import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport.ws

object AppDependencies {

  val bootstrapFrontendVersion = "7.15.0"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"        %% "bootstrap-frontend-play-28"         % bootstrapFrontendVersion,
    "uk.gov.hmrc.mongo"  %% "hmrc-mongo-play-28"                 % "1.1.0",
    "uk.gov.hmrc"        %% "play-frontend-hmrc"                 % "7.0.0-play-28",
    "uk.gov.hmrc"        %% "tax-year"                           % "3.1.0",
    "com.typesafe.play"  %% "play-json-joda"                     % "2.9.4"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        "org.mockito"                   %   "mockito-core"              % "5.3.0",
        "uk.gov.hmrc"                   %%  "bootstrap-test-play-28"    % bootstrapFrontendVersion,
        "com.typesafe.play"             %%  "play-test"                 % PlayVersion.current,
        "com.fasterxml.jackson.module"  %%  "jackson-module-scala"      % "2.14.2",
        "com.github.tomakehurst"        %   "wiremock"                  % "2.27.2",
        "com.github.tomakehurst"        %   "wiremock-jre8"             % "2.31.0",
        "com.vladsch.flexmark"          %   "flexmark-all"              % "0.35.10",
        "org.scalatestplus"             %%  "scalatestplus-mockito"     % "1.0.0-M2",
        "org.scalatestplus.play"        %%  "scalatestplus-play"        % "5.1.0",
        "org.scalatestplus"             %%  "scalatestplus-scalacheck"  % "3.1.0.0-RC2",
        "org.pegdown"                   %   "pegdown"                   % "1.6.0",
        "org.jsoup"                     %   "jsoup"                     % "1.15.4",
        "org.mockito"                   %   "mockito-all"               % "1.10.19"
      ).map(_ % scope)
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}