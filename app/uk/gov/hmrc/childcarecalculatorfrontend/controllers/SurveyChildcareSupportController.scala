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

import javax.inject.Inject

import play.api.Logger
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.SurveyChildcareSupportId
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.services.{SplunkSubmissionServiceInterface, SubmissionFailed, SubmissionSuccessful}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.surveyChildcareSupport
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

class SurveyChildcareSupportController @Inject()(appConfig: FrontendAppConfig,
                                                 override val messagesApi: MessagesApi,
                                                 dataCacheConnector: DataCacheConnector,
                                                 navigator: Navigator,
                                                 getData: DataRetrievalAction,
                                                 requireData: DataRequiredAction,
                                                 splunkSubmissionService: SplunkSubmissionServiceInterface) extends FrontendController with I18nSupport {

  def onPageLoad = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.surveyChildcareSupport match {
        case None => BooleanForm()
        case Some(value) => BooleanForm().fill(value)
      }
      Ok(surveyChildcareSupport(appConfig, preparedForm))
  }

  def onSubmit = (getData andThen requireData).async {
    implicit request =>
      BooleanForm(surveyChildcareSupportErrorKey).bindFromRequest().fold(
        (formWithErrors: Form[Boolean]) =>
          Future.successful(BadRequest(surveyChildcareSupport(appConfig, formWithErrors))),
        (value) => {

          val data = Map("understandChildcareSupport" -> s"$value")

          splunkSubmissionService.submit(data).map {
            x => x match {
              case SubmissionSuccessful => Logger.info("understandChildcareSupport logged to Splunk")
              case SubmissionFailed => Logger.warn("understandChildcareSupport failed to log to Splunk")
            }
          }

          dataCacheConnector.save[Boolean](request.sessionId, SurveyChildcareSupportId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(SurveyChildcareSupportId, NormalMode)(new UserAnswers(cacheMap))))
        }

      )
  }
}
