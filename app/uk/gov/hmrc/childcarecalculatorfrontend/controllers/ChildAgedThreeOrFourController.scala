/*
 * Copyright 2017 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildAgedThreeOrFourForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.Household
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childAgedThreeOrFour
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class ChildAgedThreeOrFourController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def getBackUrl(summary: Boolean, hasChildAgedTwo: Option[Boolean])(implicit hc: HeaderCarrier): Call = {
    if(summary) {
      routes.FreeHoursResultsController.onPageLoad()
    } else {
      if(hasChildAgedTwo.isDefined) {
        routes.ChildAgedTwoController.onPageLoad(false)
      } else {
        routes.LocationController.onPageLoad()
      }
    }
  }

  def onPageLoad(summary: Boolean = false): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[Household]().map {
      case Some(household) =>
        Ok(
          childAgedThreeOrFour(
            new ChildAgedThreeOrFourForm(messagesApi).form.fill(household.childAgedThreeOrFour),
            getBackUrl(summary, household.childAgedTwo),
            household.location
          )
        )
      case _ =>
        Logger.warn("Household object is missing in ChildAgedThreeOrFourController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from ChildAgedThreeOrFourController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[Household]().flatMap {
      case Some(household) =>
        new ChildAgedThreeOrFourForm(messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                childAgedThreeOrFour(
                  errors,
                  getBackUrl(false, household.childAgedTwo),
                  household.location
                )
              )
            ),
          success => {
            val modifiedHousehold = household.copy(
              childAgedThreeOrFour = success
            )
            keystore.cache(modifiedHousehold).map { result =>
              Redirect(routes.ExpectChildcareCostsController.onPageLoad(false))
            } recover {
              case ex: Exception =>
                Logger.warn(s"Exception from ChildAgedThreeOrFourController.onSubmit: ${ex.getMessage}")
                Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
            }
          }
        )
      case _ =>
        Logger.warn("Household object is missing in ChildAgedThreeOrFourController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from ChildAgedThreeOrFourController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }
}
