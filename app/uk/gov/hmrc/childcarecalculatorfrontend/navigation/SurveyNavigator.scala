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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import javax.inject.Inject

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, SubNavigator}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{SurveyChildcareSupportId, SurveyDoNotUnderstandId}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

class SurveyNavigator @Inject()(utils: Utils,appConfig: FrontendAppConfig) extends SubNavigator {
  override protected def routeMap = Map (SurveyChildcareSupportId -> doNotUnderstandRoute, SurveyDoNotUnderstandId -> reasonsForNotUnderstanding)

  private def doNotUnderstandRoute(answers: UserAnswers) = {
    utils.getCall(answers.surveyChildcareSupport) {
      case false => routes.SurveyDoNotUnderstandController.onPageLoad()
      case true => Call("",appConfig.surveyThankYouUrl)
    }
  }

  private def reasonsForNotUnderstanding(answers: UserAnswers) = Call("",appConfig.surveyThankYouUrl)
}
