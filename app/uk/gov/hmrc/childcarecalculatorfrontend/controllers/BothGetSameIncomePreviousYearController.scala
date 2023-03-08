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
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.BothGetSameIncomePreviousYearId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMapCloner, IncomeSummary, TaxYearInfo, UserAnswers}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.bothGetSameIncomePreviousYear
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

class BothGetSameIncomePreviousYearController @Inject()(appConfig: FrontendAppConfig,
                                                        mcc: MessagesControllerComponents,
                                                        dataCacheConnector: DataCacheConnector,
                                                        navigator: Navigator,
                                                        getData: DataRetrievalAction,
                                                        requireData: DataRequiredAction,
                                                        taxYearInfo: TaxYearInfo,
                                                        incomeSummary: IncomeSummary,
                                                        bothGetSameIncomePreviousYear: bothGetSameIncomePreviousYear)(implicit ec: ExecutionContext)
  extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.bothGetSameIncomePreviousYear match {
        case None => BooleanForm()
        case Some(value) => BooleanForm().fill(value)
      }

      val summary = incomeSummary.load(request.userAnswers)

      Ok(bothGetSameIncomePreviousYear(appConfig, preparedForm, mode, taxYearInfo, Some(summary)))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      BooleanForm("bothGetSameIncomePreviousYear.error.notCompleted").bindFromRequest().fold(
        (formWithErrors: Form[Boolean]) => {
          val summary = incomeSummary.load(request.userAnswers)
          Future.successful(BadRequest(bothGetSameIncomePreviousYear(appConfig, formWithErrors, mode, taxYearInfo, Some(summary))))
        },
        getsSameIncomeAsLastYear => {
          val clonedPreviousYearIncomeData = if (getsSameIncomeAsLastYear) {
            CacheMapCloner.cloneCYIncomeIntoPYIncome(request.userAnswers.cacheMap)
          }
          else {
            CacheMapCloner.removeClonedDataForPreviousYearIncome(request.userAnswers.cacheMap)
          }

          dataCacheConnector.updateMap(clonedPreviousYearIncomeData).flatMap(_ => {
            dataCacheConnector.save[Boolean](request.sessionId, BothGetSameIncomePreviousYearId.toString, getsSameIncomeAsLastYear).map(cacheMap =>
              Redirect(navigator.nextPage(BothGetSameIncomePreviousYearId, mode)(new UserAnswers(cacheMap))))
          })
        })
  }
}
