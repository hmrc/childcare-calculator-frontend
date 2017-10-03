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

      "go to Expect Childcare Costs from Child Aged Three or Four" in {
        navigator.nextPage(ChildAgedThreeOrFourId, NormalMode)(mock[UserAnswers]) mustBe routes.ExpectChildcareCostsController.onPageLoad(NormalMode)
      }

      "go to free hours info from childcare cost" in {
        navigator.nextPage(ExpectChildcareCostsId, NormalMode)(mock[UserAnswers]) mustBe routes.FreeHoursInfoController.onPageLoad
      }

      "go to do you live with partner from free hours info" in {
        navigator.nextPage(FreeHoursInfoId, NormalMode)(mock[UserAnswers]) mustBe routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)
      }

      "single user will be taken to DoYouKnowYourAdjustedTaxCode screen from HasYourTaxCodeBeenAdjusted when yes is selected" in {
        val answers = mock[UserAnswers]
        when(answers.hasYourTaxCodeBeenAdjusted) thenReturn Some(true)
        when(answers.doYouLiveWithPartner) thenReturn Some (false)
        navigator.nextPage(HasYourTaxCodeBeenAdjustedId, NormalMode)(mock[UserAnswers]) mustBe routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(NormalMode)
      }

      "single user will be taken to DoesYourEmployerOfferChildcareVouchers screen from HasYourTaxCodeBeenAdjusted when no is selected" in {
        val answers = mock[UserAnswers]
        when(answers.hasYourTaxCodeBeenAdjusted) thenReturn Some(false)
        when(answers.doYouLiveWithPartner) thenReturn Some (false)
        navigator.nextPage(HasYourTaxCodeBeenAdjustedId, NormalMode)(mock[UserAnswers]) mustBe routes.DoesYourEmployerOfferChildcareVouchersController.onPageLoad(NormalMode)
      }

      "user with a partner in paid work will be taken to HasYourPartnerTaxCodeBeenAdjusted screen from HasYourTaxCodeBeenAdjusted when no is selected" in {
        val answers = mock[UserAnswers]
        when(answers.hasYourTaxCodeBeenAdjusted) thenReturn Some(false)
        when(answers.doYouLiveWithPartner) thenReturn Some (true)
        when(answers.whoIsInPaidEmployment) thenReturn Some ("both")
        navigator.nextPage(HasYourTaxCodeBeenAdjustedId, NormalMode)(mock[UserAnswers]) mustBe routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
      }

      "user with a partner in paid work will be taken to HasYourPartnerTaxCodeBeenAdjusted screen from HasYourTaxCodeBeenAdjusted when yes is selected" in {
        val answers = mock[UserAnswers]
        when(answers.hasYourTaxCodeBeenAdjusted) thenReturn Some(false)
        when(answers.doYouLiveWithPartner) thenReturn Some (true)
        when(answers.whoIsInPaidEmployment) thenReturn Some ("partner")
        navigator.nextPage(HasYourTaxCodeBeenAdjustedId, NormalMode)(mock[UserAnswers]) mustBe routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(NormalMode)
      }
    }

    "in Check mode" must {
      "go to CheckYourAnswers from an identifier that doesn't exist in the edit route map" in {
        case object UnknownIdentifier extends Identifier
        navigator.nextPage(UnknownIdentifier, CheckMode)(mock[UserAnswers]) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
  }
}
