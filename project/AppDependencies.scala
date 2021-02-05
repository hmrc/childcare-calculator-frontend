import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport.ws

object AppDependencies {
  private val hmrc = "uk.gov.hmrc"
  private val typesafe = "com.typesafe.play"

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"        %% "bootstrap-frontend-play-27"         % "3.0.0",
    "uk.gov.hmrc"        %% "simple-reactivemongo"               % "7.31.0-play-27",
    "uk.gov.hmrc"        %% "govuk-template"                     % "5.61.0-play-27",
    "uk.gov.hmrc"        %% "play-ui"                            % "8.20.0-play-27",
    "uk.gov.hmrc"        %% "http-caching-client"                % "9.2.0-play-27",
    "uk.gov.hmrc"        %% "play-conditional-form-mapping"      % "1.5.0-play-27",
    "uk.gov.hmrc"        %% "play-language"                      % "4.10.0-play-27",
    "uk.gov.hmrc"        %% "tax-year"                           % "1.1.0",
    "com.typesafe.play"  %% "play-json-joda"                     % "2.6.14"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        "org.scalatest"             %% "scalatest"                % "3.0.9"                 % scope,
        "org.scalatestplus.play"    %% "scalatestplus-play"       % "4.0.3"                 % scope,
        "org.pegdown"               % "pegdown"                   % "1.6.0"                 % scope,
        "org.jsoup"                 % "jsoup"                     % "1.13.1"                % scope,
        "com.typesafe.play"         %% "play-test"                % PlayVersion.current     % scope,
        "org.mockito"               % "mockito-core"              % "3.3.3"                 % scope,
        "org.scalacheck"            %% "scalacheck"               % "1.14.3"                % scope
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}