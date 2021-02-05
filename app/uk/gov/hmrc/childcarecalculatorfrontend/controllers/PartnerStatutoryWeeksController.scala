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

import javax.inject.Inject
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.PartnerStatutoryWeeksForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.PartnerStatutoryWeeksId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, StatutoryPayTypeEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.StatutoryPayWeeksViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerStatutoryWeeks
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PartnerStatutoryWeeksController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        mcc: MessagesControllerComponents,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        partnerStatutoryWeeksForm: PartnerStatutoryWeeksForm) extends FrontendController(mcc) with I18nSupport {

  private def sessionExpired(message: String, answers: Option[UserAnswers])(implicit request: RequestHeader): Future[Result] =
    Future.successful(Redirect(SessionExpiredRouter.route(getClass.getName,message,answers,request.uri)))

  private def validateStatutoryPayType[A](block: StatutoryPayTypeEnum.Value => Future[Result])
                                         (implicit request: DataRequest[A]): Future[Result] = {

    request.userAnswers.partnerStatutoryPayType.map {
      payType => block(payType)
    }.getOrElse(sessionExpired("validateStatutoryPayType",Some(request.userAnswers)))
  }

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      validateStatutoryPayType {
        statutoryType =>

          val viewModel = new StatutoryPayWeeksViewModel(appConfig, statutoryType)

          val preparedForm = request.userAnswers.partnerStatutoryWeeks match {
            case None => partnerStatutoryWeeksForm(statutoryType, viewModel.statutoryTypeMessage)
            case Some(value) => partnerStatutoryWeeksForm(statutoryType, viewModel.statutoryTypeMessage).fill(value)
          }
          Future.successful(Ok(partnerStatutoryWeeks(appConfig, preparedForm, mode, viewModel)))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      validateStatutoryPayType {
        statutoryType =>

          val viewModel = new StatutoryPayWeeksViewModel(appConfig, statutoryType)

          partnerStatutoryWeeksForm(statutoryType, viewModel.statutoryTypeMessage).bindFromRequest().fold(
            (formWithErrors: Form[Int]) =>
              Future.successful(BadRequest(partnerStatutoryWeeks(appConfig, formWithErrors, mode, viewModel))),
            value =>
              dataCacheConnector.save[Int](request.sessionId, PartnerStatutoryWeeksId.toString, value).map(cacheMap =>
                Redirect(navigator.nextPage(PartnerStatutoryWeeksId, mode)(new UserAnswers(cacheMap))))
          )
      }
  }
}
