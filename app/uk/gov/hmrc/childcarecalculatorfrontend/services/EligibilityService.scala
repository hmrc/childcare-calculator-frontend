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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import javax.inject.Inject

import org.joda.time.LocalDate
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.EligibilityConnector
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.mappings.UserAnswerToHousehold
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxCredits
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class EligibilityService @Inject()(appConfig: FrontendAppConfig, utils: Utils, tc: TaxCredits, connector: EligibilityConnector) {

  def userAnswerToHousehold = new UserAnswerToHousehold(appConfig, utils, tc)

  def eligibility(answers: UserAnswers)(implicit req: play.api.mvc.Request[_], hc: HeaderCarrier)
            = {

    val household = userAnswerToHousehold.convert(answers)

    val household1 = Household(
      credits = None,
      location = Location.ENGLAND,
      children = List(
        Child(
          id = 1,
          name = "Child 1",
          dob = LocalDate.parse("2011-01-01"),
          disability = Some(
            Disability(
              disabled = false,
              severelyDisabled = false,
              blind = false
            )
          ),
          childcareCost = Some(
            ChildCareCost(
              amount = Some(200),
              period = Some(ChildcarePayFrequency.MONTHLY)
            )
          ),
          education = None
        )
      ),
      parent = Claimant(
        ageRange = Some(AgeEnum.OVERTWENTYFOUR.toString),
        benefits = Some(
          Benefits()
        ),
        lastYearlyIncome = Some(
          Income(
            employmentIncome = Some(25000),
            pension = None,
            otherIncome = None,
            benefits = None,
            statutoryIncome = None
          )
        ),
        currentYearlyIncome = Some(
          Income(
            employmentIncome = Some(25000),
            pension = None,
            otherIncome = None,
            benefits = None,
            statutoryIncome = None
          )
        ),
        hours = Some(37.5),
        minimumEarnings = Some(
          MinimumEarnings(
            amount = 130,
            employmentStatus = None,
            selfEmployedIn12Months = None
          )
        ),
        escVouchers = Some(YesNoUnsureEnum.YES.toString)
      ),
      partner = None
    )

    connector.getEligibility(household).map {
      results =>
        println(s"**********************RESULTS>>>>>>>>$results")
    }


  }

}