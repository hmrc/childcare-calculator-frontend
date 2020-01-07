/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{EmploymentSupportedChildcare, TaxCredits, TaxFreeChildcare}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maxFreeHoursInfo
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

@Singleton
class MaxFreeHoursInfoController @Inject()(val appConfig: FrontendAppConfig,
                                           mcc: MessagesControllerComponents,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           tfc: TaxFreeChildcare,
                                           tc: TaxCredits,
                                           esc: EmploymentSupportedChildcare
                                          ) extends FrontendController(mcc) with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) {

    implicit request =>
      Ok(maxFreeHoursInfo(appConfig,
        tfc.eligibility(request.userAnswers),
        esc.eligibility(request.userAnswers),
        tc.eligibility(request.userAnswers),
        request.userAnswers)
      )
  }
}
