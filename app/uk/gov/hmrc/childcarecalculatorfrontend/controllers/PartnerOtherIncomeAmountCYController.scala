/*
 * Copyright 2018 HM Revenue & Customs
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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.JsBoolean
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.PartnerOtherIncomeAmountCYForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{BothPaidWorkPYId, PartnerOtherIncomeAmountCYId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMapCloner, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerOtherIncomeAmountCY

import scala.concurrent.Future

class PartnerOtherIncomeAmountCYController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        form: PartnerOtherIncomeAmountCYForm) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.partnerOtherIncomeAmountCY match {
        case None => form()
        case Some(value) => form().fill(value)
      }
      Ok(partnerOtherIncomeAmountCY(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      form().bindFromRequest().fold(
        (formWithErrors: Form[BigDecimal]) =>
          Future.successful(BadRequest(partnerOtherIncomeAmountCY(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[BigDecimal](request.sessionId, PartnerOtherIncomeAmountCYId.toString, value).map(cacheMap => {
            val anyoneInPaidEmployment : Boolean = request.userAnswers.whoIsInPaidEmployment.fold(false)(c=>c != "Neither")
            dataCacheConnector.updateMap(CacheMapCloner.cloneSection(cacheMap,CacheMapCloner.bothIncomeCurrentYearToPreviousYear,Some(Map(BothPaidWorkPYId.toString->JsBoolean(anyoneInPaidEmployment)))))
            Redirect(navigator.nextPage(PartnerOtherIncomeAmountCYId, mode)(new UserAnswers(cacheMap)))})
      )
  }
}
