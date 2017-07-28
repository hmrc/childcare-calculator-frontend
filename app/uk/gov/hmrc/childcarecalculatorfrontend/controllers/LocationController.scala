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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.LocationForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html._
import scala.concurrent.Future

@Singleton
class LocationController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetchEntryForSession[String](locationKey).map { loc =>
      Ok(location(new LocationForm(messagesApi).form.fill(loc)))
    }.recover {
      case ex: Exception =>
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    new LocationForm(messagesApi).form.bindFromRequest().fold(
      errors => {
        Future(BadRequest(location(errors)))
      },
      success => {
        val selectedLocation = success.get
        keystore.cacheEntryForSession(locationKey, selectedLocation).flatMap { result =>
          if(selectedLocation == LocationEnum.NORTHERNIRELAND.toString) {
            keystore.removeFromSession(childAgedTwoKey).map {
              case true => Redirect(routes.ChildAgedThreeOrFourController.onPageLoad())
              case false => Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
            }
          }
          else {
            Future(Redirect(routes.ChildAgedTwoController.onPageLoad()))
          }
        } recover {
          case e: Exception =>
            Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
        }
      }
    )
  }

}
