/*
 * Copyright 2017 HM Revenue & Customs
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
import uk.gov.hmrc.play.bootstrap.config.{AppName, BaseUrl}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes

@Singleton
class FrontendAppConfig @Inject() (override val configuration: Configuration) extends AppName with BaseUrl {

  private def loadConfig(key: String) = configuration.getString(key).getOrElse(throw new ConfigException.Missing(s"Missing configuration key: $key"))

  private lazy val contactHost = configuration.getString("contact-frontend.host").getOrElse("")
  private val contactFormServiceIdentifier = "childcarecalculatorfrontend"

  lazy val analyticsToken = loadConfig(s"google-analytics.token")
  lazy val analyticsHost = loadConfig(s"google-analytics.host")
  lazy val reportAProblemPartialUrl = s"$contactHost/contact/problem_reports_ajax?service=$contactFormServiceIdentifier"
  lazy val reportAProblemNonJSUrl = s"$contactHost/contact/problem_reports_nonjs?service=$contactFormServiceIdentifier"
  lazy val betaFeedbackUrl = s"$contactHost/contact/beta-feedback"
  lazy val betaFeedbackUnauthenticatedUrl = s"$contactHost/contact/beta-feedback-unauthenticated"

  lazy val authUrl = baseUrl("auth")
  lazy val loginUrl = loadConfig("urls.login")
  lazy val loginContinueUrl = loadConfig("urls.loginContinue")

  lazy val languageTranslationEnabled = configuration.getBoolean("microservice.services.features.welsh-translation").getOrElse(true)

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy"))

  def routeToSwitchLanguage = (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  def getConfig(key: String): Option[String] = configuration.getString(key)

  lazy val minWorkingHours: Double = configuration.getDouble("workingHours.min").
    getOrElse(throw new ConfigException.Missing("Missing configuration workingHours.min"))

  lazy val maxWorkingHours: Double = configuration.getDouble("workingHours.max").
    getOrElse(throw new ConfigException.Missing("Missing configuration workingHours.max"))

}
