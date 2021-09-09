import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport.ws

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"        %% "bootstrap-frontend-play-28"         % "5.4.0",
    "uk.gov.hmrc"        %% "simple-reactivemongo"               % "8.0.0-play-28",
    "uk.gov.hmrc"        %% "govuk-template"                     % "5.68.0-play-28",
    "uk.gov.hmrc"        %% "play-ui"                            % "9.6.0-play-28",
    "uk.gov.hmrc"        %% "play-frontend-hmrc"                 % "0.88.0-play-28",
    "uk.gov.hmrc"        %% "http-caching-client"                % "9.5.0-play-28",
    "uk.gov.hmrc"        %% "play-conditional-form-mapping"      % "1.9.0-play-28",
    "uk.gov.hmrc"        %% "play-language"                      % "5.1.0-play-28",
    "uk.gov.hmrc"        %% "tax-year"                           % "1.1.0",
    "com.typesafe.play"  %% "play-json-joda"                     % "2.9.2"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        "org.mockito"                   %   "mockito-core"              %   "3.11.2"           % scope,
        "com.typesafe.play"             %%  "play-test"                 % PlayVersion.current % scope,
        "com.fasterxml.jackson.module"  %%  "jackson-module-scala"      % "2.12.3"            % scope,
        "com.github.tomakehurst"        %   "wiremock"                  % "2.27.2"            % scope,
        "com.github.tomakehurst"        %   "wiremock-jre8"             % "2.28.1"            % scope,
        "com.vladsch.flexmark"          %   "flexmark-all"              % "0.35.10"           % scope,
        "org.scalatestplus"             %%  "scalatestplus-mockito"     % "1.0.0-M2"          % scope,
        "org.scalatestplus.play"        %%  "scalatestplus-play"        % "5.1.0"             % scope,
        "org.scalatestplus"             %%  "scalatestplus-scalacheck"  % "3.1.0.0-RC2"       % scope,
        "org.pegdown"                   %   "pegdown"                   % "1.6.0"             % scope,
        "org.jsoup"                     %   "jsoup"                     % "1.13.1"            % scope,
        "org.mockito"                   %   "mockito-all"               % "1.10.19"           % scope
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}