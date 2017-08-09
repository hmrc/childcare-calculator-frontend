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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ExpectChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, LocationEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.expectChildcareCosts

import scala.concurrent.Future

@Singleton
class ExpectChildcareCostsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) =>
        Ok(
          expectChildcareCosts(
            new ExpectChildcareCostsForm(messagesApi).form.fill(pageObjects.expectChildcareCosts),
            pageObjects.household.location
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

  private def getNextPage(modifiedPageObjects: PageObjects): Call = {
      val location: LocationEnum = modifiedPageObjects.household.location
      val hasChildAgedTwo: Boolean = modifiedPageObjects.childAgedTwo.getOrElse(false)
      val hasChildAgedThreeOrFour: Boolean = modifiedPageObjects.childAgedThreeOrFour.getOrElse(false)
      val hasExpectedChildcareCost: Boolean = modifiedPageObjects.expectChildcareCosts.getOrElse(false)
      if(
        hasChildAgedThreeOrFour &&
        (hasExpectedChildcareCost || location.equals(LocationEnum.ENGLAND) || hasChildAgedTwo)
      ) {
        routes.FreeHoursInfoController.onPageLoad()
      }
      else if(hasChildAgedTwo || hasExpectedChildcareCost) {
        routes.LivingWithPartnerController.onPageLoad()
      }
      else {
        routes.FreeHoursResultsController.onPageLoad()
      }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) =>
        new ExpectChildcareCostsForm(messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                expectChildcareCosts(errors, pageObjects.household.location)
              )
            ),
          success => {
            val modifiedPageObjects = pageObjects.copy(
              expectChildcareCosts = success
            )
            keystore.cache(modifiedPageObjects).map { result =>
              Redirect(getNextPage(modifiedPageObjects))
            } recover {
              case ex: Exception =>
                Logger.warn(s"Exception from ExpectChildcareCostsController.onSubmit: ${ex.getMessage}")
                Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
            }
          }
        )
      case _ =>
        Logger.warn("PageObjects object is missing in ExpectChildcareCostsController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from ExpectChildcareCostsController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }

  }

}
