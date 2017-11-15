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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.CheckMode
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.AnswerRow

class CheckYourAnswersHelper(userAnswers: UserAnswers) {

  def partnerStatutoryPayPerWeek: Option[AnswerRow] = userAnswers.partnerStatutoryPayPerWeek map {
    x => AnswerRow("partnerStatutoryPayPerWeek.checkYourAnswersLabel", s"$x", false, routes.PartnerStatutoryPayPerWeekController.onPageLoad(CheckMode).url)
  }

  def yourStatutoryPayPerWeek: Option[AnswerRow] = userAnswers.yourStatutoryPayPerWeek map {
    x => AnswerRow("yourStatutoryPayPerWeek.checkYourAnswersLabel", s"$x", false, routes.YourStatutoryPayPerWeekController.onPageLoad(CheckMode).url)
  }

  def partnerStatutoryPayBeforeTax: Option[AnswerRow] = userAnswers.partnerStatutoryPayBeforeTax map {
    x => AnswerRow("partnerStatutoryPayBeforeTax.checkYourAnswersLabel", s"partnerStatutoryPayBeforeTax.$x", true, routes.PartnerStatutoryPayBeforeTaxController.onPageLoad(CheckMode).url)
  }

  def yourStatutoryPayBeforeTax: Option[AnswerRow] = userAnswers.yourStatutoryPayBeforeTax map {
    x => AnswerRow("yourStatutoryPayBeforeTax.checkYourAnswersLabel", s"yourStatutoryPayBeforeTax.$x", true, routes.YourStatutoryPayBeforeTaxController.onPageLoad(CheckMode).url)
  }

  def partnerStatutoryWeeks: Option[AnswerRow] = userAnswers.partnerStatutoryWeeks map {
    x => AnswerRow("partnerStatutoryWeeks.checkYourAnswersLabel", s"$x", false, routes.PartnerStatutoryWeeksController.onPageLoad(CheckMode).url)
  }

  def yourStatutoryWeeks: Option[AnswerRow] = userAnswers.yourStatutoryWeeks map {
    x => AnswerRow("yourStatutoryWeeks.checkYourAnswersLabel", s"$x", false, routes.YourStatutoryWeeksController.onPageLoad(CheckMode).url)
  }

  def yourStatutoryPayType: Option[AnswerRow] = userAnswers.yourStatutoryPayType map {
    x => AnswerRow("yourStatutoryPayType.checkYourAnswersLabel", s"yourStatutoryPayType.$x", true, routes.YourStatutoryPayTypeController.onPageLoad(CheckMode).url)
  }

  def partnerStatutoryPayType: Option[AnswerRow] = userAnswers.partnerStatutoryPayType map {
    x => AnswerRow("partnerStatutoryPayType.checkYourAnswersLabel", s"partnerStatutoryPayType.$x", true, routes.PartnerStatutoryPayTypeController.onPageLoad(CheckMode).url)
  }

  def partnerStatutoryPay: Option[AnswerRow] = userAnswers.partnerStatutoryPay map {
    x => AnswerRow("partnerStatutoryPay.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.PartnerStatutoryPayController.onPageLoad(CheckMode).url)
  }

  def bothStatutoryPay: Option[AnswerRow] = userAnswers.bothStatutoryPay map {
    x => AnswerRow("bothStatutoryPay.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.BothStatutoryPayController.onPageLoad(CheckMode).url)
  }

  def youStatutoryPay: Option[AnswerRow] = userAnswers.youStatutoryPay map {
    x => AnswerRow("youStatutoryPay.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.YouStatutoryPayController.onPageLoad(CheckMode).url)
  }

  def whoGotStatutoryPay: Option[AnswerRow] = userAnswers.whoGotStatutoryPay map {
    x => AnswerRow("whoGotStatutoryPay.checkYourAnswersLabel", s"whoGotStatutoryPay.$x", true, routes.WhoGotStatutoryPayController.onPageLoad(CheckMode).url)
  }

  def expectedChildcareCosts(index: Int): Option[AnswerRow] = userAnswers.expectedChildcareCosts map {
    x => AnswerRow("expectedChildcareCosts.checkYourAnswersLabel", s"$x", false, routes.ExpectedChildcareCostsController.onPageLoad(CheckMode, index).url)
  }

  def whoHasChildcareCosts: Option[AnswerRow] = userAnswers.whoHasChildcareCosts map {
    x => AnswerRow("whoHasChildcareCosts.checkYourAnswersLabel", s"whoHasChildcareCosts.$x", true, routes.WhoHasChildcareCostsController.onPageLoad(CheckMode).url)
  }

  def whichDisabilityBenefits(index: Int): Option[AnswerRow] = userAnswers.whichDisabilityBenefits(index) map {
    x => AnswerRow("whichDisabilityBenefits.checkYourAnswersLabel", s"whichDisabilityBenefits.$x", true, routes.WhichDisabilityBenefitsController.onPageLoad(CheckMode, index).url)
  }

  def whichChildrenBlind: Option[AnswerRow] = userAnswers.whichChildrenBlind map {
    x => AnswerRow("whichChildrenBlind.checkYourAnswersLabel", s"whichChildrenBlind.$x", true, routes.WhichChildrenBlindController.onPageLoad(CheckMode).url)
  }

  def whichChildrenDisability: Option[AnswerRow] = userAnswers.whichChildrenDisability map {
    x => AnswerRow("whichChildrenDisability.checkYourAnswersLabel", s"whichChildrenDisability.$x", true, routes.WhichChildrenDisabilityController.onPageLoad(CheckMode).url)
  }

  def childStartEducation(index: Int): Option[AnswerRow] = userAnswers.childStartEducation(index) map {
    x => AnswerRow("childStartEducation.checkYourAnswersLabel", s"$x", false, routes.ChildStartEducationController.onPageLoad(CheckMode, index).url)
  }

  def childApprovedEducation(index: Int): Option[AnswerRow] = userAnswers.childApprovedEducation(index) map {
    x => AnswerRow("childApprovedEducation.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.ChildApprovedEducationController.onPageLoad(CheckMode, index).url)
  }

  def childrenDisabilityBenefits: Option[AnswerRow] = userAnswers.childrenDisabilityBenefits map {
    x => AnswerRow("childrenDisabilityBenefits.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.ChildrenDisabilityBenefitsController.onPageLoad(CheckMode).url)
  }

  def childcarePayFrequency(index: Int): Option[AnswerRow] = userAnswers.childcarePayFrequency map {
    x => AnswerRow("childcarePayFrequency.checkYourAnswersLabel", s"childcarePayFrequency.$x", true, routes.ChildcarePayFrequencyController.onPageLoad(CheckMode, index).url)
  }

  def employmentIncomePY: Option[AnswerRow] = userAnswers.employmentIncomePY map {
    x => AnswerRow("employmentIncomePY.checkYourAnswersLabel", s"${x.parentEmploymentIncomePY} ${x.partnerEmploymentIncomePY}", false, routes.EmploymentIncomePYController.onPageLoad(CheckMode).url)
  }

  def partnerEmploymentIncomePY: Option[AnswerRow] = userAnswers.partnerEmploymentIncomePY map {
    x => AnswerRow("partnerEmploymentIncomePY.checkYourAnswersLabel", s"$x", false, routes.PartnerEmploymentIncomePYController.onPageLoad(CheckMode).url)
  }

  def parentEmploymentIncomePY: Option[AnswerRow] = userAnswers.parentEmploymentIncomePY map {
    x => AnswerRow("parentEmploymentIncomePY.checkYourAnswersLabel", s"$x", false, routes.ParentEmploymentIncomePYController.onPageLoad(CheckMode).url)
  }

  def howMuchPartnerPayPensionPY: Option[AnswerRow] = userAnswers.howMuchPartnerPayPensionPY map {
    x => AnswerRow("howMuchPartnerPayPensionPY.checkYourAnswersLabel", s"$x", false, routes.HowMuchPartnerPayPensionPYController.onPageLoad(CheckMode).url)
  }

  def howMuchYouPayPensionPY: Option[AnswerRow] = userAnswers.howMuchYouPayPensionPY map {
    x => AnswerRow("howMuchYouPayPensionPY.checkYourAnswersLabel", s"$x", false, routes.HowMuchYouPayPensionPYController.onPageLoad(CheckMode).url)
  }

  def howMuchBothPayPensionPY: Option[AnswerRow] = userAnswers.howMuchBothPayPensionPY map {
    x => AnswerRow("howMuchBothPayPensionPY.checkYourAnswersLabel", s"${x.field1} ${x.field2}", false, routes.HowMuchBothPayPensionPYController.onPageLoad(CheckMode).url)
  }

  def partnerEmploymentIncomeCY: Option[AnswerRow] = userAnswers.partnerEmploymentIncomeCY map {
    x => AnswerRow("partnerEmploymentIncomeCY.checkYourAnswersLabel", s"$x", false, routes.PartnerEmploymentIncomeCYController.onPageLoad(CheckMode).url)
  }

  def parentEmploymentIncomeCY: Option[AnswerRow] = userAnswers.parentEmploymentIncomeCY map {
    x => AnswerRow("parentEmploymentIncomeCY.checkYourAnswersLabel", s"$x", false, routes.ParentEmploymentIncomeCYController.onPageLoad(CheckMode).url)
  }

  def otherIncomeAmountPY: Option[AnswerRow] = userAnswers.otherIncomeAmountPY map {
    x => AnswerRow("otherIncomeAmountPY.checkYourAnswersLabel", s"${x.parentOtherIncomeAmountPY} ${x.partnerOtherIncomeAmountPY}", false, routes.OtherIncomeAmountPYController.onPageLoad(CheckMode).url)
  }

  def partnerOtherIncomeAmountPY: Option[AnswerRow] = userAnswers.partnerOtherIncomeAmountPY map {
    x => AnswerRow("partnerOtherIncomeAmountPY.checkYourAnswersLabel", s"$x", false, routes.PartnerOtherIncomeAmountPYController.onPageLoad(CheckMode).url)
  }

  def yourOtherIncomeAmountPY: Option[AnswerRow] = userAnswers.yourOtherIncomeAmountPY map {
    x => AnswerRow("yourOtherIncomeAmountPY.checkYourAnswersLabel", s"$x", false, routes.YourOtherIncomeAmountPYController.onPageLoad(CheckMode).url)
  }

  def aboutYourChild(index: Int): Option[AnswerRow] = userAnswers.aboutYourChild(index) map {
    x => AnswerRow("aboutYourChild.checkYourAnswersLabel", s"${x.name} ${x.dob}", false, routes.AboutYourChildController.onPageLoad(CheckMode, index).url)
  }

  def howMuchBothPayPension: Option[AnswerRow] = userAnswers.howMuchBothPayPension map {
    x => AnswerRow("howMuchBothPayPension.checkYourAnswersLabel", s"${x.field1} ${x.field2}", false, routes.HowMuchBothPayPensionController.onPageLoad(CheckMode).url)
  }

  def howMuchPartnerPayPension: Option[AnswerRow] = userAnswers.howMuchPartnerPayPension map {
    x => AnswerRow("howMuchPartnerPayPension.checkYourAnswersLabel", s"$x", false, routes.HowMuchPartnerPayPensionController.onPageLoad(CheckMode).url)
  }

  def howMuchYouPayPension: Option[AnswerRow] = userAnswers.howMuchYouPayPension map {
    x => AnswerRow("howMuchYouPayPension.checkYourAnswersLabel", s"$x", false, routes.HowMuchYouPayPensionController.onPageLoad(CheckMode).url)
  }

  def youBenefitsIncomePY: Option[AnswerRow] = userAnswers.youBenefitsIncomePY map {
    x => AnswerRow("youBenefitsIncomePY.checkYourAnswersLabel", s"$x", false, routes.YouBenefitsIncomePYController.onPageLoad(CheckMode).url)
  }

  def partnerBenefitsIncomePY: Option[AnswerRow] = userAnswers.partnerBenefitsIncomePY map {
    x => AnswerRow("partnerBenefitsIncomePY.checkYourAnswersLabel", s"$x", false, routes.PartnerBenefitsIncomePYController.onPageLoad(CheckMode).url)
  }

  def bothBenefitsIncomePY: Option[AnswerRow] = userAnswers.bothBenefitsIncomePY map {
    x => AnswerRow("bothBenefitsIncomePY.checkYourAnswersLabel", s"${x.field1} ${x.field2}", false, routes.BothBenefitsIncomePYController.onPageLoad(CheckMode).url)
  }

  def registeredBlind: Option[AnswerRow] = userAnswers.registeredBlind map {
    x => AnswerRow("registeredBlind.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.RegisteredBlindController.onPageLoad(CheckMode).url)
  }


  def whosHadBenefitsPY: Option[AnswerRow] = userAnswers.whosHadBenefitsPY map {
    x => AnswerRow("whosHadBenefitsPY.checkYourAnswersLabel", s"whosHadBenefitsPY.$x", true, routes.WhosHadBenefitsPYController.onPageLoad(CheckMode).url)
  }

  def benefitsIncomeCY: Option[AnswerRow] = userAnswers.benefitsIncomeCY map {
    x => AnswerRow("benefitsIncomeCY.checkYourAnswersLabel", s"${x.parentBenefitsIncome} ${x.partnerBenefitsIncome}", false, routes.BenefitsIncomeCYController.onPageLoad(CheckMode).url)
  }

  def bothOtherIncomeLY: Option[AnswerRow] = userAnswers.bothOtherIncomeLY map {
    x => AnswerRow("bothOtherIncomeLY.checkYourAnswersLabel", if (x) "site.yes" else "site.no", true, routes.BothOtherIncomeLYController.onPageLoad(CheckMode).url)
  }
  def partnerAnyOtherIncomeLY: Option[AnswerRow] = userAnswers.partnerAnyOtherIncomeLY map {
    x => AnswerRow("partnerAnyOtherIncomeLY.checkYourAnswersLabel", if (x) "site.yes" else "site.no", true, routes.PartnerAnyOtherIncomeLYController.onPageLoad(CheckMode).url)
  }

  def bothAnyTheseBenefitsPY: Option[AnswerRow] = userAnswers.bothAnyTheseBenefitsPY map {
    x => AnswerRow("bothAnyTheseBenefitsPY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.BothAnyTheseBenefitsPYController.onPageLoad(CheckMode).url)
  }

  def partnerAnyTheseBenefitsPY: Option[AnswerRow] = userAnswers.partnerAnyTheseBenefitsPY map {
    x => AnswerRow("partnerAnyTheseBenefitsPY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.PartnerAnyTheseBenefitsPYController.onPageLoad(CheckMode).url)
  }

  def youAnyTheseBenefitsPY: Option[AnswerRow] = userAnswers.youAnyTheseBenefitsPY map {
    x => AnswerRow("youAnyTheseBenefitsPY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.YouAnyTheseBenefitsPYController.onPageLoad(CheckMode).url)
  }

  def whoPaidIntoPensionPY: Option[AnswerRow] = userAnswers.whoPaidIntoPensionPY map {
    x => AnswerRow("whoPaidIntoPensionPY.checkYourAnswersLabel", s"whoPaidIntoPensionPY.$x", true, routes.WhoPaidIntoPensionPYController.onPageLoad(CheckMode).url)
  }

  def whoOtherIncomePY: Option[AnswerRow] = userAnswers.whoOtherIncomePY map {
    x => AnswerRow("whoOtherIncomePY.checkYourAnswersLabel", s"whoOtherIncomePY.$x", true, routes.WhoOtherIncomePYController.onPageLoad(CheckMode).url)
  }

  def youBenefitsIncomeCY: Option[AnswerRow] = userAnswers.youBenefitsIncomeCY map {
    x => AnswerRow("youBenefitsIncomeCY.checkYourAnswersLabel", s"$x", false, routes.YouBenefitsIncomeCYController.onPageLoad(CheckMode).url)
  }

  def partnerBenefitsIncomeCY: Option[AnswerRow] = userAnswers.partnerBenefitsIncomeCY map {
    x => AnswerRow("partnerBenefitsIncomeCY.checkYourAnswersLabel", s"$x", false, routes.PartnerBenefitsIncomeCYController.onPageLoad(CheckMode).url)
  }
  def yourOtherIncomeLY: Option[AnswerRow] = userAnswers.yourOtherIncomeLY map {
    x => AnswerRow("yourOtherIncomeLY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.YourOtherIncomeLYController.onPageLoad(CheckMode).url)
  }

  def bothPaidPensionPY: Option[AnswerRow] = userAnswers.bothPaidPensionPY map {
    x => AnswerRow("bothPaidPensionPY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.BothPaidPensionPYController.onPageLoad(CheckMode).url)
  }

  def partnerPaidPensionPY: Option[AnswerRow] = userAnswers.partnerPaidPensionPY map {
    x => AnswerRow("partnerPaidPensionPY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.PartnerPaidPensionPYController.onPageLoad(CheckMode).url)
  }

  def youPaidPensionPY: Option[AnswerRow] = userAnswers.youPaidPensionPY map {
    x => AnswerRow("youPaidPensionPY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.YouPaidPensionPYController.onPageLoad(CheckMode).url)
  }

  def bothPaidWorkPY: Option[AnswerRow] = userAnswers.bothPaidWorkPY map {
    x => AnswerRow("bothPaidWorkPY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.BothPaidWorkPYController.onPageLoad(CheckMode).url)
  }

  def partnerPaidWorkPY: Option[AnswerRow] = userAnswers.partnerPaidWorkPY map {
    x => AnswerRow("partnerPaidWorkPY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.PartnerPaidWorkPYController.onPageLoad(CheckMode).url)
  }

  def parentPaidWorkPY: Option[AnswerRow] = userAnswers.parentPaidWorkPY map {
    x => AnswerRow("parentPaidWorkPY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.ParentPaidWorkPYController.onPageLoad(CheckMode).url)
  }

  def bothOtherIncomeThisYear: Option[AnswerRow] = userAnswers.bothOtherIncomeThisYear map {
    x => AnswerRow("bothOtherIncomeThisYear.checkYourAnswersLabel", if (x) "site.yes" else "site.no", true, routes.BothOtherIncomeThisYearController.onPageLoad(CheckMode).url)
  }

  def bothPaidPensionCY: Option[AnswerRow] = userAnswers.bothPaidPensionCY map {
    x => AnswerRow("bothPaidPensionCY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.BothPaidPensionCYController.onPageLoad(CheckMode).url)
  }

  def whosHadBenefits: Option[AnswerRow] = userAnswers.whosHadBenefits map {
    x => AnswerRow("whosHadBenefits.checkYourAnswersLabel", s"whosHadBenefits.$x", true, routes.WhosHadBenefitsController.onPageLoad(CheckMode).url)
  }

  def employmentIncomeCY: Option[AnswerRow] = userAnswers.employmentIncomeCY map {
    x => AnswerRow("employmentIncomeCY.checkYourAnswersLabel", s"${x.parentEmploymentIncome} ${x.partnerEmploymentIncome}", false, routes.EmploymentIncomeCYController.onPageLoad(CheckMode).url)
  }

  def partnerOtherIncomeAmountCY: Option[AnswerRow] = userAnswers.partnerOtherIncomeAmountCY map {
    x => AnswerRow("partnerOtherIncomeAmountCY.checkYourAnswersLabel", s"$x", false, routes.PartnerOtherIncomeAmountCYController.onPageLoad(CheckMode).url)
  }

  def yourOtherIncomeAmountCY: Option[AnswerRow] = userAnswers.yourOtherIncomeAmountCY map {
    x => AnswerRow("yourOtherIncomeAmountCY.checkYourAnswersLabel", s"$x", false, routes.YourOtherIncomeAmountCYController.onPageLoad(CheckMode).url)
  }

  def otherIncomeAmountCY: Option[AnswerRow] = userAnswers.otherIncomeAmountCY map {
    x => AnswerRow("otherIncomeAmountCY.checkYourAnswersLabel", s"${x.partnerOtherIncome} ${x.partnerOtherIncome}", false, routes.OtherIncomeAmountCYController.onPageLoad(CheckMode).url)
  }

  def whichBenefitsPartnerGet: Option[AnswerRow] = userAnswers.whichBenefitsPartnerGet map {
    x => AnswerRow("whichBenefitsPartnerGet.checkYourAnswersLabel", s"whichBenefitsPartnerGet.$x", true, routes.WhichBenefitsPartnerGetController.onPageLoad(CheckMode).url)
  }

  def whichBenefitsYouGet: Option[AnswerRow] = userAnswers.whichBenefitsYouGet map {
    x => AnswerRow("whichBenefitsYouGet.checkYourAnswersLabel", s"whichBenefitsYouGet.$x", true, routes.WhichBenefitsYouGetController.onPageLoad(CheckMode).url)
  }

  def whoGetsOtherIncomeCY: Option[AnswerRow] = userAnswers.whoGetsOtherIncomeCY map {
    x => AnswerRow("whoGetsOtherIncomeCY.checkYourAnswersLabel", s"whoGetsOtherIncomeCY.$x", true, routes.WhoGetsOtherIncomeCYController.onPageLoad(CheckMode).url)
  }

  def bothPaidWorkCY: Option[AnswerRow] = userAnswers.bothPaidWorkCY map {
    x => AnswerRow("bothPaidWorkCY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.BothPaidWorkCYController.onPageLoad(CheckMode).url)
  }

  def partnerPaidWorkCY: Option[AnswerRow] = userAnswers.partnerPaidWorkCY map {
    x => AnswerRow("partnerPaidWorkCY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.PartnerPaidWorkCYController.onPageLoad(CheckMode).url)
  }

  def parentPaidWorkCY: Option[AnswerRow] = userAnswers.parentPaidWorkCY map {
    x => AnswerRow("parentPaidWorkCY.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.ParentPaidWorkCYController.onPageLoad(CheckMode).url)
  }

  def whoPaysIntoPension: Option[AnswerRow] = userAnswers.whoPaysIntoPension map {
    x => AnswerRow("whoPaysIntoPension.checkYourAnswersLabel", s"whoPaysIntoPension.$x", true, routes.WhoPaysIntoPensionController.onPageLoad(CheckMode).url)
  }

  def partnerAnyOtherIncomeThisYear: Option[AnswerRow] = userAnswers.partnerAnyOtherIncomeThisYear map {
    x => AnswerRow("partnerAnyOtherIncomeThisYear.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.PartnerAnyOtherIncomeThisYearController.onPageLoad(CheckMode).url)

  }

  def yourOtherIncomeThisYear: Option[AnswerRow] = userAnswers.yourOtherIncomeThisYear map {
    x => AnswerRow("yourOtherIncomeThisYear.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.YourOtherIncomeThisYearController.onPageLoad(CheckMode).url)
  }

  def eitherOfYouMaximumEarnings: Option[AnswerRow] = userAnswers.eitherOfYouMaximumEarnings map {
    x => AnswerRow("eitherOfYouMaximumEarnings.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.EitherOfYouMaximumEarningsController.onPageLoad(CheckMode).url)
  }

  def noOfChildren: Option[AnswerRow] = userAnswers.noOfChildren map {
    x => AnswerRow("noOfChildren.checkYourAnswersLabel", s"$x", false, routes.NoOfChildrenController.onPageLoad(CheckMode).url)
  }

  def taxOrUniversalCredits: Option[AnswerRow] = userAnswers.taxOrUniversalCredits map {
    x => AnswerRow("taxOrUniversalCredits.checkYourAnswersLabel", s"taxOrUniversalCredits.$x", true, routes.TaxOrUniversalCreditsController.onPageLoad(CheckMode).url)
  }

  def yourSelfEmployed: Option[AnswerRow] = userAnswers.yourSelfEmployed map {
    x => AnswerRow("yourSelfEmployed.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.YourSelfEmployedController.onPageLoad(CheckMode).url)
  }

  def partnerSelfEmployed: Option[AnswerRow] = userAnswers.partnerSelfEmployed map {
    x => AnswerRow("partnerSelfEmployed.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.PartnerSelfEmployedController.onPageLoad(CheckMode).url)
  }

  def partnerSelfEmployedOrApprentice: Option[AnswerRow] = userAnswers.partnerSelfEmployedOrApprentice map {
    x => AnswerRow("partnerSelfEmployedOrApprentice.checkYourAnswersLabel", s"partnerSelfEmployedOrApprentice.$x", true, routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(CheckMode).url)
  }

  def partnerMinimumEarnings: Option[AnswerRow] = userAnswers.partnerMinimumEarnings map {
    x => AnswerRow("partnerMinimumEarnings.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.PartnerMinimumEarningsController.onPageLoad(CheckMode).url)
  }

  def partnerMaximumEarnings: Option[AnswerRow] = userAnswers.partnerMaximumEarnings map {
    x => AnswerRow("partnerMaximumEarnings.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.PartnerMaximumEarningsController.onPageLoad(CheckMode).url)
  }

  def yourMaximumEarnings: Option[AnswerRow] = userAnswers.yourMaximumEarnings map {
    x => AnswerRow("yourMaximumEarnings.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.YourMaximumEarningsController.onPageLoad(CheckMode).url)
  }

  def areYouSelfEmployedOrApprentice: Option[AnswerRow] = userAnswers.areYouSelfEmployedOrApprentice map {
    x => AnswerRow("areYouSelfEmployedOrApprentice.checkYourAnswersLabel", s"areYouSelfEmployedOrApprentice.$x", true, routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(CheckMode).url)
  }

  def yourMinimumEarnings: Option[AnswerRow] = userAnswers.yourMinimumEarnings map {
    x => AnswerRow("yourMinimumEarnings.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.YourMinimumEarningsController.onPageLoad(CheckMode).url)
  }

  def yourAge: Option[AnswerRow] = userAnswers.yourAge map {
    x => AnswerRow("yourAge.checkYourAnswersLabel", s"yourAge.$x", true, routes.YourAgeController.onPageLoad(CheckMode).url)
  }

  def yourPartnersAge: Option[AnswerRow] = userAnswers.yourPartnersAge map {
    x => AnswerRow("yourPartnersAge.checkYourAnswersLabel", s"yourPartnersAge.$x", true, routes.YourPartnersAgeController.onPageLoad(CheckMode).url)
  }

  def yourChildcareVouchers: Option[AnswerRow] = userAnswers.yourChildcareVouchers map {
    x => AnswerRow("yourChildcareVouchers.checkYourAnswersLabel", s"yourChildcareVouchers.$x", true, routes.YourChildcareVouchersController.onPageLoad(CheckMode).url)
  }

  def partnerChildcareVouchers: Option[AnswerRow] = userAnswers.partnerChildcareVouchers map {
    x => AnswerRow("partnerChildcareVouchers.checkYourAnswersLabel", s"partnerChildcareVouchers.$x", true, routes.PartnerChildcareVouchersController.onPageLoad(CheckMode).url)
  }

  def whatIsYourPartnersTaxCode: Option[AnswerRow] = userAnswers.whatIsYourPartnersTaxCode map {
    x => AnswerRow("whatIsYourPartnersTaxCode.checkYourAnswersLabel", s"$x", false, routes.WhatIsYourPartnersTaxCodeController.onPageLoad(CheckMode).url)
  }

  def whatIsYourTaxCode: Option[AnswerRow] = userAnswers.whatIsYourTaxCode map {
    x => AnswerRow("whatIsYourTaxCode.checkYourAnswersLabel", s"$x", false, routes.WhatIsYourTaxCodeController.onPageLoad(CheckMode).url)
  }

  def whoGetsBenefits: Option[AnswerRow] = userAnswers.whoGetsBenefits map {
    x => AnswerRow("whoGetsBenefits.checkYourAnswersLabel", s"whoGetsBenefits.$x", true, routes.WhoGetsBenefitsController.onPageLoad(CheckMode).url)
  }

  def doYouOrYourPartnerGetAnyBenefits: Option[AnswerRow] = userAnswers.doYouOrYourPartnerGetAnyBenefits map {
    x => AnswerRow("doYouOrYourPartnerGetAnyBenefits.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(CheckMode).url)
  }

  def doYouGetAnyBenefits: Option[AnswerRow] = userAnswers.doYouGetAnyBenefits map {
    x => AnswerRow("doYouGetAnyBenefits.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.DoYouGetAnyBenefitsController.onPageLoad(CheckMode).url)
  }

  def doYouKnowYourPartnersAdjustedTaxCode: Option[AnswerRow] = userAnswers.doYouKnowYourPartnersAdjustedTaxCode map {
    x => AnswerRow("doYouKnowYourPartnersAdjustedTaxCode.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.DoYouKnowYourPartnersAdjustedTaxCodeController.onPageLoad(CheckMode).url)
  }

  def areYouInPaidWork: Option[AnswerRow] = userAnswers.areYouInPaidWork map {
    x => AnswerRow("areYouInPaidWork.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.AreYouInPaidWorkController.onPageLoad(CheckMode).url)
  }

  def whoGetsVouchers: Option[AnswerRow] = userAnswers.whoGetsVouchers map {
    x => AnswerRow("whoGetsVouchers.checkYourAnswersLabel", s"whoGetsVouchers.$x", true, routes.WhoGetsVouchersController.onPageLoad(CheckMode).url)
  }

  def eitherGetsVouchers: Option[AnswerRow] = userAnswers.eitherGetsVouchers map {
    x => AnswerRow("eitherGetsVouchers.checkYourAnswersLabel", s"vouchers.$x", true, routes.EitherGetsVouchersController.onPageLoad(CheckMode).url)
  }

      def hasYourTaxCodeBeenAdjusted: Option[AnswerRow] = userAnswers.hasYourTaxCodeBeenAdjusted map {
        x => AnswerRow("hasYourTaxCodeBeenAdjusted.checkYourAnswersLabel", s"hasYourTaxCodeBeenAdjusted.$x", true, routes.HasYourTaxCodeBeenAdjustedController.onPageLoad(CheckMode).url)
      }

      def hasYourPartnersTaxCodeBeenAdjusted: Option[AnswerRow] = userAnswers.hasYourPartnersTaxCodeBeenAdjusted map {
        x => AnswerRow("hasYourPartnersTaxCodeBeenAdjusted.checkYourAnswersLabel", s"hasYourPartnersTaxCodeBeenAdjusted.$x", true, routes.HasYourPartnersTaxCodeBeenAdjustedController.onPageLoad(CheckMode).url)
}
  def doYouKnowYourAdjustedTaxCode: Option[AnswerRow] = userAnswers.doYouKnowYourAdjustedTaxCode map {
    x => AnswerRow("doYouKnowYourAdjustedTaxCode.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.DoYouKnowYourAdjustedTaxCodeController.onPageLoad(CheckMode).url)
  }

  def partnerWorkHours: Option[AnswerRow] = userAnswers.partnerWorkHours map {
    x => AnswerRow("partnerWorkHours.checkYourAnswersLabel", s"$x", false, routes.PartnerWorkHoursController.onPageLoad(CheckMode).url)
  }

  def parentWorkHours: Option[AnswerRow] = userAnswers.parentWorkHours map {
    x => AnswerRow("parentWorkHours.checkYourAnswersLabel", s"$x", false, routes.ParentWorkHoursController.onPageLoad(CheckMode).url)
  }

  def whoIsInPaidEmployment: Option[AnswerRow] = userAnswers.whoIsInPaidEmployment map {
    x => AnswerRow("whoIsInPaidEmployment.checkYourAnswersLabel", s"whoIsInPaidEmployment.$x", true, routes.WhoIsInPaidEmploymentController.onPageLoad(CheckMode).url)
  }

  def paidEmployment: Option[AnswerRow] = userAnswers.paidEmployment map {
    x => AnswerRow("paidEmployment.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.PaidEmploymentController.onPageLoad(CheckMode).url)
  }

  def doYouLiveWithPartner: Option[AnswerRow] = userAnswers.doYouLiveWithPartner map {
    x => AnswerRow("doYouLiveWithPartner.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.DoYouLiveWithPartnerController.onPageLoad(CheckMode).url)
  }

  def approvedProvider: Option[AnswerRow] = userAnswers.approvedProvider map {
    x => AnswerRow("approvedProvider.checkYourAnswersLabel", s"approvedProvider.$x", true, routes.ApprovedProviderController.onPageLoad(CheckMode).url)
  }

  def childcareCosts: Option[AnswerRow] = userAnswers.childcareCosts map {
    x => AnswerRow("childcareCosts.checkYourAnswersLabel", s"childcareCosts.$x", true, routes.ChildcareCostsController.onPageLoad(CheckMode).url)
  }

  def childAgedThreeOrFour: Option[AnswerRow] = userAnswers.childAgedThreeOrFour map {
    x => AnswerRow("childAgedThreeOrFour.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.ChildAgedThreeOrFourController.onPageLoad(CheckMode).url)
  }

  def childAgedTwo: Option[AnswerRow] = userAnswers.childAgedTwo map {
    x => AnswerRow("childAgedTwo.checkYourAnswersLabel", if(x) "site.yes" else "site.no", true, routes.ChildAgedTwoController.onPageLoad(CheckMode).url)
  }

  def location: Option[AnswerRow] = userAnswers.location map {
    x => AnswerRow("location.checkYourAnswersLabel", s"location.$x", true, routes.LocationController.onPageLoad(CheckMode).url)
  }
}
