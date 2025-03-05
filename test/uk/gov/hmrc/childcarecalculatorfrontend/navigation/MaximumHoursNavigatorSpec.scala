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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefits._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{both, partner, you}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.{SpecBase, SubNavigator}

class MaximumHoursNavigatorSpec extends SpecBase with MockitoSugar {

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  def navigator(
    schemes: Schemes,
    freeChildcareWorkingParents: FreeChildcareWorkingParents,
    tfc: TaxFreeChildcare,
    esc: EmploymentSupportedChildcare
  ): SubNavigator =
    new MaximumHoursNavigator(schemes, freeChildcareWorkingParents, tfc, esc)

  def navigator(schemes: Schemes): SubNavigator = new MaximumHoursNavigator(
    schemes,
    mock[FreeChildcareWorkingParents],
    mock[TaxFreeChildcare],
    mock[EmploymentSupportedChildcare]
  )

  def navigator: SubNavigator = navigator(new Schemes())

  lazy val selfEmployed: String = SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString
  lazy val apprentice: String = SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString
  lazy val neither: String = SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString
  lazy val yes: String = YesNoUnsureEnum.YES.toString
  lazy val no: String = YesNoUnsureEnum.NO.toString
  lazy val notSure: String = YesNoUnsureEnum.NOTSURE.toString
  lazy val notYet: String = YesNoNotYetEnum.NOTYET.toString

  private val AllParentsBenefits = Seq(
    CarersAllowance,
    IncapacityBenefit,
    SevereDisablementAllowance,
    ContributionBasedEmploymentAndSupportAllowance,
    NICreditsForIncapacityOrLimitedCapabilityForWork,
    CarersCredit
  )


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

  "Go to childcare vouchers" when {
    "user selects 'Yes' from are you in paid work" in {
      val answers = spy(userAnswers())
      when(answers.areYouInPaidWork) thenReturn Some(true)
      navigator.nextPage(AreYouInPaidWorkId, NormalMode).value(answers) mustBe routes.YourChildcareVouchersController.onPageLoad(NormalMode)
    }
    "user selects 'you' from who is in paid employment" in {
      val answers = spy(userAnswers())
      when(answers.whoIsInPaidEmployment) thenReturn Some(you)
      navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode).value(answers) mustBe routes.YourChildcareVouchersController.onPageLoad(NormalMode)
    }

    "user selects 'partner' from who is in paid employment" in {
      val answers = spy(userAnswers())
      when(answers.whoIsInPaidEmployment) thenReturn Some(partner)
      navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode).value(answers) mustBe routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
    }

    "when user selects 'both' on paid employment and coming from partner work hours" in {
      val answers = spy(userAnswers())
      when(answers.whoIsInPaidEmployment) thenReturn Some(both)
      navigator.nextPage(WhoIsInPaidEmploymentId, NormalMode).value(answers) mustBe routes.WhoGetsVouchersController.onPageLoad(NormalMode)
    }
  }

  "Do you get childcare vouchers from your employer?" must {
    "always go to 'do you get any of these benefits'" in {
      val answers = spy(userAnswers())

      navigator.nextPage(YourChildcareVouchersId, NormalMode).value(answers) mustBe
        routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
    }
  }

  "Does your partner get childcare vouchers from their employer?" must {
    "always go to 'do you get any of these benefits'" in {
      val answers = spy(userAnswers())

      navigator.nextPage(PartnerChildcareVouchersId, NormalMode).value(answers) mustBe
        routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
    }
  }

  "Do either of you get childcare vouchers from your employer?" must {
    "always go to 'do you get any of these benefits'" in {
      val answers = spy(userAnswers())

      navigator.nextPage(WhoGetsVouchersId, NormalMode).value(answers) mustBe
        routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
    }
  }

  "Do you get any benefits" when {

    "doYouLiveWithPartner is false" must {
      "go to 'what is your age'" in {
        val answers = mock[UserAnswers]
        when(answers.doYouLiveWithPartner) thenReturn Some(false)

        val result = navigator.nextPage(DoYouGetAnyBenefitsId, NormalMode).value(answers)

        result mustEqual routes.YourAgeController.onPageLoad(NormalMode)
      }
    }

    "doYouLiveWithPartner is true" must {

      "go to 'Results' page" when {
        "whoIsInPaidEmployment is partner, they don't receive childcare vouchers, and parent receives NO benefits" in {
          val answers = mock[UserAnswers]
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(partner)
          when(answers.partnerChildcareVouchers) thenReturn Some(false)
          when(answers.doYouGetAnyBenefits) thenReturn Some(Set(ParentsBenefits.NoneOfThese))

          val result = navigator.nextPage(DoYouGetAnyBenefitsId, NormalMode).value(answers)

          result mustEqual routes.ResultController.onPageLoad()
        }
      }

      "go to 'does your partner get any of these benefits'" when {

        AllParentsBenefits.foreach { benefits =>
          s"whoIsInPaidEmployment is partner, they don't receive childcare vouchers, but parent receives ${benefits.toString} benefits" in {
            val answers = mock[UserAnswers]
            when(answers.doYouLiveWithPartner) thenReturn Some(true)
            when(answers.whoIsInPaidEmployment) thenReturn Some(partner)
            when(answers.partnerChildcareVouchers) thenReturn Some(false)
            when(answers.doYouGetAnyBenefits) thenReturn Some(Set(benefits))

            val result = navigator.nextPage(DoYouGetAnyBenefitsId, NormalMode).value(answers)

            result mustEqual routes.DoesYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
          }
        }

        "whoIsInPaidEmployment is partner and they do receive childcare vouchers" in {
          val answers = mock[UserAnswers]
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(partner)
          when(answers.partnerChildcareVouchers) thenReturn Some(true)

          val result = navigator.nextPage(DoYouGetAnyBenefitsId, NormalMode).value(answers)

          result mustEqual routes.DoesYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
        }

        "whoIsInPaidEmployment is you" in {
          val answers = mock[UserAnswers]
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(you)

          val result = navigator.nextPage(DoYouGetAnyBenefitsId, NormalMode).value(answers)

          result mustEqual routes.DoesYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
        }

        "whoIsInPaidEmployment is both" in {
          val answers = mock[UserAnswers]
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoIsInPaidEmployment) thenReturn Some(both)

          val result = navigator.nextPage(DoYouGetAnyBenefitsId, NormalMode).value(answers)

          result mustEqual routes.DoesYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
        }
      }
    }

    "doYouLiveWithPartner is None" must {
      "go to 'Session expired'" in {
        val answers = mock[UserAnswers]
        when(answers.doYouLiveWithPartner) thenReturn None

        val result = navigator.nextPage(DoYouGetAnyBenefitsId, NormalMode).value(answers)

        result mustEqual routes.SessionExpiredController.onPageLoad
      }
    }
  }

  "Does your partner get any benefits" must {

    "go to 'what is your age'" when {

      "whoIsInPaidEmployment is both" in {
        val answers = mock[UserAnswers]
        when(answers.whoIsInPaidEmployment) thenReturn Some(both)

        val result = navigator.nextPage(DoesYourPartnerGetAnyBenefitsId, NormalMode).value(answers)

        result mustEqual routes.YourAgeController.onPageLoad(NormalMode)
      }

      "whoIsInPaidEmployment is you and you receive childcare vouchers" in {
        val answers = mock[UserAnswers]
        when(answers.whoIsInPaidEmployment) thenReturn Some(you)
        when(answers.yourChildcareVouchers) thenReturn Some(true)

        val result = navigator.nextPage(DoesYourPartnerGetAnyBenefitsId, NormalMode).value(answers)

        result mustEqual routes.YourAgeController.onPageLoad()
      }

      AllParentsBenefits.foreach { benefits =>
        s"whoIsInPaidEmployment is you, you don't receive childcare vouchers, but partner receives ${benefits.toString} benefits" in {
          val answers = mock[UserAnswers]
          when(answers.whoIsInPaidEmployment) thenReturn Some(you)
          when(answers.yourChildcareVouchers) thenReturn Some(false)
          when(answers.doesYourPartnerGetAnyBenefits) thenReturn Some(Set(benefits))

          val result = navigator.nextPage(DoesYourPartnerGetAnyBenefitsId, NormalMode).value(answers)

          result mustEqual routes.YourAgeController.onPageLoad(NormalMode)
        }
      }
    }

    "go to 'what is your partner's age'" when {
      "whoIsInPaidEmployment is partner" in {
        val answers = mock[UserAnswers]
        when(answers.whoIsInPaidEmployment) thenReturn Some(partner)

        val result = navigator.nextPage(DoesYourPartnerGetAnyBenefitsId, NormalMode).value(answers)

        result mustEqual routes.YourPartnersAgeController.onPageLoad(NormalMode)
      }
    }

    "go to 'Results' page" when {
      "whoIsInPaidEmployment is you, you don't receive childcare vouchers and partner receives NO benefits" in {
        val answers = mock[UserAnswers]
        when(answers.whoIsInPaidEmployment) thenReturn Some(you)
        when(answers.yourChildcareVouchers) thenReturn Some(false)
        when(answers.doesYourPartnerGetAnyBenefits) thenReturn Some(Set(ParentsBenefits.NoneOfThese))

        val result = navigator.nextPage(DoesYourPartnerGetAnyBenefitsId, NormalMode).value(answers)

        result mustEqual routes.ResultController.onPageLoad()
      }
    }

    "go to 'Session expired'" when {
      "whoIsInPaidEmployment is None" in {
        val answers = mock[UserAnswers]
        when(answers.whoIsInPaidEmployment) thenReturn None

        val result = navigator.nextPage(DoesYourPartnerGetAnyBenefitsId, NormalMode).value(answers)

        result mustEqual routes.SessionExpiredController.onPageLoad
      }
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


      "parent with partner, both in paid work, will be redirected to you and your average earnings page" in {
        val answers = spy(userAnswers())
        when(answers.doYouLiveWithPartner) thenReturn Some(true)
        when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)


        navigator.nextPage(YourPartnersAgeId, NormalMode).value(answers) mustBe
          routes.AverageWeeklyEarningController.onPageLoad(NormalMode)
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

  "Whats Your age" when {
    "single user will be taken to your average earnings page when user selects any age option " in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      navigator.nextPage(YourAgeId, NormalMode).value(answers) mustBe routes.AverageWeeklyEarningController.onPageLoad(NormalMode)
    }

    "partner user with both in paid work will be taken to whats your partners age page when user selects any age option " in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("both")
      navigator.nextPage(YourAgeId, NormalMode).value(answers) mustBe routes.YourPartnersAgeController.onPageLoad(NormalMode)
    }

    "partner user with only user(You) in paid work will be taken to your average earnings page when user selects any age option " in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("you")
      navigator.nextPage(YourAgeId, NormalMode).value(answers) mustBe routes.AverageWeeklyEarningController.onPageLoad(NormalMode)
    }
  }

  "Whats your partners age" when {
    "user will be taken to partners average earnings page when user selects any age option" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("partner")
      navigator.nextPage(YourPartnersAgeId, NormalMode).value(answers) mustBe routes.AverageWeeklyEarningController.onPageLoad(NormalMode)
    }

    "both in paid work, selecting any age option redirect to parent's average earnings page" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some("both")
      navigator.nextPage(YourPartnersAgeId, NormalMode).value(answers) mustBe routes.AverageWeeklyEarningController.onPageLoad(NormalMode)
    }
  }

  "Your self employed" must {
    "single parent will be redirected to tax or universal credits page when user selects yes/No" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.yourSelfEmployed) thenReturn Some(true) thenReturn Some(false)

      navigator.nextPage(YourSelfEmployedId, NormalMode).value(answers) mustBe
        routes.UniversalCreditController.onPageLoad(NormalMode)
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
        routes.UniversalCreditController.onPageLoad(NormalMode)
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
        routes.UniversalCreditController.onPageLoad(NormalMode)
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

    "navigate to uc page when user select apprentice or neither" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(false)
      when(answers.areYouInPaidWork) thenReturn Some(true)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice) thenReturn Some(neither)

      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.UniversalCreditController.onPageLoad(NormalMode)
      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.UniversalCreditController.onPageLoad(NormalMode)
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
        routes.UniversalCreditController.onPageLoad(NormalMode)

      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.UniversalCreditController.onPageLoad(NormalMode)
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

    "navigate to uc page when user select apprentice or neither on partner self employed or apprentice page" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn (Some(apprentice)) thenReturn (Some(neither))

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.UniversalCreditController.onPageLoad(NormalMode)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.UniversalCreditController.onPageLoad(NormalMode)
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

    "navigate to uc page when user have partner and both doesn't satisfy minimum earning and both select apprentice" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(apprentice)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.UniversalCreditController.onPageLoad(NormalMode)

    }

    "navigate to uc page when user have partner and both doesn't satisfy minimum earning and both select neither" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(neither)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(neither)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.UniversalCreditController.onPageLoad(NormalMode)

    }

    "navigate to uc page when user have partner, parent select self employment and partner select apprentice" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(selfEmployed)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(apprentice)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.UniversalCreditController.onPageLoad(NormalMode)

    }

    "navigate to uc page when user have partner, parent select apprentice and partner select neither" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(neither)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.UniversalCreditController.onPageLoad(NormalMode)
    }

    "navigate to uc page when user have partner, parent select neither and partner select apprentice" in {
      val answers = spy(userAnswers())
      when(answers.doYouLiveWithPartner) thenReturn Some(true)
      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
      when(answers.yourMinimumEarnings) thenReturn Some(false)
      when(answers.partnerMinimumEarnings) thenReturn Some(false)
      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(neither)
      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(apprentice)

      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode).value(answers) mustBe
        routes.UniversalCreditController.onPageLoad(NormalMode)
    }

  }

  "Your Maximum Earnings Navigation" when {

    "in Normal mode" must {

      "Your Maximum Earnings" must {
        "single user where yourChildcareVouchers is yes redirects to your Do you get universal credit page" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourChildcareVouchers) thenReturn Some(true)
          when(answers.yourMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)
        }

        "single user where yourChildcareVouchers is yes and partnerChildcareVouchers is no" +
          "redirects to your Do you get universal credit page" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(false)
          when(answers.yourChildcareVouchers) thenReturn Some(true)
          when(answers.yourMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)
        }

        "user with partner redirects to Do you get universal credit page where " +
          "yourChildcareVouchers is yes and  partner does not satisfy NMW" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.YOU.toString)
          when(answers.partnerMinimumEarnings) thenReturn Some(false)
          when(answers.yourMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)
        }

        //TODO :NEED TO WRITE A SCENARIO WHEN BOTH NOT GETTING VOUCHERS

        "user with partner redirects to Do you get universal credit page " +
          "where yourChildcareVouchers is yes only parent is in paid employment" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.yourChildcareVouchers) thenReturn Some(true)
          when(answers.yourMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)

          navigator.nextPage(YourMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)
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
      }

      "Partner Maximum earnings" must {
        "user with partner redirects to your Do you get universal credit page where partnerChildcareVouchers is yes" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.partnerChildcareVouchers) thenReturn Some(true)
          when(answers.partnerMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(PartnerMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)

          navigator.nextPage(PartnerMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)
        }

        "user with partner redirects to your FreeHoursResult page where partnerChildcareVouchers is NO" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.partnerChildcareVouchers) thenReturn Some(false)
          when(answers.partnerMaximumEarnings) thenReturn Some(true)

          navigator.nextPage(PartnerMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.ResultController.onPageLoad()

        }

        "user with partner redirects to your Do you get universal credit page where partnerChildcareVouchers is NO" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.partnerChildcareVouchers) thenReturn Some(false)
          when(answers.partnerMaximumEarnings) thenReturn Some(false)

          navigator.nextPage(PartnerMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)
        }

        "user with partner redirects to Do you get universal credit page where " +
          "partnerChildcareVouchers is yes and  partner does not satisfy NMW" in {
          val answers = spy(userAnswers())
          when(answers.doYouLiveWithPartner) thenReturn Some(true)
          when(answers.whoGetsVouchers) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
          when(answers.yourMinimumEarnings) thenReturn Some(false)
          when(answers.partnerMaximumEarnings) thenReturn Some(true) thenReturn Some(false)

          navigator.nextPage(PartnerMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)

          navigator.nextPage(PartnerMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)
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
            routes.UniversalCreditController.onPageLoad(NormalMode)
        }

        "redirect to Tax or Universal Credits page when partner selects no to vouchers and false to maximum earnings" in {
          val answers = spy(userAnswers())
          when(answers.yourChildcareVouchers) thenReturn Some(false)
          when(answers.partnerChildcareVouchers) thenReturn Some(true)
          when(answers.eitherOfYouMaximumEarnings) thenReturn Some(false)

          navigator.nextPage(EitherOfYouMaximumEarningsId, NormalMode).value(answers) mustBe
            routes.UniversalCreditController.onPageLoad(NormalMode)
        }
      }
    }
  }

  "Do you get universal credit" must {

    "redirect to `Results`" when {

      "the user is NOT eligible for either TFC or ESC" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn NotEligible
        when(esc.eligibility(any())) thenReturn NotEligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.ResultController.onPageLoad()
      }

      "the user is eligible for TFC but not for ESC, and does NOT have approved childcare costs" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn Eligible
        when(esc.eligibility(any())) thenReturn NotEligible
        when(answers.hasApprovedCosts) thenReturn Some(false)

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.ResultController.onPageLoad()
      }

      "the user is eligible for ESC but not for TFC, and does NOT have approved childcare costs" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn NotEligible
        when(esc.eligibility(any())) thenReturn Eligible
        when(answers.hasApprovedCosts) thenReturn Some(false)

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.ResultController.onPageLoad()
      }

      "the user is eligible for both TFC and ESC, but does NOT have approved childcare costs" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn Eligible
        when(esc.eligibility(any())) thenReturn Eligible
        when(answers.hasApprovedCosts) thenReturn Some(false)

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.ResultController.onPageLoad()
      }
    }

    "redirect to `Max Free Hours Info`" when {

      "the user is eligible for both TFC and ESC, have approved childcare costs, and is eligible for Free Childcare" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn Eligible
        when(esc.eligibility(any())) thenReturn Eligible
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.MaxFreeHoursInfoController.onPageLoad()
      }

      "the user is eligible for TFC but not for ESC, have approved childcare costs, and is eligible for Free Childcare" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn Eligible
        when(esc.eligibility(any())) thenReturn NotEligible
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.MaxFreeHoursInfoController.onPageLoad()
      }

      "the user is eligible for ESC but not for TFC, have approved childcare costs, and is eligible for Free Childcare" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn NotEligible
        when(esc.eligibility(any())) thenReturn Eligible
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.MaxFreeHoursInfoController.onPageLoad()
      }

      "the user is NotDetermined for both TFC and ESC, have approved childcare costs, and is eligible for Free Childcare" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn NotDetermined
        when(esc.eligibility(any())) thenReturn NotDetermined
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.MaxFreeHoursInfoController.onPageLoad()
      }

      "the user is NotDetermined for TFC and NOT eligible for ESC, have approved childcare costs, and is eligible for Free Childcare" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn NotDetermined
        when(esc.eligibility(any())) thenReturn NotEligible
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.MaxFreeHoursInfoController.onPageLoad()
      }

      "the user is NotDetermined for ESC ant NOT eligible for TFC, have approved childcare costs, and is eligible for Free Childcare" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn NotEligible
        when(esc.eligibility(any())) thenReturn NotDetermined
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn Eligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.MaxFreeHoursInfoController.onPageLoad()
      }
    }

    "redirect to `How many children do you have`" when {

      "the user is eligible for both TFC and ESC, have approved childcare costs, but is NOT eligible for Free Childcare" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn Eligible
        when(esc.eligibility(any())) thenReturn Eligible
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn NotEligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.NoOfChildrenController.onPageLoad(NormalMode)
      }

      "the user is eligible for TFC but not for ESC, have approved childcare costs, but is NOT eligible for Free Childcare" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn Eligible
        when(esc.eligibility(any())) thenReturn NotEligible
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn NotEligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.NoOfChildrenController.onPageLoad(NormalMode)
      }

      "the user is eligible for ESC but not for TFC, have approved childcare costs, but is NOT eligible for Free Childcare" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn NotEligible
        when(esc.eligibility(any())) thenReturn Eligible
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn NotEligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.NoOfChildrenController.onPageLoad(NormalMode)
      }

      "the user is NotDetermined for both TFC and ESC, have approved childcare costs, but is NOT eligible for Free Childcare" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn NotDetermined
        when(esc.eligibility(any())) thenReturn NotDetermined
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn NotEligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.NoOfChildrenController.onPageLoad(NormalMode)
      }

      "the user is NotDetermined for TFC and NOT eligible for ESC, have approved childcare costs, but is NOT eligible for Free Childcare" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn NotDetermined
        when(esc.eligibility(any())) thenReturn NotEligible
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn NotEligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.NoOfChildrenController.onPageLoad(NormalMode)
      }

      "the user is NotDetermined for ESC and NOT eligible for TFC, have approved childcare costs, but is NOT eligible for Free Childcare" in {
        val answers = mock[UserAnswers]
        val schemes = mock[Schemes]
        val freeChildcareWorkingParents = mock[FreeChildcareWorkingParents]
        val tfc = mock[TaxFreeChildcare]
        val esc = mock[EmploymentSupportedChildcare]
        when(schemes.allSchemesDetermined(any())) thenReturn true
        when(tfc.eligibility(any())) thenReturn NotEligible
        when(esc.eligibility(any())) thenReturn NotDetermined
        when(answers.hasApprovedCosts) thenReturn Some(true)
        when(freeChildcareWorkingParents.eligibility(any())) thenReturn NotEligible

        val result = navigator(schemes, freeChildcareWorkingParents, tfc, esc).nextPage(UniversalCreditId, NormalMode).value(answers)
        result mustEqual routes.NoOfChildrenController.onPageLoad(NormalMode)
      }
    }

    "redirect to `Session Expired` if not all schemes are determined" in {
      val answers = mock[UserAnswers]
      val schemes = mock[Schemes]
      when(schemes.allSchemesDetermined(any())) thenReturn false

      val result = navigator(schemes).nextPage(UniversalCreditId, NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad
    }
  }

}
