/*
 * Copyright 2021 HM Revenue & Customs
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

import com.google.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CheckYourAnswersHelper
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.AnswerSection
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.check_your_answers
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

class CheckYourAnswersController @Inject()(appConfig: FrontendAppConfig,
                                           mcc: MessagesControllerComponents,
                                           getData: DataRetrievalAction,
                                           requireData: DataRequiredAction,
                                           check_your_answers: check_your_answers) extends FrontendController(mcc)with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val checkYourAnswersHelper = new CheckYourAnswersHelper(request.userAnswers)
      val sections = Seq(AnswerSection(None, Seq(
        checkYourAnswersHelper.location,
        checkYourAnswersHelper.childAgedTwo,
        checkYourAnswersHelper.childAgedThreeOrFour,
        checkYourAnswersHelper.childcareCosts
      ).flatten))
      Ok(check_your_answers(appConfig, sections))
  }
}
