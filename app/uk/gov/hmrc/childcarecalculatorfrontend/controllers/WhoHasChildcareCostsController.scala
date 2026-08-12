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
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoHasChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhoHasChildcareCostsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.services.DataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoHasChildcareCosts
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class WhoHasChildcareCostsController @Inject() (
    mcc: MessagesControllerComponents,
    dataCacheService: DataCacheService,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    whoHasChildcareCosts: whoHasChildcareCosts
)(using ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = getData.andThen(requireData).async { request =>
    given DataRequest[AnyContent] = request
    withValues { values =>
      val answer               = request.userAnswers.whoHasChildcareCosts
      val childrenUnderSixteen = request.userAnswers.childrenBelow16AndExactly16Disabled
      val preparedForm = answer match {
        case None        => WhoHasChildcareCostsForm()
        case Some(value) => WhoHasChildcareCostsForm().fill(value)
      }
      Future.successful(
        Ok(whoHasChildcareCosts(preparedForm, options(values, childrenUnderSixteen).toSeq))
      )
    }
  }

  def onSubmit(): Action[AnyContent] = getData.andThen(requireData).async { request =>
    given DataRequest[AnyContent] = request
    withValues { values =>
      val childrenUnderSixteen = request.userAnswers.childrenBelow16AndExactly16Disabled
      WhoHasChildcareCostsForm(values.values.toSeq*)
        .bindFromRequest()
        .fold(
          (formWithErrors: Form[Set[Int]]) =>
            Future.successful(
              BadRequest(
                whoHasChildcareCosts(formWithErrors, options(values, childrenUnderSixteen).toSeq)
              )
            ),
          value =>
            dataCacheService.save(WhoHasChildcareCostsId, value).map { cacheMap =>
              Redirect(navigator.nextPage(WhoHasChildcareCostsId)(new UserAnswers(cacheMap)))
            }
        )
    }
  }

  private def options(values: Map[String, Int], childrenUnder16: Seq[Int]): Map[String, Int] =
    values.filter(c => childrenUnder16.contains(c._2)).map { case (k, v) => (k, v) }

  private def withValues[A](
      block: Map[String, Int] => Future[Result]
  )(using request: DataRequest[A]): Future[Result] =
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
