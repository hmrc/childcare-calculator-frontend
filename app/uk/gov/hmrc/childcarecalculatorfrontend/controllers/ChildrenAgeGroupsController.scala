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

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildrenAgeGroupsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.ChildrenAgeGroupsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.{ChildAgeGroup, Mode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childrenAgeGroups
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class ChildrenAgeGroupsController @Inject()(mcc: MessagesControllerComponents,
                                            dataCacheConnector: DataCacheConnector,
                                            navigator: Navigator,
                                            getData: DataRetrievalAction,
                                            requireData: DataRequiredAction,
                                            childAgeGroup: childrenAgeGroups
                                           )(implicit ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.childrenAgeGroups match {
        case None => ChildrenAgeGroupsForm()
        case Some(value) => ChildrenAgeGroupsForm().fill(value)
      }
      Ok(childAgeGroup(preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      ChildrenAgeGroupsForm().bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(childAgeGroup(formWithErrors, mode))),
        value =>
          dataCacheConnector.save[Set[ChildAgeGroup]](request.sessionId, ChildrenAgeGroupsId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(ChildrenAgeGroupsId, mode)(new UserAnswers(cacheMap))))
      )

  }
}
