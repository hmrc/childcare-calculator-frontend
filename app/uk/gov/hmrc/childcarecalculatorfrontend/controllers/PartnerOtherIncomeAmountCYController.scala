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
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.PartnerOtherIncomeAmountCYForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.PartnerOtherIncomeAmountCYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.childcarecalculatorfrontend.navigation.Navigator
import uk.gov.hmrc.childcarecalculatorfrontend.services.DataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerOtherIncomeAmountCY
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PartnerOtherIncomeAmountCYController @Inject() (
    mcc: MessagesControllerComponents,
    dataCacheService: DataCacheService,
    navigator: Navigator,
    getData: DataRetrievalAction,
    requireData: DataRequiredAction,
    form: PartnerOtherIncomeAmountCYForm,
    partnerOtherIncomeAmountCY: partnerOtherIncomeAmountCY
)(using ec: ExecutionContext)
    extends FrontendController(mcc)
    with I18nSupport {

  def onPageLoad(): Action[AnyContent] = getData.andThen(requireData) { request =>
    given DataRequest[AnyContent] = request
    val preparedForm = request.userAnswers.partnerOtherIncomeAmountCY match {
      case None        => form()
      case Some(value) => form().fill(value)
    }
    Ok(partnerOtherIncomeAmountCY(preparedForm))
  }

  def onSubmit(): Action[AnyContent] = getData.andThen(requireData).async { request =>
    given DataRequest[AnyContent] = request
    form()
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[BigDecimal]) => Future.successful(BadRequest(partnerOtherIncomeAmountCY(formWithErrors))),
        value =>
          dataCacheService
            .save(PartnerOtherIncomeAmountCYId, value)
            .map(cacheMap => Redirect(navigator.nextPage(PartnerOtherIncomeAmountCYId)(new UserAnswers(cacheMap))))
      )
  }

}
