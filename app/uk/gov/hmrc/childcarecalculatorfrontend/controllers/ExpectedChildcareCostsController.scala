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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ExpectedChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ExpectedChildcareCostsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.models.{ChildcarePayFrequency, YesNoNotYetEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{MapFormats, SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.expectedChildcareCosts
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ExpectedChildcareCostsController @Inject() (
    appConfig: FrontendAppConfig,
    mcc: MessagesControllerComponents,
    dataCacheConnector: DataCacheConnector,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    expectedChildcareCosts: expectedChildcareCosts
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport
    with MapFormats {

  def onPageLoad(childIndex: Int): Action[AnyContent] =
    getData.andThen(requireData).async { implicit request =>
      validIndex(childIndex) { case (hasCosts, name, frequency) =>
        val preparedForm = request.userAnswers.expectedChildcareCosts(childIndex) match {
          case None        => ExpectedChildcareCostsForm(frequency, name)
          case Some(value) => ExpectedChildcareCostsForm(frequency, name).fill(value)
        }
        Future.successful(
          Ok(expectedChildcareCosts(appConfig, preparedForm, hasCosts, childIndex, frequency, name))
        )
      }
    }

  def onSubmit(childIndex: Int): Action[AnyContent] =
    getData.andThen(requireData).async { implicit request =>
      validIndex(childIndex) { case (hasCosts, name, frequency) =>
        ExpectedChildcareCostsForm(frequency, name)
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[BigDecimal]) =>
              Future.successful(
                BadRequest(
                  expectedChildcareCosts(appConfig, formWithErrors, hasCosts, childIndex, frequency, name)
                )
              ),
            value =>
              dataCacheConnector
                .saveInMap[Int, BigDecimal](
                  request.sessionId,
                  ExpectedChildcareCostsId.toString,
                  childIndex,
                  value
                )
                .map { cacheMap =>
                  Redirect(navigator.nextPage(ExpectedChildcareCostsId(childIndex))(new UserAnswers(cacheMap)))
                }
          )
      }
    }

  private def validIndex[A](childIndex: Int)(
      block: (YesNoNotYetEnum.Value, String, ChildcarePayFrequency.Value) => Future[Result]
  )(implicit request: DataRequest[A]): Future[Result] = {

    for {
      // TODO remove `map` when type is fixed
      hasCosts  <- request.userAnswers.childcareCosts.map(YesNoNotYetEnum.withName)
      model     <- request.userAnswers.aboutYourChild(childIndex)
      frequency <- request.userAnswers.childcarePayFrequency(childIndex)
    } yield block(hasCosts, model.name, frequency)
  }.getOrElse(
    Future.successful(
      Redirect(SessionExpiredRouter.route(getClass.getName, "validIndex", Some(request.userAnswers), request.uri))
    )
  )

}
