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

package uk.gov.hmrc.childcarecalculatorfrontend.models.mappings

import org.joda.time.LocalDate
import org.mockito.Matchers.any
import org.scalatest.{EitherValues, OptionValues}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.scalatestplus.play.PlaySpec
import play.api.inject.Injector
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.SchemeSpec
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.cache.client.CacheMap
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig

class UserAnswerToHouseholdSpec extends SchemeSpec with MockitoSugar {

  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))
  val mockUtils = mock[Utils]
  val frontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]

  val cy: Int = LocalDate.now.getYear
  def userAnswerToHousehold = new UserAnswerToHousehold(frontendAppConfig, mockUtils)

  "UserAnswerToHousehold" must {

    "given a user input with location" in {
      val household = Household(location = Location.ENGLAND)
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.ENGLAND)

      userAnswerToHousehold.convert(answers) mustEqual household
    }

    "given a user input with location and tax credits" in {
      val household = Household(credits = Some(CreditsEnum.TAXCREDITS.toString), location = Location.SCOTLAND)
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.SCOTLAND)
      when(answers.taxOrUniversalCredits) thenReturn Some(CreditsEnum.TAXCREDITS.toString)

      userAnswerToHousehold.convert(answers) mustEqual household
    }

    "given a user input with parent" in {
      val parent = Claimant(
        hours = Some(BigDecimal(54.9)),
        ageRange = Some(AgeEnum.OVERTWENTYFOUR.toString),
        minimumEarnings = Some(MinimumEarnings(120.0)),
        maximumEarnings = Some(false)
      )
      val household = Household(location = Location.SCOTLAND, parent = parent)
      val answers = spy(userAnswers())
      //      ageRange: Option[AgeEnum] = None,
      //      benefits: Option[Benefits] = None,
      //      lastYearlyIncome: Option[Income] =   None,
      //      currentYearlyIncome: Option[Income] = None,
      //      hours: Option[BigDecimal] = None,
      //      minimumEarnings: Option[MinimumEarnings] = None,
      //      escVouchers: Option[YesNoUnsureEnum] = None,
      //      maximumEarnings: Option[Boolean] = None

      when(answers.location) thenReturn Some(Location.SCOTLAND)
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.parentWorkHours) thenReturn Some(BigDecimal(54.9))
      when(answers.yourAge) thenReturn Some(AgeEnum.OVERTWENTYFOUR.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(true)
      when(answers.yourMaximumEarnings) thenReturn Some(false)
      when(mockUtils.getEarningsForAgeRange(any(), any(), any())).thenReturn(120)

      userAnswerToHousehold.convert(answers) mustEqual household
    }


  }
}
