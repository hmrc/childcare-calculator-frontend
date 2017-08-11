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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.LivingWithPartnerForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.PageObjects
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.livingWithPartner

import scala.concurrent.Future

@Singleton
class LivingWithPartnerController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def getBackUrl(hasChildAgedThreeOrFour: Option[Boolean]): Call = {
    if (hasChildAgedThreeOrFour.getOrElse(false)) {
      routes.FreeHoursInfoController.onPageLoad()
    } else {
      routes.ExpectChildcareCostsController.onPageLoad(false)
    }
  }

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) =>
        Ok(
          livingWithPartner(new LivingWithPartnerForm(messagesApi).form.fill(pageObjects.livingWithPartner),
            getBackUrl(pageObjects.childAgedThreeOrFour)
          )
        )
      case _ =>
        Logger.warn("PageObjects object is missing in LivingWithPartnerController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from LivingWithPartnerController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) =>
        new LivingWithPartnerForm(messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                livingWithPartner(
                  errors,
                  getBackUrl(pageObjects.childAgedThreeOrFour)
                )
              )
            ),
          success => {
            val modifiedPageObjects = pageObjects.copy(
              livingWithPartner = success
            )
            keystore.cache(modifiedPageObjects).map { result =>
              Redirect(routes.PaidEmploymentController.onPageLoad())
            }
          }
        )
      case _ =>
        Logger.warn("PageObjects object is missing in LivingWithPartnerController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from LivingWithPartnerController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }
}
