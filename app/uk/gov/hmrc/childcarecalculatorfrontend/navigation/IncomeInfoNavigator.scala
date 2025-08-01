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

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{Identifier, PartnerIncomeInfoId}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, UserAnswers, Utils}

import javax.inject.{Inject, Singleton}

/** Contains the navigation for current and previous year employment income pages
  */
@Singleton
private[navigation] class IncomeInfoNavigator @Inject() (utils: Utils) extends SubNavigator {

  override protected val routeMap: Map[Identifier, UserAnswers => Call] = Map(
    PartnerIncomeInfoId -> nextPageUrlCY
  )

  private def nextPageUrlCY(userAnswers: UserAnswers) = {
    val hasPartner = userAnswers.doYouLiveWithPartner.getOrElse(false)
    if (hasPartner) {
      utils.getCall(userAnswers.whoIsInPaidEmployment) {
        case `you`     => routes.PartnerPaidWorkCYController.onPageLoad()
        case `partner` => routes.ParentPaidWorkCYController.onPageLoad()
        case `both`    => routes.EmploymentIncomeCYController.onPageLoad()
      }
    } else {
      SessionExpiredRouter.route(getClass.getName, "nextPageUrlCY", Some(userAnswers))
    }
  }

}
