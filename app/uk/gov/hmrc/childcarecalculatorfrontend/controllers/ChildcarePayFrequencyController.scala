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

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Result
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildcarePayFrequencyForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ChildcarePayFrequencyId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.models.{ChildcarePayFrequency, Mode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{MapFormats, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childcarePayFrequency

import scala.concurrent.Future

class ChildcarePayFrequencyController @Inject() (
                                                  appConfig: FrontendAppConfig,
                                                  override val messagesApi: MessagesApi,
                                                  dataCacheConnector: DataCacheConnector,
                                                  navigator: Navigator,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction
                                                ) extends FrontendController with I18nSupport with MapFormats {

  def onPageLoad(mode: Mode, childIndex: Int) = (getData andThen requireData).async {
    implicit request =>
      validateIndex(childIndex) {
        name =>
          val preparedForm = request.userAnswers.childcarePayFrequency(childIndex) match {
            case None => ChildcarePayFrequencyForm(name)
            case Some(value) => ChildcarePayFrequencyForm(name).fill(value)
          }
          Future.successful(Ok(childcarePayFrequency(appConfig, preparedForm, childIndex, name, mode)))
      }
  }

  def onSubmit(mode: Mode, childIndex: Int) = (getData andThen requireData).async {
    implicit request =>
      validateIndex(childIndex) {
        name =>
          ChildcarePayFrequencyForm(name).bindFromRequest().fold(
            (formWithErrors: Form[ChildcarePayFrequency.Value]) =>
              Future.successful(BadRequest(childcarePayFrequency(appConfig, formWithErrors, childIndex, name, mode))),
            (value) =>
              dataCacheConnector.saveInMap[Int, ChildcarePayFrequency.Value](
                request.sessionId,
                ChildcarePayFrequencyId.toString,
                childIndex,
                value
              ).map { cacheMap =>
                Redirect(navigator.nextPage(ChildcarePayFrequencyId(childIndex), mode)(new UserAnswers(cacheMap)))
              }
          )
      }
  }

  private def validateIndex[A](i: Int)(block: String => Future[Result])
                           (implicit request: DataRequest[A]): Future[Result] = {

    for {
      model             <- request.userAnswers.aboutYourChild(i)
      childrenWithCosts <- request.userAnswers.childrenWithCosts
    } yield {
      if (childrenWithCosts.contains(i)) {
        block(model.name)
      } else {
        Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))
      }
    }
  }.getOrElse(Future.successful(Redirect(routes.SessionExpiredController.onPageLoad())))
}
