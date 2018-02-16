/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.{Inject, Singleton}

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{EmploymentSupportedChildcare, TaxCredits, TaxFreeChildcare}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YesNoNotYetEnum, YesNoUnsureEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maxFreeHoursInfo
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

@Singleton
class MaxFreeHoursInfoController @Inject()(val appConfig: FrontendAppConfig,
                                           val messagesApi: MessagesApi,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           tfc: TaxFreeChildcare,
                                           tc: TaxCredits,
                                           esc: EmploymentSupportedChildcare
                                          ) extends FrontendController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) {

    implicit request =>
      Ok(maxFreeHoursInfo(appConfig,
        tfc.eligibility(request.userAnswers),
        esc.eligibility(request.userAnswers),
        tc.eligibility(request.userAnswers),
        request.userAnswers.taxOrUniversalCredits.getOrElse(""))
      )
  }

 /* private def getESCEligibility(answers: UserAnswers): Boolean = {

    val No = YesNoUnsureEnum.NO.toString

    val hasParentChildcareCosts: Boolean = answers.childcareCosts.fold(false) {
      _ != YesNoNotYetEnum.NO.toString
    }

    val hasPartnerChildcareVouchers = answers.partnerChildcareVouchers.fold(false)(x => !x.equals(No))
    val hasParentChildcareVouchers = answers.yourChildcareVouchers.fold(false)(x => !x.equals(No))

    val hasPartner = answers.doYouLiveWithPartner.getOrElse(false)
    val whoInPaidEmployment = answers.whoIsInPaidEmployment
    val bothChildcareVouchers = answers.whoGetsVouchers

    if (hasPartner) {
      whoInPaidEmployment match {
        case Some(You) => hasParentChildcareCosts && hasParentChildcareVouchers
        case Some(Partner) => hasParentChildcareCosts && hasPartnerChildcareVouchers
        case Some(_) => hasParentChildcareCosts && bothChildcareVouchers.contains(Both)
        case _ => false
      }
    } else {
      hasParentChildcareCosts && hasParentChildcareVouchers
    }
  }
*/
}
