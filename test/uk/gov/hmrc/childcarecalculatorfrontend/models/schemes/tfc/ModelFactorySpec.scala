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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc

import org.mockito.Mockito._
import org.scalatest.{MustMatchers, OptionValues}
import uk.gov.hmrc.childcarecalculatorfrontend.models.SelfEmployedOrApprenticeOrNeitherEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.SchemeSpec
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class ModelFactorySpec extends SchemeSpec with MustMatchers with OptionValues {
  
  val factory = new ModelFactory

  ".apply" must {

    "return `None` when `doYouLiveWithPartner` is undefined" in {
      val answers = spy(helper())
      when(answers.areYouInPaidWork) thenReturn Some(true)
      when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
      when(answers.doYouGetAnyBenefits) thenReturn Some(true)
      when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
      factory(answers) mustNot be(defined)
    }

    "single user" when {

      "return 'Some' when a user is self employed for less than 12 months" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.doYouGetAnyBenefits) thenReturn Some(false)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        when(answers.yourSelfEmployed) thenReturn Some(true)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString)
        factory(answers).value mustEqual SingleHousehold(Parent(minEarnings = false, maxEarnings = true,
                                                                selfEmployed = true, apprentice = false, Set.empty))
      }

      "return 'Some' when a user is not self employed for less than 12 months" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.doYouGetAnyBenefits) thenReturn Some(false)
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.yourMaximumEarnings) thenReturn Some(false)
        factory(answers).value mustEqual SingleHousehold(Parent(minEarnings = true, maxEarnings = true,
          selfEmployed = false, apprentice = false, Set.empty))
      }

      "return `None` when `areYouInPaidWork` is undefined" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.doYouGetAnyBenefits) thenReturn Some(true)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        factory(answers) mustNot be(defined)
      }

    }

    "partner user" when {

      "return `Some` when all data is available" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
        when(answers.doYouGetAnyBenefits) thenReturn Some(false)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.partnerMinimumEarnings) thenReturn Some(false)

        when(answers.yourSelfEmployed) thenReturn Some(true)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString)

        when(answers.partnerSelfEmployed) thenReturn Some(true)
        when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString)

        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(false)

        factory(answers).value mustEqual JointHousehold(
          Parent(minEarnings = false, maxEarnings = true, selfEmployed = true, apprentice = false, Set.empty),
          Parent(minEarnings = false, maxEarnings = true, selfEmployed = true, apprentice = false, Set.empty)
        )
      }

      "return None when whoIsInPaidEmployment value is not defined" in {
        val answers = spy(helper())

        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.doYouGetAnyBenefits) thenReturn Some(false)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.partnerMinimumEarnings) thenReturn Some(false)

        when(answers.yourSelfEmployed) thenReturn Some(true)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString)

        when(answers.partnerSelfEmployed) thenReturn Some(true)
        when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString)

        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(false)

        factory(answers) mustNot be(defined)
      }

      "return Some when Parent is Apprentice and Partner satisfies the min amd max earnings" in {
        val answers = spy(helper())

        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
        when(answers.doYouGetAnyBenefits) thenReturn Some(false)
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMaximumEarnings) thenReturn Some(false)

        when(answers.yourSelfEmployed) thenReturn Some(true)
        when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString)

        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(false)

        factory(answers).value mustEqual JointHousehold(
          Parent(minEarnings = false, maxEarnings = true, selfEmployed = false, apprentice = true, Set.empty),
          Parent(minEarnings = true, maxEarnings = true, selfEmployed = false, apprentice = false, Set.empty)
        )
      }

    }
  }
}
