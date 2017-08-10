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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildAgedTwoForm

import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, LocationEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childAgedTwo
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class ChildAgedTwoController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def getBackUrl(summary: Boolean)(implicit hc: HeaderCarrier): Call = {
    if(summary) {
      routes.FreeHoursResultsController.onPageLoad()
    } else {
      routes.LocationController.onPageLoad()
    }
  }

  def onPageLoad(summary: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) =>
        Ok(
          childAgedTwo(
            new ChildAgedTwoForm(messagesApi).form.fill(pageObjects.childAgedTwo),
            getBackUrl(summary),
            pageObjects.household.location
          )
        )
      case _ =>
        Logger.warn("PageObjects object is missing in ChildAgedTwoController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from ChildAgedTwoController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) =>
        new ChildAgedTwoForm(messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                childAgedTwo(errors, getBackUrl(false), pageObjects.household.location)

              )
            ),
          success => {
            val modifiedPageObjects = pageObjects.copy(
              childAgedTwo = success
            )

            keystore.cache(modifiedPageObjects).map {
              result =>
                Redirect(routes.ChildAgedThreeOrFourController.onPageLoad(false))
            }
          }
        )
      case _ =>
        Logger.warn("PageObjects object is missing in ChildAgedTwoController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from ChildAgedTwoController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }
}
