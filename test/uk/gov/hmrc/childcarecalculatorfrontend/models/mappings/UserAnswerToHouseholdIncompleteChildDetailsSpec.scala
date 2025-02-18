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

package uk.gov.hmrc.childcarecalculatorfrontend.models.mappings

import java.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.SchemeSpec
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{TaxYearInfo, UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.time.TaxYear

class UserAnswerToHouseholdIncompleteChildDetailsSpec extends SchemeSpec with MockitoSugar with BeforeAndAfterEach {

  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))

  val frontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  val utils: Utils = mock[Utils]

  val mockTaxYearInfo: TaxYearInfo = mock[TaxYearInfo]

  val currentTaxYear =  TaxYear.current.startYear

  val previousTaxYear = currentTaxYear - 1

  def userAnswerToHousehold: UserAnswerToHousehold = new UserAnswerToHousehold(frontendAppConfig, utils)

  val currentDate: LocalDate = LocalDate.now()

  override def beforeEach(): Unit = {
    reset(frontendAppConfig, utils,mockTaxYearInfo)
    super.beforeEach()
  }

  "UserAnswerToHousehold" should {
    "convert UserAnswers to Household object specifically when not all children have dob" when {
      "includes all child with dob" in {
        val claimant = Claimant(escVouchers = Some(YesNoUnsureEnum.NO), minimumEarnings =
          Some(MinimumEarnings(0.0, None, None)))

        val child1 = Child(
          id = 0,
          name = "child-1",
          dob = currentDate.minusYears(7),
          disability = None,
          childcareCost = None,
          education = None)

        val child2 = Child(
          id = 1,
          name = "child-2",
          dob = currentDate.minusYears(2),
          disability = None,
          childcareCost = None,
          education = None)

        val child3 = Child(
          id = 2,
          name = "child-3",
          dob = currentDate.minusYears(3),
          disability = None,
          childcareCost = None,
          education = None)

        val expectedHousehold = Household(location = Location.ENGLAND, children = List(child1, child2, child3), parent = claimant)
        val answers = spy(userAnswers())

        when(answers.location) thenReturn Some(Location.ENGLAND)
        when(answers.noOfChildren) thenReturn Some(3)
        when(answers.aboutYourChild(0)) thenReturn Some(AboutYourChild("child-1", currentDate.minusYears(7)))
        when(answers.aboutYourChild(1)) thenReturn Some(AboutYourChild("child-2", currentDate.minusYears(2)))
        when(answers.aboutYourChild(2)) thenReturn Some(AboutYourChild("child-3", currentDate.minusYears(3)))

        userAnswerToHousehold.convert(answers) mustEqual expectedHousehold
      }

      "exclude child with no dob or details" in {
        val claimant = Claimant(escVouchers = Some(YesNoUnsureEnum.NO), minimumEarnings = Some(MinimumEarnings(0.0, None, None)))

        val child1 = Child(
          id = 0,
          name = "child-1",
          dob = currentDate.minusYears(7),
          disability = None,
          childcareCost = None,
          education = None)

        val child2 = Child(
          id = 1,
          name = "child-2",
          dob = currentDate.minusYears(2),
          disability = None,
          childcareCost = None,
          education = None)

        val expectedHousehold = Household(location = Location.ENGLAND, children = List(child1, child2), parent = claimant)
        val answers = spy(userAnswers())

        when(answers.noOfChildren) thenReturn Some(3)
        when(answers.aboutYourChild(0)) thenReturn Some(AboutYourChild("child-1", currentDate.minusYears(7)))
        when(answers.aboutYourChild(1)) thenReturn Some(AboutYourChild("child-2", currentDate.minusYears(2)))
        when(answers.aboutYourChild(2)) thenReturn None

        when(answers.whichChildrenDisability) thenReturn Some(Set(0, 1))

        userAnswerToHousehold.convert(answers) mustEqual expectedHousehold
      }
    }
  }

}
