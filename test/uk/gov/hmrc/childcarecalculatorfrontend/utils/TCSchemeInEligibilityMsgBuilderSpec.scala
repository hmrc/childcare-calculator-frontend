/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import java.time.LocalDate

import org.mockito.Mockito.{spy, when}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.Request
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class TCSchemeInEligibilityMsgBuilderSpec extends PlaySpec with MockitoSugar with SpecBase {
  "TC Scheme Ineligibility Message Builder" must {
    "get default message" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.areYouInPaidWork) thenReturn Some(true)

      tcSchemeMessageBuilder.getMessage(answers) mustBe
        messages("result.tc.not.eligible.para1")
    }

    "get the correct message for single journey" when {
      "parent work for less than 16 hours per week" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(10))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List(3)

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.single.user.hours.less.than.16")
      }
    }

    "get the correct message" when {
      "both work less than 16 hrs" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(Both)

        when(answers.parentWorkHours) thenReturn Some(BigDecimal(10))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(10))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List(2)

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum")
      }

      "parent works 16 hrs and partner works 7" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(Both)

        when(answers.parentWorkHours) thenReturn Some(BigDecimal(16))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(7))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List(2)

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum")
      }

      "parent works 7 hrs and partner works 16 " in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(7))
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(16))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List(2)

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum")
      }

      "parent only works and for 21 hrs " in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(You)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(21))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List(2)

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum")
      }

      "parent only works and for 25 hrs" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(You)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(25))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List(2)

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum")
      }

      "parent only works 15 hrs and partner get benefits" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(You)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(15))
        when(answers.whosHadBenefits) thenReturn Some(YouPartnerBothEnum.PARTNER)
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set("disabilityBenefits"))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List(2)

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum.partner.receiving.benefits")
      }

      "parent works 25 hrs and partner get benefits with no valid children" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(You)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(25))
        when(answers.whosHadBenefits) thenReturn Some(YouPartnerBothEnum.PARTNER)
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set("disabilityBenefits"))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List()

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.user.no.child.below.16")
      }

      "parent works 25 hrs and partner get benefits with valid children" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(You)
        when(answers.parentWorkHours) thenReturn Some(BigDecimal(25))
        when(answers.whosHadBenefits) thenReturn Some(YouPartnerBothEnum.PARTNER)
        when(answers.whichBenefitsPartnerGet) thenReturn Some(Set("disabilityBenefits"))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List(1)

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.para1")
      }

      "partner only works and for 21 hrs " in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(21))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List()

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum")
      }

      "partner only works and for 25 hrs " in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(25))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List()

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum")
      }

      "partner only works 15 hrs and parent get benefits" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(15))
        when(answers.whichBenefitsYouGet) thenReturn Some(Set("disabilityBenefits"))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List(2)

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.partner.journey.hours.less.than.minimum.parent.receiving.benefits")
      }

      "partner only works 25 hrs and parent get benefits with no valid children" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(25))
        when(answers.whosHadBenefits) thenReturn Some(YouPartnerBothEnum.YOU)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set("disabilityBenefits"))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List()

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.user.no.child.below.16")
      }

      "partner only works 25 hrs and parent get benefits with valid children" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(Partner)
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(25))
        when(answers.whosHadBenefits) thenReturn Some(YouPartnerBothEnum.YOU)
        when(answers.whichBenefitsYouGet) thenReturn Some(Set("disabilityBenefits"))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn List(1)

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.para1")
      }

      "user does not have any child under 16" in {
        val answers = spy(userAnswers())

        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(Both)
        when(answers.partnerWorkHours) thenReturn Some(BigDecimal(25))
        when(answers.childrenBelow16AndExactly16Disabled) thenReturn (List())

        tcSchemeMessageBuilder.getMessage(answers) mustBe
          messages("result.tc.not.eligible.user.no.child.below.16")
      }

    }
  }

  val utils = new Utils()
  val tcSchemeMessageBuilder = new TCSchemeInEligibilityMsgBuilder()
  val answers: UserAnswers = spy(userAnswers())
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val req: Request[_] = mock[Request[_]]
  override implicit val messages: Messages = messagesApi.preferred(fakeRequest)
  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))
}