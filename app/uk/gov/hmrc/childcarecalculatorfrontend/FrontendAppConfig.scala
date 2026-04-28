/*
 * Copyright 2023 HM Revenue & Customs
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

import javax.inject.Inject
import play.api.Configuration
import play.api.i18n.Lang
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{eligibleMaxFreeHours, freeHoursForEngland}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import java.time.LocalDate

class FrontendAppConfig @Inject() (config: ServicesConfig, val configuration: Configuration) {

  private def loadConfig(key: String) = config.getString(key)

  private lazy val contactHost             = loadConfig("contact-frontend.host")
  private val contactFormServiceIdentifier = loadConfig("contact-frontend.serviceId")

  lazy val urBannerUrl: String = loadConfig("user-research-banner-url")

  lazy val eligibilityUrl: String =
    config.baseUrl("cc-eligibility") + loadConfig("microservice.services.cc-eligibility.url")

  lazy val betaFeedbackUnauthenticatedUrl =
    s"$contactHost/contact/beta-feedback-unauthenticated?service=$contactFormServiceIdentifier"

  lazy val surveyUrl: String         = loadConfig("feedback-survey-frontend.host")
  lazy val surveyThankYouUrl: String = loadConfig("feedback-survey-frontend.thankYou")

  lazy val adjustedNetIncome: String = loadConfig("urls.adjustedNetIncome")

  lazy val languageTranslationEnabled: Boolean = config.getBoolean("microservice.services.features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  lazy val minWorkingHours: Double = config.getString("workingHours.min").toDouble
  lazy val maxWorkingHours: Double = config.getString("workingHours.max").toDouble

  lazy val maxIncome: Double = config.getString("income.max").toDouble
  lazy val minIncome: Double = config.getString("income.min").toDouble

  lazy val maxEmploymentIncome: Double = config.getString("employmentIncome.max").toDouble
  lazy val minEmploymentIncome: Double = config.getString("employmentIncome.min").toDouble

  lazy val maxAmountChildren: Int = config.getInt("amountChildren.max")
  lazy val minAmountChildren: Int = config.getInt("amountChildren.min")

  lazy val navigationAudit: Boolean = config.getBoolean("feature.navigationAudit")

  lazy val maxFreeHoursCutoff: LocalDate = LocalDate.parse(config.getString("freeHours.maxFreeHoursCutoff"))

  // This scheme overlaps the max hours scheme, after the date for allowMaxFreeHoursFromNineMonths they will become the same
  def maxFreeHoursAmount: Double = if (allowMaxFreeHoursFromNineMonths) eligibleMaxFreeHours else freeHoursForEngland

  def allowMaxFreeHoursFromNineMonths: Boolean = !LocalDate.now().isBefore(maxFreeHoursCutoff)
}
