import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport.ws

object FrontendBuild extends Build with MicroService {

  val appName = "childcare-calculator-frontend"
  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()

}

private object AppDependencies {
  private val playHealthVersion = "3.10.0-play-26"
  private val bootstrapPlayVersion = "0.36.0"
  private val logbackJsonLoggerVersion = "4.2.0"
  private val govukTemplateVersion = "5.27.0-play-26"
  private val playUiVersion = "7.31.0-play-26"
  private val hmrcTestVersion = "3.4.0-play-26"
  private val scalaTestVersion = "3.0.5"
  private val scalaCheckVersion = "1.13.4"
  private val scalaTestPlusPlayVersion = "2.0.1"
  private val pegdownVersion = "1.6.0"
  private val mockitoCoreVersion = "2.23.4"
  private val httpCachingClientVersion = "8.0.0"
  private val playReactivemongoVersion = "7.14.0-play-26"
  private val playConditionalFormMappingVersion = "0.2.0"
  private val playLanguageVersion = "3.4.0"
  private val taxYearVersion = "0.4.0"
  private val hmrc = "uk.gov.hmrc"
  private val typesafe = "com.typesafe.play"

  val compile: Seq[ModuleID] = Seq(
    ws,
    hmrc %% "simple-reactivemongo" % playReactivemongoVersion,
    hmrc %% "logback-json-logger" % logbackJsonLoggerVersion,
    hmrc %% "govuk-template" % govukTemplateVersion,
    hmrc %% "play-health" % playHealthVersion,
    hmrc %% "play-ui" % playUiVersion,
    hmrc %% "http-caching-client" % httpCachingClientVersion,
    hmrc %% "play-conditional-form-mapping" % playConditionalFormMappingVersion,
    hmrc %% "bootstrap-play-26" % bootstrapPlayVersion,
    hmrc %% "play-language" % playLanguageVersion,
    hmrc %% "tax-year" % taxYearVersion,
    typesafe %% "play-json" % "2.6.13",
    typesafe %% "play-json-joda" % "2.6.13"
  )

  trait TestDependencies {
    lazy val scope: String = "test"
    lazy val test : Seq[ModuleID] = ???
  }

  object Test {
    def apply(): Seq[ModuleID] = new TestDependencies {
      override lazy val test = Seq(
        hmrc %% "hmrctest" % hmrcTestVersion % scope,
        "org.scalatest" %% "scalatest" % scalaTestVersion % scope,
        "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusPlayVersion % scope,
        "org.pegdown" % "pegdown" % pegdownVersion % scope,
        "org.jsoup" % "jsoup" % "1.10.3" % scope,
        typesafe %% "play-test" % PlayVersion.current % scope,
        "org.mockito" % "mockito-core" % mockitoCoreVersion % scope,
        "org.scalacheck" %% "scalacheck" % scalaCheckVersion % scope
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}