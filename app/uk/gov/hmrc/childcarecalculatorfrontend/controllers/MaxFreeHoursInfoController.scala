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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.{Inject, Singleton}

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxFreeChildcare
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, YesNoNotYetEnum, YesNoUnsureEnum, YouPartnerBothNeitherEnum, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maxFreeHoursInfo
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

@Singleton
class MaxFreeHoursInfoController @Inject()(val appConfig: FrontendAppConfig,
                                           val messagesApi: MessagesApi,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                          taxFreeChildcare: TaxFreeChildcare) extends FrontendController with I18nSupport {

    def onPageLoad: Action[AnyContent] = (getData andThen requireData) {
    implicit request =>

      val Yes = YesNoNotYetEnum.YES.toString
      val NotSure = YesNoUnsureEnum.NOTSURE.toString
      val YesSure = YesNoUnsureEnum.YES.toString

      val hasChildcareCosts = request.userAnswers.childcareCosts.getOrElse(false) match {
        case Yes => true
        case _=> false
      }

      val youPaidEmployment = request.userAnswers.areYouInPaidWork.getOrElse(false)

      val hasPartner = request.userAnswers.doYouLiveWithPartner.getOrElse(false)

      val partnerChildcareVouchers = request.userAnswers.partnerChildcareVouchers.getOrElse(false) match {
        case YesSure => true
        case NotSure => true
        case _ => false
      }

      val whoInPaidEmployment = request.userAnswers.whoIsInPaidEmployment

     val bothChildcareVouchers: String = request.userAnswers.whoGetsVouchers.getOrElse("")

      val parentHowManyHours: BigDecimal = request.userAnswers.parentWorkHours.getOrElse(0)

      val partnerHowManyHours: BigDecimal = request.userAnswers.partnerWorkHours.getOrElse(0)

      val totalHouseholdWorkHours = parentHowManyHours + partnerHowManyHours

      val yourChildcareVouchers = request.userAnswers.yourChildcareVouchers.getOrElse(false) match {
        case YesSure => true
        case NotSure => true
        case _ => false
      }

      val benefits = request.userAnswers.doYouGetAnyBenefits.getOrElse(false)

      val householdBenefits = request.userAnswers.doYouOrYourPartnerGetAnyBenefits.getOrElse(false)


      val childcareVouchersEligibility =
        if (hasPartner) {
          whoInPaidEmployment match {
            case  Some(You) => hasChildcareCosts && yourChildcareVouchers
            case Some(Partner) => hasChildcareCosts && partnerChildcareVouchers
            case Some(_) => hasChildcareCosts && (bothChildcareVouchers == Both)
            case _ => false
          }
         }else{
          hasChildcareCosts && yourChildcareVouchers
        }

      val taxCreditsEligibility =
        if (hasPartner){
          (parentHowManyHours >=16 && partnerHowManyHours >=16) && hasChildcareCosts ||
          ((parentHowManyHours <=16 || partnerHowManyHours >=16) && totalHouseholdWorkHours >= 24) ||
          ((parentHowManyHours >=16 || partnerHowManyHours >=16) &&  householdBenefits)
        }else {
         youPaidEmployment && hasChildcareCosts && (parentHowManyHours >= 16)
        }

    Ok(maxFreeHoursInfo(appConfig, taxFreeChildcare.eligibility(request.userAnswers), childcareVouchersEligibility, taxCreditsEligibility))
  }
}
