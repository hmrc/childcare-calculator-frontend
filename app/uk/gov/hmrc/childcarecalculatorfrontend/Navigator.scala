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

import javax.inject.{Inject, Singleton}

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{ParentEmploymentIncomeCYId, PartnerEmploymentIncomeCYId, PartnerPaidPensionCYId, PartnerPaidWorkCYId, YouPaidPensionCYId, _}
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.navigation._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{Utils, UserAnswers}

@Singleton
class Navigator @Inject()(schemes: Schemes,
                          employmentIncomeNav: EmploymentIncomeNavigation = new EmploymentIncomeNavigation(),
                          pensionNav: PensionNavigation = new PensionNavigation(),
                          minHoursNav: MinimumHoursNavigation = new MinimumHoursNavigation(),
                          maxHoursNav: MaximumHoursNavigation = new MaximumHoursNavigation(),
                          StatutoryPayNav: StatutoryPayNavigator = new StatutoryPayNavigator()) {

  val You: String = YouPartnerBothEnum.YOU.toString
  val Partner: String = YouPartnerBothEnum.PARTNER.toString
  val Both: String = YouPartnerBothEnum.BOTH.toString


  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId -> minHoursNav.locationRoute,
    ChildAgedTwoId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)),
    ChildAgedThreeOrFourId -> (_ => routes.ChildcareCostsController.onPageLoad(NormalMode)),
    ChildcareCostsId -> minHoursNav.costRoute,
    ApprovedProviderId -> minHoursNav.approvedChildCareRoute,
    FreeHoursInfoId -> (_ => routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)),
    DoYouLiveWithPartnerId -> maxHoursNav.doYouLiveRoute,
    AreYouInPaidWorkId -> maxHoursNav.areYouInPaidWorkRoute,
    PaidEmploymentId -> maxHoursNav.paidEmploymentRoute,
    WhoIsInPaidEmploymentId -> maxHoursNav.whoIsInPaidWorkRoute,
    ParentWorkHoursId -> (_ => routes.HasYourTaxCodeBeenAdjustedController.onPageLoad(NormalMode)),
    PartnerWorkHoursId -> maxHoursNav.partnerWorkHoursRoute,
    HasYourTaxCodeBeenAdjustedId -> maxHoursNav.hasYourTaxCodeBeenAdjusted,
    DoYouKnowYourAdjustedTaxCodeId -> maxHoursNav.doYouKnowYourAdjustedTaxCodeRoute,
    WhatIsYourTaxCodeId -> maxHoursNav.whatIsYourTaxCodeRoute,
    HasYourPartnersTaxCodeBeenAdjustedId -> maxHoursNav.hasYourPartnersTaxCodeBeenAdjusted,
    DoYouKnowYourPartnersAdjustedTaxCodeId -> maxHoursNav.doYouKnowPartnersTaxCodeRoute,
    WhatIsYourPartnersTaxCodeId -> maxHoursNav.whatIsYourPartnersTaxCodeRoute,
    YourChildcareVouchersId -> (_ => routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)),
    PartnerChildcareVouchersId -> (_ => routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)),
    EitherGetsVouchersId -> maxHoursNav.eitherGetVouchersRoute,
    WhoGetsVouchersId -> (_ => routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)),
    DoYouGetAnyBenefitsId -> maxHoursNav.doYouGetAnyBenefitsRoute,
    DoYouOrYourPartnerGetAnyBenefitsId -> maxHoursNav.doYouOrYourPartnerGetAnyBenefitsRoute,
    WhoGetsBenefitsId -> maxHoursNav.whoGetsBenefitsRoute,
    WhichBenefitsYouGetId -> maxHoursNav.whichBenefitsYouGetRoute,
    WhichBenefitsPartnerGetId -> maxHoursNav.whichBenefitsPartnerGetRoute,
    YourAgeId -> maxHoursNav.yourAgeRoute,
    YourPartnersAgeId -> maxHoursNav.yourPartnerAgeRoute,
    YourMinimumEarningsId -> maxHoursNav.yourMinimumEarningsRoute,
    PartnerMinimumEarningsId -> maxHoursNav.partnerMinimumEarningsRoute,
    AreYouSelfEmployedOrApprenticeId -> maxHoursNav.areYouSelfEmployedOrApprenticeRoute,
    PartnerSelfEmployedOrApprenticeId -> maxHoursNav.partnerSelfEmployedOrApprenticeRoute,
    YourSelfEmployedId -> maxHoursNav.yourSelfEmployedRoute,
    PartnerSelfEmployedId -> maxHoursNav.partnerSelfEmployedRoute,
    YourMaximumEarningsId -> maxHoursNav.yourMaximumEarningsRoute,
    PartnerMaximumEarningsId -> (_ => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)),
    EitherOfYouMaximumEarningsId -> (_ => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)),
    PartnerPaidWorkCYId -> (_ =>  employmentIncomeNav.partnerPaidWorkCYRoute),
    ParentPaidWorkCYId -> (_ => employmentIncomeNav.parentPaidWorkCYRoute),
    ParentEmploymentIncomeCYId -> (_ => employmentIncomeNav.parentEmploymentIncomeCYRoute),
    PartnerEmploymentIncomeCYId -> (_ =>employmentIncomeNav.partnerEmploymentIncomeCYRoute),
    EmploymentIncomeCYId -> (_ => employmentIncomeNav.employmentIncomeCYRoute),
    YouPaidPensionCYId -> pensionNav.yourPensionRouteCY,
    PartnerPaidPensionCYId -> pensionNav.partnerPensionRouteCY,
    BothPaidPensionCYId -> pensionNav.bothPensionRouteCY,
    WhoPaysIntoPensionId -> pensionNav.whoPaysPensionRouteCY,
    HowMuchYouPayPensionId -> pensionNav.howMuchYouPayPensionRouteCY,
    HowMuchPartnerPayPensionId -> pensionNav.howMuchPartnerPayPensionRouteCY,
    HowMuchBothPayPensionId -> pensionNav.howMuchBothPayPensionRouteCY,
    YourStatutoryPayCYId -> StatutoryPayNav.yourStatutoryPayRouteCY
  )

  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map.empty

  def nextPage(id: Identifier, mode: Mode): UserAnswers => Call = {
    answers =>
      mode match {
        case NormalMode =>
          routeMap.getOrElse(id, (_: UserAnswers) => routes.WhatToTellTheCalculatorController.onPageLoad())(answers)
        case CheckMode =>
          editRouteMap.getOrElse(id, (_: UserAnswers) => routes.CheckYourAnswersController.onPageLoad())(answers)
      }
  }
}
