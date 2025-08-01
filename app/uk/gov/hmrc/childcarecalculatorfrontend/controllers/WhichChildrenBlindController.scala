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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichChildrenBlindForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhichChildrenBlindId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichChildrenBlind
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class WhichChildrenBlindController @Inject() (
    appConfig: FrontendAppConfig,
    mcc: MessagesControllerComponents,
    dataCacheConnector: DataCacheConnector,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    whichChildrenBlind: whichChildrenBlind
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] =
    getData.andThen(requireData).async { implicit request: DataRequest[_] =>
      withValues { values =>
        val answer = request.userAnswers.whichChildrenBlind
        val preparedForm = answer match {
          case None        => WhichChildrenBlindForm()
          case Some(value) => WhichChildrenBlindForm().fill(value)
        }
        Future.successful(Ok(whichChildrenBlind(appConfig, preparedForm, options(values).toSeq)))
      }
    }

  def onSubmit(): Action[AnyContent] =
    getData.andThen(requireData).async { implicit request: DataRequest[_] =>
      withValues { values =>
        WhichChildrenBlindForm(values.values.toSeq: _*)
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[_]) =>
              Future.successful(BadRequest(whichChildrenBlind(appConfig, formWithErrors, options(values).toSeq))),
            value =>
              dataCacheConnector.save[Set[Int]](request.sessionId, WhichChildrenBlindId.toString, value).map { cacheMap =>
                Redirect(navigator.nextPage(WhichChildrenBlindId)(new UserAnswers(cacheMap)))
              }
          )
      }
    }

  private def options(values: Map[String, Int]): Map[String, String] =
    values.map { case (k, v) =>
      (k, v.toString)
    }

  private def withValues[A](
      block: Map[String, Int] => Future[Result]
  )(implicit request: DataRequest[A]): Future[Result] =
    request.userAnswers.aboutYourChild
      .map { aboutYourChild =>
        val values: Map[String, Int] = aboutYourChild.map { case (i, model) =>
          model.name -> i
        }
        block(values)
      }
      .getOrElse(
        Future.successful(
          Redirect(SessionExpiredRouter.route(getClass.getName, "withValues", Some(request.userAnswers), request.uri))
        )
      )

}
