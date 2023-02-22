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

import javax.inject.Inject
import play.api.i18n.{I18nSupport, Lang}
import play.api.mvc._
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichDisabilityBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhichDisabilityBenefitsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.models.{DisabilityBenefits, Mode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{MapFormats, SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichDisabilityBenefits
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class WhichDisabilityBenefitsController @Inject() (
                                                    appConfig: FrontendAppConfig,
                                                    mcc: MessagesControllerComponents,
                                                    dataCacheConnector: DataCacheConnector,
                                                    navigator: Navigator,
                                                    getData: DataRetrievalAction,
                                                    requireData: DataRequiredAction,
                                                    whichDisabilityBenefits: whichDisabilityBenefits
                                                 ) extends FrontendController(mcc) with I18nSupport with MapFormats {

  def onPageLoad(mode: Mode, childIndex: Int): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      implicit val lang: Lang = request.lang
      withValidIndex(childIndex) {
        name =>
          val answer = request.userAnswers.whichDisabilityBenefits(childIndex)
          val preparedForm = answer match {
            case None => WhichDisabilityBenefitsForm(name)
            case Some(value) => WhichDisabilityBenefitsForm(name).fill(value)
          }
          Future.successful(Ok(whichDisabilityBenefits(appConfig, preparedForm, childIndex, name, mode)))
        }
  }

  def onSubmit(mode: Mode, childIndex: Int): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      implicit val lang: Lang = request.lang
      withValidIndex(childIndex) {
        name =>
          WhichDisabilityBenefitsForm(name).bindFromRequest().fold(
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
                  Redirect(navigator.nextPage(WhichDisabilityBenefitsId(childIndex), mode)(new UserAnswers(cacheMap)))
              }
            }
          )
      }
  }

  private def sessionExpired(message: String, answers: Option[UserAnswers])(implicit request: RequestHeader): Future[Result] =
    Future.successful(Redirect(SessionExpiredRouter.route(getClass.getName,message,answers,request.uri)))

  private def withValidIndex[A](index: Int)
                               (block: String => Future[Result])
                               (implicit request: DataRequest[A]): Future[Result] = {
    for {
      children <- request.userAnswers.childrenWithDisabilityBenefits
      name     <- request.userAnswers.aboutYourChild(index).map(_.name)
    } yield if (children.contains(index)) {
      block(name)
    } else {
      sessionExpired("withValidIndex",Some(request.userAnswers))
    }
  }.getOrElse(sessionExpired("withValidIndex",None))
}
