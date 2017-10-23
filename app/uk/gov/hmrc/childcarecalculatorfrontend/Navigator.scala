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
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.navigation._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

@Singleton
class Navigator @Inject() (minHoursNav: MinimumHoursNavigation = new MinimumHoursNavigation(),
                           maxEarningsNav: MaximumHoursNavigation = new MaximumHoursNavigation()) {

  private val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    LocationId -> minHoursNav.locationRoute,
    ChildAgedTwoId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)),
    ChildAgedThreeOrFourId -> (_ => routes.ChildcareCostsController.onPageLoad(NormalMode)),
    ChildcareCostsId -> minHoursNav.costRoute,
    ApprovedProviderId -> minHoursNav.approvedChildCareRoute,
    FreeHoursInfoId -> (_ => routes.DoYouLiveWithPartnerController.onPageLoad(NormalMode)),
    DoYouLiveWithPartnerId -> maxEarningsNav.doYouLiveRoute,
    AreYouInPaidWorkId -> maxEarningsNav.areYouInPaidWorkRoute,
    PaidEmploymentId -> maxEarningsNav.paidEmploymentRoute,
    WhoIsInPaidEmploymentId -> maxEarningsNav.parentWorkHoursRoute,
    ParentWorkHoursId -> (_ => routes.HasYourTaxCodeBeenAdjustedController.onPageLoad(NormalMode)),
    PartnerWorkHoursId -> maxEarningsNav.partnerWorkHoursRoute,
    HasYourTaxCodeBeenAdjustedId -> maxEarningsNav.yourTaxCodeAdjustedRoute,
    DoYouKnowYourAdjustedTaxCodeId -> maxEarningsNav.doYouKnowYourAdjustedTaxCodeRoute,
    WhatIsYourTaxCodeId -> maxEarningsNav.whatIsYourTaxCodeRoute,
    HasYourPartnersTaxCodeBeenAdjustedId -> maxEarningsNav.partnerTaxCodeAdjustedRoute,
    DoYouKnowYourPartnersAdjustedTaxCodeId -> maxEarningsNav.doYouKnowPartnersTaxCodeRoute,
    WhatIsYourPartnersTaxCodeId -> maxEarningsNav.whatIsYourPartnersTaxCodeRoute,
    YourChildcareVouchersId -> (_ => routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)),
    PartnerChildcareVouchersId -> (_ => routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)),
    EitherGetsVouchersId -> maxEarningsNav.eitherGetVouchersRoute,
    WhoGetsVouchersId -> (_ => routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)),
    DoYouGetAnyBenefitsId -> maxEarningsNav.doYouGetAnyBenefitsRoute,
    DoYouOrYourPartnerGetAnyBenefitsId -> maxEarningsNav.doYouOrYourPartnerGetAnyBenefitsRoute,
    WhoGetsBenefitsId -> maxEarningsNav.whoGetsBenefitsRoute,
    WhichBenefitsYouGetId -> maxEarningsNav.whichBenefitsYouGetRoute,
    WhichBenefitsPartnerGetId -> maxEarningsNav.whichBenefitsPartnerGetRoute,
    YourAgeId -> maxEarningsNav.yourAgeRoute,
    YourPartnersAgeId -> maxEarningsNav.yourPartnerAgeRoute,
    YourMinimumEarningsId -> maxEarningsNav.yourMinimumEarningsRoute,
    PartnerMinimumEarningsId -> maxEarningsNav.partnerMinimumEarningsRoute,
    AreYouSelfEmployedOrApprenticeId -> maxEarningsNav.areYouSelfEmployedOrApprenticeRoute,
    PartnerSelfEmployedOrApprenticeId -> maxEarningsNav.partnerSelfEmployedOrApprenticeRoute,
    YourSelfEmployedId -> maxEarningsNav.yourSelfEmployedRoute,
    PartnerSelfEmployedId -> maxEarningsNav.partnerSelfEmployedRoute,
    YourMaximumEarningsId -> maxEarningsNav.yourMaximumEarningsRoute,
    PartnerMaximumEarningsId -> (_ => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)),
    EitherOfYouMaximumEarningsId -> (_ => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode))
  )

  private val editRouteMap: Map[Identifier, UserAnswers => Call] = Map()

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
