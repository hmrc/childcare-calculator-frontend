/*
 * Copyright 2019 HM Revenue & Customs
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

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.SurveyDoNotUnderstandForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.SurveyDoNotUnderstandId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.services.{SplunkSubmissionService, SplunkSubmissionServiceInterface, SubmissionFailed, SubmissionSuccessful}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.surveyDoNotUnderstand

import scala.concurrent.Future

class SurveyDoNotUnderstandController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        splunkSubmissionService: SplunkSubmissionServiceInterface) extends FrontendController with I18nSupport {

  def onPageLoad() = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.surveyDoNotUnderstand match {
        case None => SurveyDoNotUnderstandForm()
        case Some(value) => SurveyDoNotUnderstandForm().fill(value)
      }
      Ok(surveyDoNotUnderstand(appConfig, preparedForm))
  }

  def onSubmit() = (getData andThen requireData).async {
    implicit request =>
      SurveyDoNotUnderstandForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(surveyDoNotUnderstand(appConfig, formWithErrors))),
        (value) => {

          val data = Map("reasonForNotUnderstanding" -> value)

          splunkSubmissionService.submit(data).map {
            case SubmissionSuccessful => Logger.info("reasonForNotUnderstanding logged to Splunk")
            case SubmissionFailed => Logger.warn("reasonForNotUnderstanding failed to log to Splunk")
          }

          dataCacheConnector.save[String](request.sessionId, SurveyDoNotUnderstandId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(SurveyDoNotUnderstandId, NormalMode)(new UserAnswers(cacheMap))))
        }

      )
  }
}
