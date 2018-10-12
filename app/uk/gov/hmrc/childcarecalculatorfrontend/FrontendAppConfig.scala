/*
 * Copyright 2018 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.childcarecalculatorfrontend

import com.google.inject.{Inject, Singleton}
import com.typesafe.config.ConfigException
import play.api.Configuration
import play.api.i18n.Lang
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.play.bootstrap.config.AppName

@Singleton
class FrontendAppConfig @Inject() (override val configuration: Configuration) extends AppName {

  private def loadConfig(key: String) = configuration.getString(key).getOrElse(throw new ConfigException.Missing(s"Missing configuration key: $key"))

  private lazy val contactHost = loadConfig("contact-frontend.host")
  private val contactFormServiceIdentifier = "childcarecalculatorfrontend"

  lazy val eligibilityUrl =  baseUrl("cc-eligibility") + loadConfig("microservice.services.cc-eligibility.url")

  lazy val analyticsToken = loadConfig(s"google-analytics.token")
  lazy val analyticsHost = loadConfig(s"google-analytics.host")
  lazy val analyticsDimensionKey = loadConfig(s"google-analytics.dimensionKey")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"
  lazy val surveyUrl = loadConfig("feedback-survey-frontend.host")
  lazy val surveyThankYouUrl = loadConfig("feedback-survey-frontend.thankYou")

  lazy val authUrl = baseUrl("auth")
  lazy val loginUrl = loadConfig("urls.login")
  lazy val loginContinueUrl = loadConfig("urls.loginContinue")

  lazy val languageTranslationEnabled = configuration.getBoolean("microservice.services.features.welsh-translation").getOrElse(true)

  private lazy val root: Configuration = {
    configuration.getConfig("microservice.services")
  }.getOrElse(Configuration.empty)

  private lazy val defaultProtocol: String = root.getString("protocol").getOrElse("http")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy"))

  def routeToSwitchLanguage = (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  def getConfig(key: String): Option[String] = configuration.getString(key)

  lazy val minWorkingHours: Double = configuration.getDouble("workingHours.min").
    getOrElse(throw new ConfigException.Missing("Missing configuration workingHours.min"))

  lazy val maxWorkingHours: Double = configuration.getDouble("workingHours.max").
    getOrElse(throw new ConfigException.Missing("Missing configuration workingHours.max"))

  lazy val maxIncome: Double = configuration.getDouble("income.max").
    getOrElse(throw new ConfigException.Missing("Missing configuration income.max"))

  lazy val minIncome: Double = configuration.getDouble("income.min").
    getOrElse(throw new ConfigException.Missing("Missing configuration income.min"))

  lazy val maxEmploymentIncome: Double = configuration.getDouble("employmentIncome.max").
    getOrElse(throw new ConfigException.Missing("Missing configuration employmentIncome.max"))

  lazy val minEmploymentIncome: Double = configuration.getDouble("employmentIncome.min").
    getOrElse(throw new ConfigException.Missing("Missing configuration employmentIncome.min"))

  lazy val minNoWeeksStatPay: Int = configuration.getInt("noWeeksStatPay.min").
    getOrElse(throw new ConfigException.Missing("Missing configuration noWeeksStatPay.min"))

  lazy val maxNoWeeksMaternityPay: Int = configuration.getInt("noWeeksStatPay.maternity").
    getOrElse(throw new ConfigException.Missing("Missing configuration noWeeksStatPay.maternity"))

  lazy val maxNoWeeksPaternityPay: Int = configuration.getInt("noWeeksStatPay.paternity").
    getOrElse(throw new ConfigException.Missing("Missing configuration noWeeksStatPay.paternity"))

  lazy val maxNoWeeksAdoptionPay: Int = configuration.getInt("noWeeksStatPay.adoption").
    getOrElse(throw new ConfigException.Missing("Missing configuration noWeeksStatPay.adoption"))

  lazy val maxNoWeeksSharedParentalPay: Int = configuration.getInt("noWeeksStatPay.sharedParental").
    getOrElse(throw new ConfigException.Missing("Missing configuration noWeeksStatPay.sharedParental"))

  lazy val maxStatutoryPay: Double = configuration.getDouble("statutoryPay.max").
    getOrElse(throw new ConfigException.Missing("Missing configuration statutoryPay.max"))

  lazy val minStatutoryPay: Double = configuration.getDouble("statutoryPay.min").
    getOrElse(throw new ConfigException.Missing("Missing configuration statutoryPay.min"))

  lazy val maxAmountChildren: Double = configuration.getDouble("amountChildren.max").
    getOrElse(throw new ConfigException.Missing("Missing configuration amountChildren.max"))

  lazy val minAmountChildren: Double = configuration.getDouble("amountChildren.min").
    getOrElse(throw new ConfigException.Missing("Missing configuration amountChildren.min"))

  def baseUrl(serviceName: String): String = {

    val config    = root.underlying.getConfig(serviceName)

    val protocol  = Configuration(config).getString("protocol").getOrElse(defaultProtocol)
    val host      = config.getString("host")
    val port      = config.getString("port")

    s"$protocol://$host:$port"
  }
}
