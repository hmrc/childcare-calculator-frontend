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

import play.api.Logging
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.SurveyChildcareSupportId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.services.{
  DataCacheService,
  SplunkSubmissionServiceInterface,
  SubmissionFailed,
  SubmissionSuccessful
}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.surveyChildcareSupportErrorKey
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.surveyChildcareSupport
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SurveyChildcareSupportController @Inject() (
    mcc: MessagesControllerComponents,
    dataCacheService: DataCacheService,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    splunkSubmissionService: SplunkSubmissionServiceInterface,
    surveyChildcareSupport: surveyChildcareSupport
)(using ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport
    with Logging {

  def onPageLoad: Action[AnyContent] = getData.andThen(requireData) { request =>
    given DataRequest[AnyContent] = request
    val preparedForm = request.userAnswers.surveyChildcareSupport match {
      case None        => BooleanForm()
      case Some(value) => BooleanForm().fill(value)
    }
    Ok(surveyChildcareSupport(preparedForm))
  }

  def onSubmit: Action[AnyContent] = getData.andThen(requireData).async { request =>
    given DataRequest[AnyContent] = request
    BooleanForm(surveyChildcareSupportErrorKey)
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[Boolean]) => Future.successful(BadRequest(surveyChildcareSupport(formWithErrors))),
        value => {

          val data = Map("understandChildcareSupport" -> s"$value")

          splunkSubmissionService.submit(data).map {
            case SubmissionSuccessful => logger.info("understandChildcareSupport logged to Splunk")
            case SubmissionFailed     => logger.warn("understandChildcareSupport failed to log to Splunk")
          }

          dataCacheService
            .save(SurveyChildcareSupportId, value)
            .map(cacheMap => Redirect(navigator.nextPage(SurveyChildcareSupportId)(new UserAnswers(cacheMap))))
        }
      )
  }

}
