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
import play.api.mvc._
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ChildrenDisabilityBenefitsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.{childDisabilityBenefits, childrenDisabilityBenefits}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ChildrenDisabilityBenefitsController @Inject() (
    appConfig: FrontendAppConfig,
    mcc: MessagesControllerComponents,
    dataCacheConnector: DataCacheConnector,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    childDisabilityBenefits: childDisabilityBenefits,
    childrenDisabilityBenefits: childrenDisabilityBenefits
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = getData.andThen(requireData).async { implicit request =>
    withData { case (noOfChildren, name) =>
      val preparedForm = request.userAnswers.childrenDisabilityBenefits match {
        case None        => BooleanForm()
        case Some(value) => BooleanForm().fill(value)
      }
      Future.successful(Ok(view(appConfig, preparedForm, name, noOfChildren)))
    }
  }

  def onSubmit(): Action[AnyContent] = getData.andThen(requireData).async { implicit request =>
    withData { case (noOfChildren, name) =>
      BooleanForm("childrenDisabilityBenefits.error.notCompleted")
        .bindFromRequest()
        .fold(
          (formWithErrors: Form[Boolean]) =>
            Future.successful(BadRequest(view(appConfig, formWithErrors, name, noOfChildren))),
          value =>
            dataCacheConnector
              .save[Boolean](request.sessionId, ChildrenDisabilityBenefitsId.toString, value)
              .map(cacheMap => Redirect(navigator.nextPage(ChildrenDisabilityBenefitsId)(new UserAnswers(cacheMap))))
        )
    }
  }

  private def withData[A](block: (Int, String) => Future[Result])(implicit request: DataRequest[A]): Future[Result] = {
    for {
      noOfChildren <- request.userAnswers.noOfChildren
      name         <- request.userAnswers.aboutYourChild(0).map(_.name)
    } yield block(noOfChildren, name)
  }.getOrElse(
    Future.successful(
      Redirect(SessionExpiredRouter.route(getClass.getName, "withData", Some(request.userAnswers), request.uri))
    )
  )

  private def view(appConfig: FrontendAppConfig, form: Form[Boolean], name: String, noOfChildren: Int)(
      implicit request: Request[_]
  ): Html =
    if (noOfChildren == 1) {
      childDisabilityBenefits(appConfig, form, name)
    } else {
      childrenDisabilityBenefits(appConfig, form)
    }

}
