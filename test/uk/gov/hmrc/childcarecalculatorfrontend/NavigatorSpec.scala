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

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class NavigatorSpec extends SpecBase with MockitoSugar {

  val navigator = new Navigator

  "Navigator" when {

    "in Normal mode" must {

      "go to Index from an identifier that doesn't exist in the route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, NormalMode)(mock[UserAnswers]) mustBe routes.WhatToTellTheCalculatorController.onPageLoad()
      }

      "go to Child Aged Two from Location when the location is England, Scotland or Wales" in {
        val answers = mock[UserAnswers]
        when(answers.location) thenReturn Some("england") thenReturn Some("wales") thenReturn Some("scotland")

        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedTwoController.onPageLoad(NormalMode)
      }

      "go to Child Aged Three or Four from Location when the location is Northern Ireland" in {
        val answers = mock[UserAnswers]
        when(answers.location) thenReturn Some("northernIreland")
        navigator.nextPage(LocationId, NormalMode)(answers) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      }

      "go to Child Aged Three or Four from Child Aged Two" in {
        navigator.nextPage(ChildAgedTwoId, NormalMode)(mock[UserAnswers]) mustBe routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
      }

      "go to Childcare Costs from Child Aged Three or Four" in {
        navigator.nextPage(ChildAgedThreeOrFourId, NormalMode)(mock[UserAnswers]) mustBe routes.ChildcareCostsController.onPageLoad(NormalMode)
      }

      "go to expect approved childcare cost from childcare cost when you have childcare cost or not yet decided" in {
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("yes") thenReturn Some("notYet")

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.ApprovedProviderController.onPageLoad(NormalMode)
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.ApprovedProviderController.onPageLoad(NormalMode)
      }

      "go to results page from childcare cost if you are not eligible for free hours and don't have the child care cost" in {
        //TODO - results page
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.isEligibleForFreeHours) thenReturn NotEligible
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
      }
    }

      "Has Your Tax Code Been Adjusted" when {
        "single user will be taken to DoYouKnowYourAdjustedTaxCode screen from HasYourTaxCodeBeenAdjusted when yes is selected" in {
          val answers = mock[UserAnswers]
          when(answers.hasPartnerInPaidWork) thenReturn false
          when(answers.hasYourTaxCodeBeenAdjusted) thenReturn Some(true)
          navigator.nextPage(HasYourTaxCodeBeenAdjustedId, NormalMode)(answers) mustBe routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(NormalMode)
        }

        "single user will be taken to DoesYourEmployerOfferChildcareVouchers screen from HasYourTaxCodeBeenAdjusted when no is selected" in {
          val answers = mock[UserAnswers]
          when(answers.hasPartnerInPaidWork) thenReturn false
          when(answers.hasYourTaxCodeBeenAdjusted) thenReturn Some(false)
          navigator.nextPage(HasYourTaxCodeBeenAdjustedId, NormalMode)(answers) mustBe routes.DoesYourEmployerOfferChildcareVouchersController.onPageLoad(NormalMode)
        }

        "user with a partner in paid work will be taken to HasYourPartnerTaxCodeBeenAdjusted screen from HasYourTaxCodeBeenAdjusted when no is selected" in {
          val answers = mock[UserAnswers]
          when(answers.hasPartnerInPaidWork) thenReturn true
          when(answers.hasYourTaxCodeBeenAdjusted) thenReturn Some(false)
          navigator.nextPage(HasYourTaxCodeBeenAdjustedId, NormalMode)(answers) mustBe routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
        }

        "user with a partner in paid work will be taken to DoYouKnowYourAdjustedTaxCode screen from HasYourTaxCodeBeenAdjusted when yes is selected" in {
          val answers = mock[UserAnswers]
          when(answers.hasPartnerInPaidWork) thenReturn true
          when(answers.hasYourTaxCodeBeenAdjusted) thenReturn Some(true)
          navigator.nextPage(HasYourTaxCodeBeenAdjustedId, NormalMode)(answers) mustBe routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(NormalMode)
        }
      }

      "Has Your Partners Tax Code Been Adjusted" when {
        "user with partner will be taken to DoYouKnowYourPartnersAdjustedTaxCode screen from HasYourPartnersTaxCodeBeenAdjusted when yes is selected" in {
          val answers = mock[UserAnswers]
          when(answers.hasPartnerInPaidWork) thenReturn true
          when(answers.hasYourPartnersTaxCodeBeenAdjusted) thenReturn Some(true)
          navigator.nextPage(HasYourPartnersTaxCodeBeenAdjustedId, NormalMode)(answers) mustBe routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(NormalMode)
        }

        "user with partner will be taken to DoEitherOfYourEmployersOfferChildcareVouchers screen from HasYourPartnersTaxCodeBeenAdjusted when no is selected" in {
          val answers = mock[UserAnswers]
          when(answers.hasPartnerInPaidWork) thenReturn true
          when(answers.hasYourPartnersTaxCodeBeenAdjusted) thenReturn Some(false)
          navigator.nextPage(HasYourPartnersTaxCodeBeenAdjustedId, NormalMode)(answers) mustBe routes.DoEitherOfYourEmployersOfferChildcareVouchersController.onPageLoad(NormalMode)
        }
      }

      "go to results page from childcare cost if you are eligible for free hours, have child aged 3 or 4 years and don't have the child care cost" in {
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some("wales") thenReturn Some("scotland") thenReturn Some("northern-ireland")
        when(answers.isEligibleForFreeHours) thenReturn Eligible

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
      }

      "go to results page from childcare cost if you are eligible for free hours, have child aged 2 and don't have the child care cost" in {
      val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedTwo) thenReturn Some(true)
        when(answers.childAgedThreeOrFour) thenReturn Some(false)
        when(answers.location) thenReturn Some("wales") thenReturn Some("scotland") thenReturn Some("england")
        when(answers.isEligibleForFreeHours) thenReturn Eligible

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
      }

      "go to free hours results page if you are eligible for free hours, have child 2 & 3 or 4 years, don't have childcare cost & lives in wales, scotland" in {
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedTwo) thenReturn Some(true)
        when(answers.childAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some("wales") thenReturn Some("scotland")
        when(answers.isEligibleForFreeHours) thenReturn Eligible

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursResultController.onPageLoad()
      }

      "go to free hours info page if you are eligible for free hours, have child aged 3 or 4 years and don't have childcare cost and lives in england" in {
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some("england")
        when(answers.isEligibleForFreeHours) thenReturn Eligible

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursInfoController.onPageLoad()
      }

      "go to free hours info page if you are eligible for free hours, have child 2 and 3 or 4 years, don't have childcare cost and lives in england" in {
        val answers = mock[UserAnswers]
        when(answers.childcareCosts) thenReturn Some("no")
        when(answers.childAgedTwo) thenReturn Some(true)
        when(answers.childAgedThreeOrFour) thenReturn Some(true)
        when(answers.location) thenReturn Some("england")
        when(answers.isEligibleForFreeHours) thenReturn Eligible

        navigator.nextPage(ChildcareCostsId, NormalMode)(answers) mustBe routes.FreeHoursInfoController.onPageLoad()
      }

    }

    "in Check mode" must {

      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, CheckMode)(mock[UserAnswers]) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }


