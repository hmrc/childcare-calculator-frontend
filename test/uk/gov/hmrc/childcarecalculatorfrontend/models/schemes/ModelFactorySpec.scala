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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import org.mockito.Mockito._
import org.scalatest.{MustMatchers, OptionValues}
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tc._

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

      "return `Some` when all data is available" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.doYouGetAnyBenefits) thenReturn Some(true)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        factory(answers).value mustEqual SingleHousehold(Parent(110, Set(DISABILITYBENEFITS)))
      }

      "return `Some` when a user doesn't work" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.areYouInPaidWork) thenReturn Some(false)
        when(answers.doYouGetAnyBenefits) thenReturn Some(true)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        factory(answers).value mustEqual SingleHousehold(Parent(0, Set(DISABILITYBENEFITS)))
      }

      "return `Some` when a user has no benefits" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.doYouGetAnyBenefits) thenReturn Some(false)
        factory(answers).value mustEqual SingleHousehold(Parent(110, Set.empty))
      }

      "return `None` when `areYouInPaidWork` is undefined" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.doYouGetAnyBenefits) thenReturn Some(true)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        factory(answers) mustNot be(defined)
      }

      "return `None` when `parentWorkHours` is undefined and `areYouInPaidWork` is true" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.doYouGetAnyBenefits) thenReturn Some(true)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        factory(answers) mustNot be(defined)
      }

      "return `None` when `doYouGetAnyBenefits` is undefined" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        factory(answers) mustNot be(defined)
      }

      "return `None` when `whichBenefitsYouGet` is undefined and `doYouGetAnyBenefits` is true" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.doYouGetAnyBenefits) thenReturn Some(true)
        factory(answers) mustNot be(defined)
      }
    }

    "partner user" when {

      "return `Some` when all data is available" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(120))
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)
        when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set(CARERSALLOWANCE.toString))
        factory(answers).value mustEqual JointHousehold(
          Parent(110, Set(DISABILITYBENEFITS)),
          Parent(120, Set(CARERSALLOWANCE))
        )
      }

      "return `Some` when users don't work" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.areYouInPaidWork) thenReturn Some(false)
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)
        when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set(CARERSALLOWANCE.toString))
        factory(answers).value mustEqual JointHousehold(
          Parent(0, Set(DISABILITYBENEFITS)),
          Parent(0, Set(CARERSALLOWANCE))
        )
      }

      "return `Some` when users don't claim benefits" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(120))
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(false)
        factory(answers).value mustEqual JointHousehold(
          Parent(110, Set.empty),
          Parent(120, Set.empty)
        )
      }

      "return `None` when `areYouInPaidWork` is undefined" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(120))
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)
        when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set(CARERSALLOWANCE.toString))
        factory(answers) mustNot be(defined)
      }

      "return `None` when `whoIsInPaidEmployment` is undefined and the user has indicated at least one parent is in work" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(120))
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)
        when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set(CARERSALLOWANCE.toString))
        factory(answers) mustNot be(defined)
      }

      "return `None` when `parentWorkHours` is undefined and the user is employed" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(120))
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)
        when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set(CARERSALLOWANCE.toString))
        factory(answers) mustNot be(defined)
      }

      "return `None` when `parentWorkHours` is undefined both the user and their partner are employed" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(120))
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)
        when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set(CARERSALLOWANCE.toString))
        factory(answers) mustNot be(defined)
      }

      "return `None` when `doYouOrYourPartnerGetBenefits` is undefined" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(120))
        when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set(CARERSALLOWANCE.toString))
        factory(answers) mustNot be(defined)
      }

      "return `None` when `whoGetsBenefits` is undefined but the user has indicated that at least one parent claims benefits" in {
        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(120))
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set(CARERSALLOWANCE.toString))
        factory(answers) mustNot be(defined)
      }

      "return `None` when `whichBenefitsYouGet` is undefined but the user gets benefits" in {

        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(120))
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)
        when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothEnum.YOU.toString)
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set(CARERSALLOWANCE.toString))
        factory(answers) mustNot be(defined)
      }

      "return `None` when `whichBenefitsYouGet` is undefined and the user and their partner both get benefits" in {

        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(120))
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)
        when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set(CARERSALLOWANCE.toString))
        factory(answers) mustNot be(defined)
      }

      "return `None` when whichBenefitsPartnerGet` is undefined and the user's partner gets benefits" in {

        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(120))
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)
        when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        factory(answers) mustNot be(defined)
      }

      "return `None` when whichBenefitsPartnerGet` is undefined and the user and their partner both get benefits" in {

        val answers = spy(helper())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(110))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(120))
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)
        when(answers.whoGetsBenefits) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set(DISABILITYBENEFITS.toString))
        factory(answers) mustNot be(defined)
      }
    }
  }
}
