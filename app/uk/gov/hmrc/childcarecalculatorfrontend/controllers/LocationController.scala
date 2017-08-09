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
import play.api.mvc.{Action, AnyContent, Call, Result}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.LocationForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, LocationEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html._
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class LocationController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map { pageObjects =>
      Ok(location(new LocationForm(messagesApi).form.fill(pageObjects.map(_.household.location.toString))))
    }.recover {
      case ex: Exception =>
        Logger.warn(s"Exception from LocationController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def getModifiedHousehold(pageObjects: Option[PageObjects], selectedLocation: String): PageObjects = {
    pageObjects match {
      case Some(po) =>
        val modifiedChildAgedTwo = if(selectedLocation == LocationEnum.NORTHERNIRELAND.toString) {
          None
        }
        else {
          po.childAgedTwo
        }

        po.copy(
          household = po.household.copy(
            location = LocationEnum.withName(selectedLocation)
          ),
          childAgedTwo = modifiedChildAgedTwo
        )
      case _ =>
        PageObjects(
          household = Household(
            location = LocationEnum.withName(selectedLocation)
          )
        )
    }
  }

  private def saveAndGoToNextPage(pageObjects: Option[PageObjects], selectedLocation: String)(implicit hc: HeaderCarrier): Future[Result] = {
    val modifiedPageObjects: PageObjects = getModifiedHousehold(pageObjects, selectedLocation)

    keystore.cache(modifiedPageObjects).map { res =>
      if (selectedLocation == LocationEnum.NORTHERNIRELAND.toString) {
        Redirect(routes.ChildAgedThreeOrFourController.onPageLoad())
      }
      else {
        Redirect(routes.ChildAgedTwoController.onPageLoad())
      }
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    new LocationForm(messagesApi).form.bindFromRequest().fold(
      errors => {
        Future(BadRequest(location(errors)))
      },
      success => {
        val selectedLocation = success.get
        keystore.fetch[PageObjects]().flatMap { pageObjects =>
          saveAndGoToNextPage(pageObjects, selectedLocation)
        } recover {
          case ex: Exception =>
            Logger.warn(s"Exception from LocationController.onSubmit: ${ex.getMessage}")
            Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
        }
      }
    )
  }

}
