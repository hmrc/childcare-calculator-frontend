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
import play.api.mvc.{Result, Call, Action, AnyContent}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.LocationForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, LocationEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html._
import uk.gov.hmrc.play.http.HeaderCarrier
import scala.concurrent.Future

@Singleton
class LocationController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[Household]().map { household =>
      Ok(location(new LocationForm(messagesApi).form.fill(household.map(_.location.toString))))
    }.recover {
      case ex: Exception =>
        Logger.warn(s"Exception from LocationController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def getModifiedHousehold(household: Option[Household], selectedLocation: String): Household = {
    household match {
      case Some(hh) =>
        val modifiedChildAgedTwo = if(selectedLocation == LocationEnum.NORTHERNIRELAND.toString) {
          None
        }
        else {
          hh.childAgedTwo
        }

        hh.copy(
          location = LocationEnum.withName(selectedLocation),
          childAgedTwo = modifiedChildAgedTwo
        )
      case _ =>
        Household(
          location = LocationEnum.withName(selectedLocation)
        )
    }
  }

  private def saveAndGoToNextPage(household: Option[Household], selectedLocation: String)(implicit hc: HeaderCarrier): Future[Result] = {
    val modifiedHousehold: Household = getModifiedHousehold(household, selectedLocation)
    keystore.cache(modifiedHousehold).map { res =>
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
        keystore.fetch[Household]().flatMap { household =>
          saveAndGoToNextPage(household, selectedLocation)
        } recover {
          case ex: Exception =>
            Logger.warn(s"Exception from LocationController.onSubmit: ${ex.getMessage}")
            Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
        }
      }
    )
  }

}
