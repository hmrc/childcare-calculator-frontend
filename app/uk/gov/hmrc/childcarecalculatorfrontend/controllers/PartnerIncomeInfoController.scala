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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.PartnerIncomeInfoId
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerIncomeInfo
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

@Singleton
class PartnerIncomeInfoController @Inject()(val appConfig: FrontendAppConfig,
                                            mcc: MessagesControllerComponents,
                                            getData: DataRetrievalAction,
                                            navigator: Navigator,
                                            requireData: DataRequiredAction,
                                            taxYearInfo: TaxYearInfo,
                                            partnerIncomeInfo: partnerIncomeInfo) extends FrontendController(mcc) with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) { implicit request =>
    Ok(partnerIncomeInfo(appConfig, navigator.nextPage(PartnerIncomeInfoId, NormalMode)(request.userAnswers), taxYearInfo))
  }

}
