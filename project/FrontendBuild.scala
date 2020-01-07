import sbt._
import play.core.PlayVersion
import play.sbt.PlayImport.ws

object FrontendBuild extends Build with MicroService {

  val appName = "childcare-calculator-frontend"
  override lazy val appDependencies: Seq[ModuleID] = AppDependencies()

}

private object AppDependencies {
  private val bootstrapPlayVersion = "1.3.0"
  private val govukTemplateVersion = "5.48.0-play-26"
  private val playUiVersion = "8.5.0-play-26"
  private val hmrcTestVersion = "3.9.0-play-26"
  private val scalaTestVersion = "3.0.8"
  private val scalaCheckVersion = "1.14.0"
  private val scalaTestPlusPlayVersion = "3.1.2"
  private val pegdownVersion = "1.6.0"
  private val mockitoCoreVersion = "3.1.0"
  private val httpCachingClientVersion = "9.0.0-play-26"
  private val playReactivemongoVersion = "7.22.0-play-26"
  private val playConditionalFormMappingVersion = "1.2.0-play-26"
  private val playLanguageVersion = "3.4.0"
  private val taxYearVersion = "0.6.0"
  private val playJavaVersion = "2.6.12"

  private val hmrc = "uk.gov.hmrc"
  private val typesafe = "com.typesafe.play"

  val compile: Seq[ModuleID] = Seq(
    ws,
    hmrc %% "simple-reactivemongo" % playReactivemongoVersion,
    hmrc %% "govuk-template" % govukTemplateVersion,
    hmrc %% "play-ui" % playUiVersion,
    hmrc %% "http-caching-client" % httpCachingClientVersion,
    hmrc %% "play-conditional-form-mapping" % playConditionalFormMappingVersion,
    hmrc %% "bootstrap-play-26" % bootstrapPlayVersion,
    hmrc %% "play-language" % playLanguageVersion,
    hmrc %% "tax-year" % taxYearVersion,
    typesafe %% "play-java" % playJavaVersion,
    typesafe %% "play-json" % "2.7.3",
    typesafe %% "play-json-joda" % "2.7.3"
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
        "org.jsoup" % "jsoup" % "1.12.1" % scope,
        typesafe %% "play-test" % PlayVersion.current % scope,
        "org.mockito" % "mockito-core" % mockitoCoreVersion % scope,
        "org.scalacheck" %% "scalacheck" % scalaCheckVersion % scope
      )
    }.test
  }

  def apply(): Seq[ModuleID] = compile ++ Test()
}