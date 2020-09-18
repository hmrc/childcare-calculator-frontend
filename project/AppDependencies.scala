import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport.ws

object AppDependencies {
  private val hmrc = "uk.gov.hmrc"
  private val typesafe = "com.typesafe.play"

  val compile: Seq[ModuleID] = Seq(
    ws,
    hmrc        %% "simple-reactivemongo"               % "7.30.0-play-26",
    hmrc        %% "govuk-template"                     % "5.55.0-play-26",
    hmrc        %% "play-ui"                            % "8.12.0-play-26",
    hmrc        %% "http-caching-client"                % "9.1.0-play-26",
    hmrc        %% "play-conditional-form-mapping"      % "1.2.0-play-26",
    hmrc        %% "bootstrap-play-26"                  % "1.11.0",
    hmrc        %% "play-language"                      % "4.3.0-play-26",
    hmrc        %% "tax-year"                           % "1.1.0",
    typesafe    %% "play-json-joda"                     % "2.9.0"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        hmrc                        %% "hmrctest"                 % "3.9.0-play-26"         % scope,
        "org.scalatest"             %% "scalatest"                % "3.0.8"                 % scope,
        "org.scalatestplus.play"    %% "scalatestplus-play"       % "3.1.3"                 % scope,
        "org.pegdown"               % "pegdown"                   % "1.6.0"                 % scope,
        "org.jsoup"                 % "jsoup"                     % "1.13.1"                % scope,
        typesafe                    %% "play-test"                % PlayVersion.current     % scope,
        "org.mockito"               % "mockito-core"              % "3.3.3"                 % scope,
        "org.scalacheck"            %% "scalacheck"               % "1.14.3"                % scope
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}