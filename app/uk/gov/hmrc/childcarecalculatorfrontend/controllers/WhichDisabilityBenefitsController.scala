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

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{RequestHeader, Result}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichDisabilityBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhichDisabilityBenefitsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.models.{DisabilityBenefits, Mode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{MapFormats, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichDisabilityBenefits
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

class WhichDisabilityBenefitsController @Inject() (
                                                    appConfig: FrontendAppConfig,
                                                    override val messagesApi: MessagesApi,
                                                    dataCacheConnector: DataCacheConnector,
                                                    navigator: Navigator,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction
                                                 ) extends FrontendController with I18nSupport with MapFormats {

  private def sessionExpired(implicit request: RequestHeader): Future[Result] =
    Future.successful(Redirect(routes.SessionExpiredController.onPageLoad()))

  private def withValidIndex[A](index: Int)
                               (block: String => Future[Result])
                               (implicit request: DataRequest[A]): Future[Result] = {
    for {
      children <- request.userAnswers.whichChildrenDisability
      name     <- request.userAnswers.aboutYourChild(index).map(_.name)
    } yield if (children.contains(index.toString)) {
        block(name)
      } else {
        sessionExpired
      }
    }.getOrElse(sessionExpired)

  def onPageLoad(mode: Mode, childIndex: Int) = (getData andThen requireData).async {
    implicit request =>
      withValidIndex(childIndex) {
        name =>
          val answer = request.userAnswers.whichDisabilityBenefits(childIndex)
          val preparedForm = answer match {
            case None => WhichDisabilityBenefitsForm()
            case Some(value) => WhichDisabilityBenefitsForm().fill(value)
          }
          Future.successful(Ok(whichDisabilityBenefits(appConfig, preparedForm, childIndex, name, mode)))
        }
  }

  def onSubmit(mode: Mode, childIndex: Int) = (getData andThen requireData).async {
    implicit request =>
      withValidIndex(childIndex) {
        name =>
          WhichDisabilityBenefitsForm().bindFromRequest().fold(
            formWithErrors => {
              Future.successful(BadRequest(whichDisabilityBenefits(appConfig, formWithErrors, childIndex, name, mode)))
            },
            value => {
              dataCacheConnector.saveInMap[Int, Set[DisabilityBenefits.Value]](
                request.sessionId,
                WhichDisabilityBenefitsId.toString,
                childIndex,
                value
              ).map {
                cacheMap =>
                  Redirect(navigator.nextPage(WhichDisabilityBenefitsId, mode)(new UserAnswers(cacheMap)))
              }
            }
          )
      }
  }
}
