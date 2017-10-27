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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerIncomeInfoPY

@Singleton
class PartnerIncomeInfoPYController @Inject()(val appConfig: FrontendAppConfig,
                                      val messagesApi: MessagesApi,
                                      getData: DataRetrievalAction,
                                      requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) { implicit request =>
    Ok(partnerIncomeInfoPY(appConfig,getNextPageUrl(request.userAnswers)))
  }

  private def getNextPageUrl(userAnswers: UserAnswers) = {

    val hasPartner = userAnswers.doYouLiveWithPartner.getOrElse(false)
    val paidEmployment = userAnswers.whoIsInPaidEmployment

    val You = YouPartnerBothEnum.YOU.toString
    val Partner = YouPartnerBothEnum.PARTNER.toString
    val Both = YouPartnerBothEnum.BOTH.toString

    if(hasPartner) {
      paidEmployment match {
        case Some(You) => routes.PartnerPaidWorkPYController.onPageLoad(NormalMode)
        case Some(Partner) => routes.ParentPaidWorkPYController.onPageLoad(NormalMode)
        case Some(Both) => routes.EmploymentIncomePYController.onPageLoad(NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      }
    }else {
      routes.SessionExpiredController.onPageLoad()
    }
  }
}
