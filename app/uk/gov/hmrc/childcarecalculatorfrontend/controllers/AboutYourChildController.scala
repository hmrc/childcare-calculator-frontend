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
import play.api.mvc.*
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.AboutYourChildForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.AboutYourChildId
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.services.DataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{MapFormats, SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.aboutYourChild
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AboutYourChildController @Inject() (
    mcc: MessagesControllerComponents,
    dataCacheService: DataCacheService,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    aboutYourChild: aboutYourChild
)(using ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport
    with MapFormats {

  private def sessionExpired(message: String, answers: Option[UserAnswers])(
      using request: RequestHeader
  ): Future[Result] =
    Future.successful(Redirect(SessionExpiredRouter.route(getClass.getName, message, answers, request.uri)))

  private def validateIndex[A](
      childIndex: Int
  )(block: Int => Future[Result])(using request: DataRequest[A]): Future[Result] =
    request.userAnswers.noOfChildren
      .map { noOfChildren =>
        if (childIndex >= 0 && childIndex < noOfChildren) {
          block(noOfChildren)
        } else {
          sessionExpired("validateIndex", Some(request.userAnswers))
        }
      }
      .getOrElse(sessionExpired("validateIndex", None))

  def onPageLoad(childIndex: Int): Action[AnyContent] =
    getData.andThen(requireData).async { request =>
      given DataRequest[AnyContent] = request
      validateIndex(childIndex) { noOfChildren =>
        val preparedForm = request.userAnswers.aboutYourChild(childIndex) match {
          case None => AboutYourChildForm(childIndex, noOfChildren)
          case Some(value) =>
            AboutYourChildForm(childIndex, noOfChildren, request.userAnswers.aboutYourChild).fill(value)
        }
        Future.successful(Ok(aboutYourChild(preparedForm, childIndex, noOfChildren)))
      }
    }

  def onSubmit(childIndex: Int): Action[AnyContent] = getData.andThen(requireData).async { request =>
    given DataRequest[AnyContent] = request
    validateIndex(childIndex) { noOfChildren =>
      AboutYourChildForm(childIndex, noOfChildren, request.userAnswers.aboutYourChild)
        .bindFromRequest()
        .fold(
          (formWithErrors: Form[AboutYourChild]) =>
            Future.successful(BadRequest(aboutYourChild(formWithErrors, childIndex, noOfChildren))),
          value =>
            dataCacheService
              .saveInMap[Int, AboutYourChild](
                AboutYourChildId,
                childIndex,
                value
              )
              .map(cacheMap => Redirect(navigator.nextPage(AboutYourChildId(childIndex))(new UserAnswers(cacheMap))))
        )
    }
  }

}
