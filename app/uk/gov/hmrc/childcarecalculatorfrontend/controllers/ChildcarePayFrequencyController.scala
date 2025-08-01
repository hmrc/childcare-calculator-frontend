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

import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildcarePayFrequencyForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ChildcarePayFrequencyId
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildcarePayFrequency
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{MapFormats, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childcarePayFrequency
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ChildcarePayFrequencyController @Inject() (
    appConfig: FrontendAppConfig,
    mcc: MessagesControllerComponents,
    dataCacheConnector: DataCacheConnector,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    childcarePayFrequency: childcarePayFrequency
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport
    with MapFormats {

  def onPageLoad(childIndex: Int): Action[AnyContent] =
    getData.andThen(requireData).async { implicit request =>
      request.userAnswers.aboutYourChild(childIndex) match {
        case Some(child) =>
          val preparedForm = request.userAnswers.childcarePayFrequency(childIndex) match {
            case None        => ChildcarePayFrequencyForm(child.name)
            case Some(value) => ChildcarePayFrequencyForm(child.name).fill(value)
          }
          Future.successful(Ok(childcarePayFrequency(appConfig, preparedForm, childIndex, child.name)))
        case _ => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad))
      }
    }

  def onSubmit(childIndex: Int): Action[AnyContent] =
    getData.andThen(requireData).async { implicit request =>
      request.userAnswers.aboutYourChild(childIndex) match {
        case Some(child) =>
          ChildcarePayFrequencyForm(child.name)
            .bindFromRequest()
            .fold(
              (formWithErrors: Form[ChildcarePayFrequency.Value]) =>
                Future.successful(
                  BadRequest(childcarePayFrequency(appConfig, formWithErrors, childIndex, child.name))
                ),
              value =>
                dataCacheConnector
                  .saveInMap[Int, ChildcarePayFrequency.Value](
                    request.sessionId,
                    ChildcarePayFrequencyId.toString,
                    childIndex,
                    value
                  )
                  .map { cacheMap =>
                    Redirect(navigator.nextPage(ChildcarePayFrequencyId(childIndex))(new UserAnswers(cacheMap)))
                  }
            )
        case _ => Future.successful(Redirect(routes.SessionExpiredController.onPageLoad))
      }
    }

}
