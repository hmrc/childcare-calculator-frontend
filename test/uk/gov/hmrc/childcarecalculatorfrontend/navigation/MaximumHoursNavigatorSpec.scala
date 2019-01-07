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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{both, partner, you}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.{SpecBase, SubNavigator}
import uk.gov.hmrc.http.cache.client.CacheMap

class MaximumHoursNavigatorSpec extends SpecBase with MockitoSugar {

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  def navigator(schemes: Schemes, maxHours: MaxFreeHours, taxCredits: TaxCredits, tfc: TaxFreeChildcare, esc: EmploymentSupportedChildcare): SubNavigator =
    new MaximumHoursNavigator(new Utils, schemes, maxHours, taxCredits, tfc, esc)

  def navigator(schemes: Schemes): SubNavigator = new MaximumHoursNavigator(new Utils,
    schemes, mock[MaxFreeHours], mock[TaxCredits], mock[TaxFreeChildcare], mock[EmploymentSupportedChildcare])

  def navigator: SubNavigator = navigator(new Schemes())

  lazy val selfEmployed: String = SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString
  lazy val apprentice: String = SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString
  lazy val neither: String = SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString
  lazy val yes: String = YesNoUnsureEnum.YES.toString
  lazy val no: String = YesNoUnsureEnum.NO.toString
  lazy val notSure: String = YesNoUnsureEnum.NOTSURE.toString
  lazy val notYet: String = YesNoNotYetEnum.NOTYET.toString


  "go to are you in paid work from do you live with partner when user selects No" in {
    val answers = spy(userAnswers())
    when(answers.doYouLiveWithPartner) thenReturn Some(false)
    navigator.nextPage(DoYouLiveWithPartnerId, NormalMode).value(answers) mustBe routes.AreYouInPaidWorkController.onPageLoad(NormalMode)
  }

  "Go to free hours results" when {
    "user selects 'No' from are you in paid work" in {
      val answers = spy(userAnswers())
      when(answers.areYouInPaidWork) thenReturn Some(false)
      navigator.nextPage(AreYouInPaidWorkId, NormalMode).value(answers) mustBe routes.ResultController.onPageLoad()
    }

    "user selects 'Neither' from who is in paid employment" in {
      val answers = spy(userAnswers())
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.NEITHER.toString)
      navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode).value(answers) mustBe routes.ResultController.onPageLoad()
    }
  }

  "go to who is in paid employment from do you live with partner when user selects yes" in {
    val answers = spy(userAnswers())
    when(answers.doYouLiveWithPartner) thenReturn Some(true)
    navigator.nextPage(DoYouLiveWithPartnerId, NormalMode).value(answers) mustBe routes.WhoIsInPaidEmploymentController.onPageLoad(NormalMode)
  }


  "Go to Partner work hours" when {
    "user selects 'partner' or 'both' from who is in paid employment" in {
      val answers = spy(userAnswers())
      when(answers.whoIsInPaidEmployment) thenReturn Some(partner) thenReturn Some(both)
      navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode).value(answers) mustBe routes.PartnerWorkHoursController.onPageLoad(NormalMode)
      navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode).value(answers) mustBe routes.PartnerWorkHoursController.onPageLoad(NormalMode)
    }
  }

  "Go to Parent work hours" when {
    "user selects 'Yes' from are you in paid work" in {
      val answers = spy(userAnswers())
      when(answers.areYouInPaidWork) thenReturn Some(true)
      navigator.nextPage(AreYouInPaidWorkId, NormalMode).value(answers) mustBe routes.ParentWorkHoursController.onPageLoad(NormalMode)
    }
    "user selects 'you' from who is in paid employment" in {
      val answers = spy(userAnswers())
      when(answers.whoIsInPaidEmployment) thenReturn Some(you)
      navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode).value(answers) mustBe routes.ParentWorkHoursController.onPageLoad(NormalMode)
    }

    "when user selects 'both' on paid employment and coming from partner work hours" in {
      val answers = spy(userAnswers())
      when(answers.whoIsInPaidEmployment) thenReturn Some(both)
      when(answers.partnerWorkHours) thenReturn Some(BigDecimal(23))
      navigator.nextPage(PartnerWorkHoursId, NormalMode).value(answers) mustBe routes.ParentWorkHoursController.onPageLoad(NormalMode)
    }
  }

  "Go to childcare vouchers" when {
    "user selects 'Partner' on who is in paid employment and hit continue on Partner work hours" in {
      val answers = spy(userAnswers())
      when(answers.whoIsInPaidEmployment) thenReturn Some(partner)
      when(answers.partnerWorkHours) thenReturn Some(BigDecimal(23))
      navigator.nextPage(PartnerWorkHoursId, NormalMode).value(answers) mustBe routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
    }

    "user selects 'Both' on who is in paid employment and hit continue on parent work hours" in {
      val answers = spy(userAnswers())
      when(answers.whoIsInPaidEmployment) thenReturn Some(both)
      when(answers.partnerWorkHours) thenReturn Some(BigDecimal(23))
      navigator.nextPage(ParentWorkHoursId, NormalMode).value(answers) mustBe routes.WhoGetsVouchersController.onPageLoad(NormalMode)
    }
  }

  "Go to Childcare Vouchers" when {
    "user selects continue on Parent work hours" in {
      val answers = spy(userAnswers())
      when(answers.parentWorkHours) thenReturn Some(BigDecimal(23))
      navigator.nextPage(ParentWorkHoursId, NormalMode).value(answers) mustBe routes.YourChildcareVouchersController.onPageLoad(NormalMode)
    }
  }

  "Do you get childcare vouchers from your employer?" when {
    "user will be taken to DoYouOrYourPartnerGetAnyBenefits screen from YourChildcareVouchers screen when any selection is done and " +
      "lives with partner" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.yourChildcareVouchers) thenReturn
        Some(true) thenReturn
        Some(false)

      navigator.nextPage(YourChildcareVouchersId, NormalMode).value(answers) mustBe
        routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
      navigator.nextPage(YourChildcareVouchersId, NormalMode).value(answers) mustBe
        routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)

    }

    "user will be taken to DoYouGetAnyBenefits screen from YourChildcareVouchers screen when any selection is done and " +
      "does not lives with partner" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourChildcareVouchers) thenReturn
        Some(true) thenReturn
        Some(false)

      navigator.nextPage(YourChildcareVouchersId, NormalMode).value(answers) mustBe
        routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
      navigator.nextPage(YourChildcareVouchersId, NormalMode).value(answers) mustBe
        routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)

    }
  }

  "Does your partner get childcare vouchers from their employer?" when {
    "user with partner will be taken to Do you get any benefits screen from PartnerChildcareVouchers screen when any selection is done" in {
      val answers = spy(userAnswers())
      when(answers.partnerChildcareVouchers) thenReturn
        Some(true) thenReturn
        Some(false)

      navigator.nextPage(PartnerChildcareVouchersId, NormalMode).value(answers) mustBe
        routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
      navigator.nextPage(PartnerChildcareVouchersId, NormalMode).value(answers) mustBe
        routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)

    }
  }

  "Do you or your partner get any benefits" when {

    "not all schemes are determined" when {

      "go to `Which of you gets benefits` when you answer `yes`" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]

        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(true)

        val result = navigator(schemes).nextPage(DoYouOrYourPartnerGetAnyBenefitsId, NormalMode).value(answers)
        result mustEqual routes.WhoGetsBenefitsController.onPageLoad(NormalMode)
      }

      "go to 'what is your age' when you answer 'no'" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]

        when(answers.whoIsInPaidEmployment) thenReturn Some(you)
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(false)

        val result = navigator(schemes).nextPage(DoYouOrYourPartnerGetAnyBenefitsId, NormalMode).value(answers)
        result mustEqual routes.YourAgeController.onPageLoad(NormalMode)
      }

      "go to 'what is your age' when you answer 'no' and partner in paid employment" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]

        when(answers.whoIsInPaidEmployment) thenReturn Some(partner)
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(false)

        val result = navigator(schemes).nextPage(DoYouOrYourPartnerGetAnyBenefitsId, NormalMode).value(answers)
        result mustEqual routes.YourPartnersAgeController.onPageLoad(NormalMode)
      }

      "go to result page when you answer 'no', only parent in paid employment and not eligible for TC" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]
        val maxHours = mock[MaxFreeHours]
        val taxCredits = mock[TaxCredits]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]

        when(taxCredits.eligibility(any())) thenReturn NotEligible
        when(esc.eligibility(any()))  thenReturn NotEligible

        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(you)
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(false)

        val result = navigator(schemes, maxHours, taxCredits, tfc, esc).nextPage(DoYouOrYourPartnerGetAnyBenefitsId, NormalMode).value(answers)
        result mustEqual routes.ResultController.onPageLoad()
      }

      "go to result page when you answer 'no', only partner in paid employment and not eligible for TC" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]
        val maxHours = mock[MaxFreeHours]
        val taxCredits = mock[TaxCredits]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]

        when(taxCredits.eligibility(any())) thenReturn NotEligible
        when(esc.eligibility(any()))  thenReturn NotEligible

        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(partner)
        when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn Some(false)

        val result = navigator(schemes, maxHours, taxCredits, tfc, esc).nextPage(DoYouOrYourPartnerGetAnyBenefitsId, NormalMode).value(answers)
        result mustEqual routes.ResultController.onPageLoad()
      }

  }

    "go to `Session expired` when there is no answer for `Do you or your partner get any benefits`" in {
      val answers = spy(userAnswers())
      val schemes = mock[Schemes]

      when(answers.doYouOrYourPartnerGetAnyBenefits) thenReturn None
      when(schemes.allSchemesDetermined(any())) thenReturn false

      val result = navigator(schemes).nextPage(DoYouOrYourPartnerGetAnyBenefitsId, NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }
  }

  "Do You get any benefits" must {

    "not all schemes are determined" when {

      "go to 'what is your age' when you answer 'no'" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]
        when(answers.doYouGetAnyBenefits) thenReturn Some(false)
        val result = navigator(schemes).nextPage(DoYouGetAnyBenefitsId, NormalMode).value(answers)
        result mustEqual routes.YourAgeController.onPageLoad(NormalMode)
      }

      "go to 'which benefits do you get' page when 'yes' selected" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]
        when(answers.doYouGetAnyBenefits) thenReturn Some(true)
        val result = navigator(schemes).nextPage(DoYouGetAnyBenefitsId, NormalMode).value(answers)
        result mustEqual routes.WhichBenefitsYouGetController.onPageLoad(NormalMode)
      }
    }

    "go to 'Session expired' when there is no answer for 'Do you get any benefits" in {
      val answers = spy(userAnswers())
      val schemes = mock[Schemes]
      when(answers.doYouGetAnyBenefits) thenReturn None
      val result = navigator(schemes).nextPage(DoYouGetAnyBenefitsId, NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }
  }

  "Who gets benefits" when {
    "partner user will be taken to which benefits do you get page when user selects You/both" in {
      val answers = spy(userAnswers())
      when(answers.whoGetsBenefits) thenReturn Some("you") thenReturn Some("both")
      navigator.nextPage(WhoGetsBenefitsId, NormalMode).value(answers) mustBe routes.WhichBenefitsYouGetController.onPageLoad(NormalMode)
    }

    "partner user will be taken to which benefits does your partner get page when user selects Partner" in {
      val answers = spy(userAnswers())
      when(answers.whoGetsBenefits) thenReturn Some("partner")
      navigator.nextPage(WhoGetsBenefitsId, NormalMode).value(answers) mustBe routes.WhichBenefitsPartnerGetController.onPageLoad(NormalMode)
    }
  }

  "Your Minimum Earnings" when {

    "in Normal mode" must {

      "single parent in paid work earns more than NMW, will be redirected to parent maximum earnings page" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.yourMinimumEarnings) thenReturn Some(true)

        navigator.nextPage(YourMinimumEarningsId, NormalMode).value(answers) mustBe
          routes.YourMaximumEarningsController.onPageLoad(NormalMode)

      }

      "single parent in paid work and does not earns more than NMW, will be redirected to parent self employed and apprentice page" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.yourMinimumEarnings) thenReturn Some(false)

        navigator.nextPage(YourMinimumEarningsId, NormalMode).value(answers) mustBe
          routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      }


      "parent with partner, both in paid work and parent earns more than NMW, will be redirected to partner minimum earnings page" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)

        navigator.nextPage(YourMinimumEarningsId, NormalMode).value(answers) mustBe
          routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
      }

      "parent with partner, both in paid work and parent does not earn more than NMW, will be redirected to partner minimum earnings page" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)

        navigator.nextPage(YourMinimumEarningsId, NormalMode).value(answers) mustBe
          routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
      }

      "redirect to your max earnings page when there is a partner, only parent is in paid work and parent earns more than NMW" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(true)

        navigator.nextPage(YourMinimumEarningsId, NormalMode).value(answers) mustBe
          routes.YourMaximumEarningsController.onPageLoad(NormalMode)
      }

      "redirect to your self employed or apprentice page when there is a partner, only parent is in paid work and parent earns less than NMW" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)
        when(answers.yourMinimumEarnings) thenReturn Some(false)

        navigator.nextPage(YourMinimumEarningsId, NormalMode).value(answers) mustBe
          routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      }

    }
  }

  "Partner Minimum Earnings " when {

    "in Normal mode" must {

      "redirect to your maximum earnings page if parent and partner earns more than NMW" in {
        val answers = spy(userAnswers())
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)

        navigator.nextPage(PartnerMinimumEarningsId, NormalMode).value(answers) mustBe
          routes.EitherOfYouMaximumEarningsController.onPageLoad(NormalMode)
      }

      "redirect to your self employed or apprentice page if partner earns more than NMW but parent doesn't" in {
        val answers = spy(userAnswers())
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)

        navigator.nextPage(PartnerMinimumEarningsId, NormalMode).value(answers) mustBe
          routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      }

      "redirect to your self employed or apprentice page if parent and partner do not earn more than NMW" in {

        val answers = spy(userAnswers())
        when(answers.yourMinimumEarnings) thenReturn Some(false)
        when(answers.partnerMinimumEarnings) thenReturn Some(false)

        navigator.nextPage(PartnerMinimumEarningsId, NormalMode).value(answers) mustBe
          routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)

      }

      "redirect to partner self employed or apprentice page if parent earns more than NMW but partner doesn't" in {
        val answers = spy(userAnswers())
        when(answers.yourMinimumEarnings) thenReturn Some(true)
        when(answers.partnerMinimumEarnings) thenReturn Some(false)

        navigator.nextPage(PartnerMinimumEarningsId, NormalMode).value(answers) mustBe
          routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      }

      "redirect to partner max earnings page when there is a partner, only partner is in paid work and partner earns more than NMW" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(true)

        navigator.nextPage(PartnerMinimumEarningsId, NormalMode).value(answers) mustBe
          routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
      }

      "redirect to partner self employed or apprentice page when there is a partner, only partner is in paid work and parent earns less than NMW" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
        when(answers.partnerMinimumEarnings) thenReturn Some(false)

        navigator.nextPage(PartnerMinimumEarningsId, NormalMode).value(answers) mustBe
          routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      }

    }
  }

  "Which benefits do you get" must {

    "not all schemes are determined" when {

      "redirect to `Which benefits does your partner get` when user with partner selects  'both' option for whoGetsBenefits and whoIsInPaidEmployment" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]

        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(both)
        when(answers.whoGetsBenefits) thenReturn Some(both)

        val result = navigator(schemes).nextPage(WhichBenefitsYouGetId, NormalMode).value(answers)
        result mustEqual routes.WhichBenefitsPartnerGetController.onPageLoad(NormalMode)
      }

      "redirect to `Your Age` when user with partner selects  'you' option for whoGetsBenefits  and both in paid employment" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]

        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(both)
        when(answers.whoGetsBenefits) thenReturn Some(you)

        val result = navigator(schemes).nextPage(WhichBenefitsYouGetId, NormalMode).value(answers)
        result mustEqual routes.YourAgeController.onPageLoad(NormalMode)
      }

      "redirect to `Your Partners Age` when user with partner selects 'you' option for whoGetsBenefits and partner in paid employment" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]

        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(partner)
        when(answers.whoGetsBenefits) thenReturn Some(you)

        val result = navigator(schemes).nextPage(WhichBenefitsYouGetId, NormalMode).value(answers)
        result mustEqual routes.YourPartnersAgeController.onPageLoad(NormalMode)
      }

      "redirect to `Your Age` when user with partner selects 'you' option for whoGetsBenefits and you in paid employment" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]

        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(you)
        when(answers.whoGetsBenefits) thenReturn Some(you)

        val result = navigator(schemes).nextPage(WhichBenefitsYouGetId, NormalMode).value(answers)
        result mustEqual routes.YourAgeController.onPageLoad(NormalMode)
      }

      "redirect to `Which benefits does your partner get` when user with partner selects 'both' option for whoGetsBenefits and you in paid employment" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]

        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(you)
        when(answers.whoGetsBenefits) thenReturn Some(both)

        val result = navigator(schemes).nextPage(WhichBenefitsYouGetId, NormalMode).value(answers)
        result mustEqual routes.WhichBenefitsPartnerGetController.onPageLoad(NormalMode)
      }

      "redirect to `Your Age` when single user selects 'yes' option for areYouPaidEmployment and doYouGetAnyBenefits" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]

        when(answers.doYouLiveWithPartner) thenReturn Some(false)
        when(answers.areYouInPaidWork) thenReturn Some(true)
        when(answers.doYouGetAnyBenefits) thenReturn Some(true)

        val result = navigator(schemes).nextPage(WhichBenefitsYouGetId, NormalMode).value(answers)
        result mustEqual routes.YourAgeController.onPageLoad(NormalMode)
      }
    }

   "redirect to `SessionExpired` when `doYouLiveWithPartner` is undefined" in {
      val answers = spy(userAnswers())
      val schemes = mock[Schemes]
      when(schemes.allSchemesDetermined(any())) thenReturn false

      val result = navigator(schemes).nextPage(WhichBenefitsYouGetId, NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }
  }

  "Which benefits your partner get" when {

    "not all schemes are determined" when {

      "redirect to `Your Partner  Age` when partner in paid employment" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]

        when(answers.whoIsInPaidEmployment) thenReturn Some(partner)

        val result = navigator(schemes).nextPage(WhichBenefitsPartnerGetId, NormalMode).value(answers)
        result mustEqual routes.YourPartnersAgeController.onPageLoad(NormalMode)
      }

      "redirect to `Your Age` when  parent in paid employment" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]

        when(answers.whoIsInPaidEmployment) thenReturn Some(you)

        val result = navigator(schemes).nextPage(WhichBenefitsPartnerGetId, NormalMode).value(answers)
        result mustEqual routes.YourAgeController.onPageLoad(NormalMode)
      }

      "redirect to `Your Age` when  both in paid employment" in {
        val answers = spy(userAnswers())
        val schemes = mock[Schemes]

        when(answers.whoIsInPaidEmployment) thenReturn Some(both)

        val result = navigator(schemes).nextPage(WhichBenefitsPartnerGetId, NormalMode).value(answers)
        result mustEqual routes.YourAgeController.onPageLoad(NormalMode)
      }
    }
  }

  "Whats Your age" when {
    "single user will be taken to parent minimum earnings page when user selects any age option " in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      navigator.nextPage(YourAgeId, NormalMode).value(answers) mustBe routes.YourMinimumEarningsController.onPageLoad(NormalMode)
    }

    "partner user with both in paid work will be taken to whats your partners age page when user selects any age option " in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("both")
      navigator.nextPage(YourAgeId, NormalMode).value(answers) mustBe routes.YourPartnersAgeController.onPageLoad(NormalMode)
    }

    "partner user with only user(You) in paid work will be taken to parent minimum earnings page when user selects any age option " in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("you")
      navigator.nextPage(YourAgeId, NormalMode).value(answers) mustBe routes.YourMinimumEarningsController.onPageLoad(NormalMode)
    }
  }

  "Whats your partners age" when {
    "user will be taken to partners minimum earnings page when user selects any age option" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("partner")
      navigator.nextPage(YourPartnersAgeId, NormalMode).value(answers) mustBe routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
    }

    "both in paid work, selecting any age option redirect to parent's minimum earnings page" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("both")
      navigator.nextPage(YourPartnersAgeId, NormalMode).value(answers) mustBe routes.YourMinimumEarningsController.onPageLoad(NormalMode)
    }
  }

  "Your self employed" must {
    "single parent will be redirected to tax or universal credits page when user selects yes/No" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourSelfEmployed) thenReturn Some(true) thenReturn Some(false)

      navigator.nextPage(YourSelfEmployedId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }

    "parent with partner, both in paid work, no minimum earnings will be redirected to Is your partner self employed or apprentice" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("both")
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)

      navigator.nextPage(YourSelfEmployedId, NormalMode).value(answers) mustBe
        routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    }

    "parent with partner, both in paid work, parent not satisfying minimum earnings but is self employed, redirect to partner self employed or apprentice page" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("both")
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.yourSelfEmployed) thenReturn Some(true)

      navigator.nextPage(YourSelfEmployedId, NormalMode).value(answers) mustBe
        routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    }

    "parent with partner, both in paid work, partner is satisfying minimum earnings will be redirected to partner max earnings" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("both")
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(true)
      when(answers.yourSelfEmployed) thenReturn Some(true)

      navigator.nextPage(YourSelfEmployedId, NormalMode).value(answers) mustBe
        routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
    }
  }

  "Your partner self employed" must {

    "parent with partner, both in paid work, no minimum earnings will be redirected to Is your partner self employed or apprentice" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("both")
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)

      navigator.nextPage(PartnerSelfEmployedId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }

    "parent with partner, both in paid work, parent is satisfying minimum earnings will be redirected to parent max earnings" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("both")
      when(answers.yourMinimumEarnings) thenReturn Some(true)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.partnerSelfEmployed) thenReturn Some(true)

      navigator.nextPage(PartnerSelfEmployedId, NormalMode).value(answers) mustBe
        routes.YourMaximumEarningsController.onPageLoad(NormalMode)
    }

    "Parent with partner, partner in paid work, partner min earnings is not satisfied will be redirected to Tax or Universal Credits" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("partner")
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.partnerSelfEmployed) thenReturn Some(true)

      navigator.nextPage(PartnerSelfEmployedId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }
  }

  "Are you self employed or apprentice" when {
    "navigate to have you been self employed less than 12 months when user select self employed" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.areYouInPaidWork) thenReturn Some(true)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(selfEmployed)

      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.YourSelfEmployedController.onPageLoad(NormalMode)
    }

    "navigate to tc/uc page when user select apprentice or neither" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.areYouInPaidWork) thenReturn Some(true)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice) thenReturn Some(neither)

      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }

    "navigate to prent self employed 12 months page when user have partner and select self employed" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(selfEmployed)

      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.YourSelfEmployedController.onPageLoad(NormalMode)
    }

    "navigate to partner max earning page when user have partner and partner satisfy minimum earning and parent select apprentice or neither" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice) thenReturn Some(neither)

      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }

    "navigate to parent self employed 12 months page when user have partner and partner satisfy minimum earning and parent select self employed" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(true)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(selfEmployed)

      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.YourSelfEmployedController.onPageLoad(NormalMode)
    }

    "navigate to partner self employed or apprentice page when user have partner and parent select apprentice or neither" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(true)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice) thenReturn Some(neither)

      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)

      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
    }

  }

  "Is your partner self employed or apprentice" when {
    "navigate to have your partner been self employed less than 12 months when user select self employed" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(selfEmployed)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.PartnerSelfEmployedController.onPageLoad(NormalMode)
    }

    "navigate to tc/uc page when user select apprentice or neither on partner self employed or apprentice page" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn (Some(apprentice)) thenReturn (Some(neither))

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }

    "navigate to partner self employed 12 months page when user have partner and parent satisfy minimum earning and partner select self employed" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(true)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(selfEmployed)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.PartnerSelfEmployedController.onPageLoad(NormalMode)
    }

    "navigate to parent max earnings page when user have partner and parent satisfy minimum earning and partner select apprentice or neither" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(true)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn (Some(apprentice)) thenReturn (Some(neither))

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.YourMaximumEarningsController.onPageLoad(NormalMode)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.YourMaximumEarningsController.onPageLoad(NormalMode)
    }

    "navigate to tc/uc page when user have partner and both doesn't satisfy minimum earning and both select apprentice" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(apprentice)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

    }

    "navigate to tc/uc page when user have partner and both doesn't satisfy minimum earning and both select neither" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(neither)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(neither)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

    }

    "navigate to tc/uc page when user have partner, parent select self employment and partner select apprentice" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(selfEmployed)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(apprentice)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

    }

    "navigate to tc/uc page when user have partner, parent select apprentice and partner select neither" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(neither)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }

    "navigate to tc/uc page when user have partner, parent select neither and partner select apprentice" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(neither)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(apprentice)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }

  }

  "Your Maximum Earnings Navigation" when {

    "in Normal mode" must {

      "Your Maximum Earnings" must {
        "single user where yourChildcareVouchers is yes redirects to your Do you get tax credits or universal credit page" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourChildcareVouchers) thenReturn Some(true)
          when(answers.yourMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        }

        "single user where yourChildcareVouchers is yes and partnerChildcareVouchers is no" +
          "redirects to your Do you get tax credits or universal credit page" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourChildcareVouchers) thenReturn Some(true)
          when(answers.yourMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        }

        "user with partner redirects to Do you get tax credits or universal credit page where " +
          "yourChildcareVouchers is yes and  partner does not satisfy NMW" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.YOU.toString)
          when(answers.partnerMinimumEarnings) thenReturn Some(false)
          when(answers.yourMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        }

        //TODO :NEED TO WRITE A SCENARIO WHEN BOTH NOT GETTING VOUCHERS
        "user with partner redirects to TaxOrUniversalCredits page " +
          "where yourChildcareVouchers is no and partner does not satisfy NMW" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.yourChildcareVouchers) thenReturn Some(false)
          when(answers.partnerMinimumEarnings) thenReturn Some(false)
          when(answers.yourMaximumEarnings) thenReturn Some(false)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        }

        "user with partner redirects to Do you get tax credits or universal credit page " +
          "where yourChildcareVouchers is yes only parent is in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.yourChildcareVouchers) thenReturn Some(true)
          when(answers.yourMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        }

        "single user redirects to free hours result pages when Childcare vouchers selection is NO " +
          "and yourMaximumEarnings is true " in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourChildcareVouchers) thenReturn Some(false)
          when(answers.yourMaximumEarnings) thenReturn Some(true)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.ResultController.onPageLoad()

        }

        "single user redirects to taxOrUniversalCredits pages when Childcare vouchers selection is NO " +
          "and yourMaximumEarnings is false " in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourChildcareVouchers) thenReturn Some(false)
          when(answers.yourMaximumEarnings) thenReturn Some(false)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        }
      }

      "Partner Maximum earnings" must {
        "user with partner redirects to your Do you get tax credits or universal credit page where partnerChildcareVouchers is yes" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.partnerChildcareVouchers) thenReturn Some(true)
          when(answers.partnerMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(PartnerMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

          navigator.nextPage(PartnerMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        }

        "user with partner redirects to your FreeHoursResult page where partnerChildcareVouchers is NO" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.partnerChildcareVouchers) thenReturn Some(false)
          when(answers.partnerMaximumEarnings) thenReturn Some(true)

          navigator.nextPage(PartnerMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.ResultController.onPageLoad()

        }

        "user with partner redirects to your Do you get tax credits or universal credit page where partnerChildcareVouchers is NO" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.partnerChildcareVouchers) thenReturn Some(false)
          when(answers.partnerMaximumEarnings) thenReturn Some(false)

          navigator.nextPage(PartnerMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        }

        "user with partner redirects to Do you get tax credits or universal credit page where " +
          "partnerChildcareVouchers is yes and  partner does not satisfy NMW" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
          when(answers.yourMinimumEarnings) thenReturn Some(false)
          when(answers.partnerMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(PartnerMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)

          navigator.nextPage(PartnerMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        }
        //TODO :: Need to write a scenario for neither in  gets vouchers
      }

      "Either Of You Maximum earnings" must {
        "redirect to FreeHoursResult page when both selects no to vouchers and yes to maximum earnings " in {
          val answers = spy(userAnswers())
          when(answers.partnerChildcareVouchers) thenReturn Some(false)
          when(answers.yourChildcareVouchers) thenReturn Some(false)
          when(answers.eitherOfYouMaximumEarnings) thenReturn Some(true)

          navigator.nextPage(EitherOfYouMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.ResultController.onPageLoad()
        }

        "redirect to Tax or Universal Credits page when partner selects no and parent yes for vouchers and yes to maximum earnings" in {
          val answers = spy(userAnswers())
          when(answers.partnerChildcareVouchers) thenReturn Some(false)
          when(answers.yourChildcareVouchers) thenReturn Some(true)
          when(answers.eitherOfYouMaximumEarnings) thenReturn Some(true)

          navigator.nextPage(EitherOfYouMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        }

        "redirect to Tax or Universal Credits page when partner selects no to vouchers and false to maximum earnings" in {
          val answers = spy(userAnswers())
          when(answers.yourChildcareVouchers) thenReturn Some(false)
          when(answers.partnerChildcareVouchers) thenReturn Some(true)
          when(answers.eitherOfYouMaximumEarnings) thenReturn Some(false)

          navigator.nextPage(EitherOfYouMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
        }
      }
    }
  }

  "Do you get tax credits or universal credit" must {

    "redirect to `Results` if the user is not eligible for Tax Credits or TFC" in {
      val answers = spy(userAnswers())
      val schemes = mock[Schemes]
      val maxHours = mock[MaxFreeHours]
      val taxCredits = mock[TaxCredits]
      val tfc = mock[TaxFreeChildcare]
      val esc = mock[EmploymentSupportedChildcare]
      when(schemes.allSchemesDetermined(any())) thenReturn true
      when(maxHours.eligibility(any())) thenReturn Eligible
      when(taxCredits.eligibility(any())) thenReturn NotEligible
      when(tfc.eligibility(any())) thenReturn NotEligible
      val result = navigator(schemes, maxHours, taxCredits, tfc, esc).nextPage(TaxOrUniversalCreditsId, NormalMode).value(answers)
      result mustEqual routes.ResultController.onPageLoad()
    }

    "redirect to `Max Hours Info` if the user is eligible for Max Free Hours and Tax Credits and TFC" in {
      val answers = spy(userAnswers())
      val schemes = mock[Schemes]
      val maxHours = mock[MaxFreeHours]
      val taxCredits = mock[TaxCredits]
      val tfc = mock[TaxFreeChildcare]
      val esc = mock[EmploymentSupportedChildcare]
      when(answers.childcareCosts) thenReturn Some(yes)
      when(answers.approvedProvider) thenReturn Some(yes)
      when(schemes.allSchemesDetermined(any())) thenReturn true
      when(maxHours.eligibility(any())) thenReturn Eligible
      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(tfc.eligibility(any())) thenReturn Eligible
      val result = navigator(schemes, maxHours, taxCredits, tfc, esc).nextPage(TaxOrUniversalCreditsId, NormalMode).value(answers)
      result mustEqual routes.MaxFreeHoursInfoController.onPageLoad()
      result mustEqual routes.MaxFreeHoursInfoController.onPageLoad()
    }

    "redirect to `Max Hours result` if the user is eligible for Max Free Hours and Tax Credits, but not TFC" in {
      val answers = spy(userAnswers())
      val schemes = mock[Schemes]
      val maxHours = mock[MaxFreeHours]
      val taxCredits = mock[TaxCredits]
      val tfc = mock[TaxFreeChildcare]
      val esc = mock[EmploymentSupportedChildcare]
      when(schemes.allSchemesDetermined(any())) thenReturn true
      when(maxHours.eligibility(any())) thenReturn NotEligible
      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(tfc.eligibility(any())) thenReturn NotEligible
      val result = navigator(schemes, maxHours, taxCredits, tfc, esc).nextPage(TaxOrUniversalCreditsId, NormalMode).value(answers)
      result mustEqual routes.ResultController.onPageLoad()
    }

    "redirect to `Max Hours Info` if the user is eligible for Max Free Hours and TFC, but not Tax Credits" in {
      val answers = spy(userAnswers())
      val schemes = mock[Schemes]
      val maxHours = mock[MaxFreeHours]
      val taxCredits = mock[TaxCredits]
      val tfc = mock[TaxFreeChildcare]
      val esc = mock[EmploymentSupportedChildcare]
      when(answers.childcareCosts) thenReturn Some(notYet)
      when(answers.approvedProvider) thenReturn Some(yes)
      when(schemes.allSchemesDetermined(any())) thenReturn true
      when(maxHours.eligibility(any())) thenReturn Eligible
      when(taxCredits.eligibility(any())) thenReturn NotEligible
      when(tfc.eligibility(any())) thenReturn Eligible
      val result = navigator(schemes, maxHours, taxCredits, tfc, esc).nextPage(TaxOrUniversalCreditsId, NormalMode).value(answers)
      result mustEqual routes.MaxFreeHoursInfoController.onPageLoad()
    }

    "redirect to `How many children do you have` if the user is not eligible for Max Free Hours or TFC, but is eligible for Tax Credits" in {
      val answers = spy(userAnswers())
      val schemes = mock[Schemes]
      val maxHours = mock[MaxFreeHours]
      val taxCredits = mock[TaxCredits]
      val tfc = mock[TaxFreeChildcare]
      val esc = mock[EmploymentSupportedChildcare]
      when(schemes.allSchemesDetermined(any())) thenReturn true
      when(answers.childcareCosts) thenReturn Some(yes)
      when(answers.approvedProvider) thenReturn Some(notSure)
      when(maxHours.eligibility(any())) thenReturn NotEligible
      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(tfc.eligibility(any())) thenReturn NotEligible
      val result = navigator(schemes, maxHours, taxCredits, tfc, esc).nextPage(TaxOrUniversalCreditsId, NormalMode).value(answers)
      result mustEqual routes.NoOfChildrenController.onPageLoad(NormalMode)
    }

    "redirect to `How many children do you have` if the user is not eligible for Max Free Hours or Tax Credits, but is eligible for TFC" in {
      val answers = spy(userAnswers())
      val schemes = mock[Schemes]
      val maxHours = mock[MaxFreeHours]
      val taxCredits = mock[TaxCredits]
      val tfc = mock[TaxFreeChildcare]
      val esc = mock[EmploymentSupportedChildcare]
      when(schemes.allSchemesDetermined(any())) thenReturn true
      when(answers.childcareCosts) thenReturn Some(notYet)
      when(answers.approvedProvider) thenReturn Some(yes)
      when(maxHours.eligibility(any())) thenReturn NotEligible
      when(taxCredits.eligibility(any())) thenReturn NotEligible
      when(tfc.eligibility(any())) thenReturn Eligible
      val result = navigator(schemes, maxHours, taxCredits, tfc, esc).nextPage(TaxOrUniversalCreditsId, NormalMode).value(answers)
      result mustEqual routes.NoOfChildrenController.onPageLoad(NormalMode)
    }

    "redirect to `How many children do you have` if the user is not eligible for Max Free Hours, but is eligible for TFC and Tax Credits" in {
      val answers = spy(userAnswers())
      val schemes = mock[Schemes]
      val maxHours = mock[MaxFreeHours]
      val taxCredits = mock[TaxCredits]
      val tfc = mock[TaxFreeChildcare]
      val esc = mock[EmploymentSupportedChildcare]
      when(schemes.allSchemesDetermined(any())) thenReturn true
      when(answers.childcareCosts) thenReturn Some(notYet)
      when(answers.approvedProvider) thenReturn Some(notSure)
      when(maxHours.eligibility(any())) thenReturn NotEligible
      when(taxCredits.eligibility(any())) thenReturn Eligible
      when(tfc.eligibility(any())) thenReturn Eligible
      val result = navigator(schemes, maxHours, taxCredits, tfc, esc).nextPage(TaxOrUniversalCreditsId, NormalMode).value(answers)
      result mustEqual routes.NoOfChildrenController.onPageLoad(NormalMode)
    }

    "redirect to `How many children do you have` if the user is not eligible for anything, but but partner is eligible for ESC" in {
      val answers = spy(userAnswers())
      val schemes = mock[Schemes]
      val maxHours = mock[MaxFreeHours]
      val taxCredits = mock[TaxCredits]
      val tfc = mock[TaxFreeChildcare]
      val esc = mock[EmploymentSupportedChildcare]
      when(schemes.allSchemesDetermined(any())) thenReturn true
      when(answers.childcareCosts) thenReturn Some(yes)
      when(answers.approvedProvider) thenReturn Some(yes)
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(maxHours.eligibility(any())) thenReturn NotEligible
      when(taxCredits.eligibility(any())) thenReturn NotEligible
      when(tfc.eligibility(any())) thenReturn NotEligible
      when(esc.eligibility(any())) thenReturn Eligible

      val result = navigator(schemes, maxHours, taxCredits, tfc, esc).nextPage(TaxOrUniversalCreditsId, NormalMode).value(answers)
      result mustEqual routes.NoOfChildrenController.onPageLoad(NormalMode)
    }

    "redirect to `Session Expired` if not all schemes are determined" in {
      val answers = spy(userAnswers())
      val schemes = mock[Schemes]
      when(schemes.allSchemesDetermined(any())) thenReturn false
      val result = navigator(schemes).nextPage(TaxOrUniversalCreditsId, NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }

  }

}
