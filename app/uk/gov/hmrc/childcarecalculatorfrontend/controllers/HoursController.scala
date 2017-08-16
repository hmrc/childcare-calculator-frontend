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
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.HoursForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.PageObjects
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.hours
import scala.concurrent.Future

@Singleton
class HoursController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) =>
        val hasPartner = pageObjects.livingWithPartner.getOrElse(false)
        Ok(
          hours(new HoursForm(hasPartner, messagesApi).form.fill(pageObjects.hours), hasPartner
          )
        )
      case _ =>
        Logger.warn("PageObjects object is missing in HoursController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from HoursController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) =>
        val hasPartner = pageObjects.livingWithPartner.getOrElse(false)
        new HoursForm(hasPartner, messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                hours(errors, hasPartner) //getBackUrl(false), pageObjects.household.location)
              )
            ),
          success => {
            val modifiedPageObjects = pageObjects.copy(
              hours = success
            )
            keystore.cache(modifiedPageObjects).map { result =>
              if (hasPartner) {
                Redirect(routes.HoursController.onPageLoad())
              } else {
                //TODO - redirect to vouchers page
                Redirect(routes.HoursController.onPageLoad())
              }

            } recover {
              case ex: Exception =>
                Logger.warn(s"Exception from HoursController.onSubmit: ${ex.getMessage}")
                Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
            }
          }
        )
      case _ =>
        Logger.warn("PageObjects object is missing in HoursController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from HoursController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

}
