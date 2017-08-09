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
import play.api.mvc.{Call, Action, AnyContent}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ExpectChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, LocationEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.expectChildcareCosts
import scala.concurrent.Future

@Singleton
class ExpectChildcareCostsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def getBackUrl(summary: Boolean): Call = {
    if(summary) {
      routes.FreeHoursResultsController.onPageLoad()
    } else {
      routes.ChildAgedThreeOrFourController.onPageLoad(false)
    }
  }

  def onPageLoad(summary: Boolean = false): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[Household]().map {
      case Some(household) =>
        Ok(
          expectChildcareCosts(
            new ExpectChildcareCostsForm(messagesApi).form.fill(household.expectChildcareCosts),
            getBackUrl(summary),
            household.location
          )
        )
      case _ =>
        Logger.warn("Household object is missing in ExpectChildcareCostsController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from ExpectChildcareCostsController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def getNextPage(modifiedHousehold: Household): Call = {
      val location: LocationEnum = modifiedHousehold.location
      val hasChildAgedTwo: Boolean = modifiedHousehold.childAgedTwo.getOrElse(false)
      val hasChildAgedThreeOrFour: Boolean = modifiedHousehold.childAgedThreeOrFour.getOrElse(false)
      val hasExpectedChildcareCost: Boolean = modifiedHousehold.expectChildcareCosts.getOrElse(false)
      if(
        hasChildAgedThreeOrFour &&
        (hasExpectedChildcareCost || location.equals(LocationEnum.ENGLAND) || hasChildAgedTwo)
      ) {
        routes.FreeHoursInfoController.onPageLoad()
      } else if(hasChildAgedTwo || hasExpectedChildcareCost) {
        routes.LivingWithPartnerController.onPageLoad()
      } else {
        routes.FreeHoursResultsController.onPageLoad()
      }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[Household]().flatMap {
      case Some(household) =>
        new ExpectChildcareCostsForm(messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                expectChildcareCosts(errors, getBackUrl(false), household.location)
              )
            ),
          success => {
            val modifiedHousehold = household.copy(
              expectChildcareCosts = success
            )
            keystore.cache(modifiedHousehold).map { result =>
              Redirect(getNextPage(modifiedHousehold))
            } recover {
              case ex: Exception =>
                Logger.warn(s"Exception from ExpectChildcareCostsController.onSubmit: ${ex.getMessage}")
                Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
            }
          }
        )
      case _ =>
        Logger.warn("Household object is missing in ExpectChildcareCostsController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from ExpectChildcareCostsController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }

  }

}
