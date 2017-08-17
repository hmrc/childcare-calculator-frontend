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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichOfYouPaidEmploymentForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YouPartnerBothEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichOfYouPaidOrSelfEmployed

import scala.concurrent.Future

@Singleton
class WhichOfYouInPaidEmploymentController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) =>
        Ok(
          whichOfYouPaidOrSelfEmployed(
            new WhichOfYouPaidEmploymentForm(messagesApi).form.fill(pageObjects.whichOfYouInPaidEmployment)
          )
        )
      case _ =>
        Logger.warn("PageObjects object is missing in WhichOfYouInPaidEmploymentController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }.recover {
      case ex: Exception =>
        Logger.warn(s"Exception from WhichOfYouInPaidEmploymentController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) =>
        new WhichOfYouPaidEmploymentForm(messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                whichOfYouPaidOrSelfEmployed(errors)
              )
            ),
          success => {
            val modifiedPageObjects = pageObjects.copy(
              whichOfYouInPaidEmployment = success
            )

            keystore.cache(modifiedPageObjects).map {
              result =>
                Redirect(
                  routes.HoursController.onPageLoad(
                    isPartner = (YouPartnerBothEnum.withName(success.get) != YouPartnerBothEnum.YOU)
                  )
                )
            }
          }
        )
      case _ =>
        Logger.warn("PageObjects object is missing in WhichOfYouInPaidEmploymentController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from WhichOfYouInPaidEmploymentController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

}
