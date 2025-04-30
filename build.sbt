import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, scalaSettings}
import scoverage._

lazy val appName = "childcare-calculator-frontend"

lazy val appDependencies: Seq[ModuleID] = ???
lazy val plugins: Seq[Plugins]          = Seq.empty
lazy val playSettings: Seq[Setting[_]]  = Seq.empty

lazy val scoverageSettings: Seq[Def.Setting[_]] =
  Seq(
    ScoverageKeys.coverageExcludedFiles := "<empty>;Reverse.*;.*filters.*;.*handlers.*;.*components.*;.*models.*;" +
      ".*BuildInfo.*;.*javascript.*;.*FrontendAuditConnector.*;.*Routes.*;.*GuiceInjector;.*DataCacheConnector;" +
      ".*ControllerConfiguration;.*LanguageSwitchController;.*repositories.*",
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum    := true,
    ScoverageKeys.coverageHighlighting     := true
  )

ThisBuild / majorVersion := 1
ThisBuild / scalaVersion := "2.13.16"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(Seq(play.sbt.PlayScala, SbtDistributablesPlugin) ++ plugins: _*)
  .disablePlugins(JUnitXmlReportPlugin) // this is an experimental plugin that is (currently) enabled by default and prevents deployment to QA environment
  .settings(playSettings: _*)
  .settings(PlayKeys.playDefaultPort := 9381)
  .settings(RoutesKeys.routesImport ++= Seq("uk.gov.hmrc.childcarecalculatorfrontend.models._"))
  .settings(scalaSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(scoverageSettings: _*)
  .settings(
    scalacOptions ++= Seq(
      "-feature",
      "-Wconf:cat=unused-imports&src=routes/.*:s",
      "-Wconf:cat=unused-imports&src=html/.*:s"
    ),
    libraryDependencies ++= AppDependencies(),
    retrieveManaged                 := true,
    update / evictionWarningOptions := EvictionWarningOptions.default.withWarnScalaVersionEviction(false),
    resolvers ++= Seq(
      Resolver.jcenterRepo
    )
  )
  .settings(
    pipelineStages := Seq(digest)
  )
  .settings(
    TwirlKeys.templateImports ++= Seq(
      "uk.gov.hmrc.govukfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.components._",
      "uk.gov.hmrc.hmrcfrontend.views.html.helpers._",
      "uk.gov.hmrc.govukfrontend.views.html.components.implicits._",
      "uk.gov.hmrc.childcarecalculatorfrontend.views.html._",
      "uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes",
      "uk.gov.hmrc.childcarecalculatorfrontend.controllers"
    )
  )
