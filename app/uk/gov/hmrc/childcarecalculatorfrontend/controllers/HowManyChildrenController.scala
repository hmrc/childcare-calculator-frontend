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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.HowManyChildrenForm
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.HelperManager
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.howManyChildren

import scala.concurrent.Future

@Singleton
class HowManyChildrenController @Inject() (val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) =>
        Ok(
          howManyChildren(
            new HowManyChildrenForm(messagesApi).form.fill(pageObjects.howManyChildren),
            getBackUrl(pageObjects)
          )
        )
      case _ =>
        Logger.warn("Invalid PageObjects in HowManyChildrenController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from HowManyChildrenController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) =>
        new HowManyChildrenForm(messagesApi).form.bindFromRequest().fold(
          errors => {
            Future(
              BadRequest(
                howManyChildren(
                  errors,
                  getBackUrl(pageObjects)
                )
              )
            )
          },
          success => {
            val modifiedPageObject = modifyPageObjects(pageObjects, success)
            keystore.cache(modifiedPageObject).map { res =>
              Redirect(routes.ChildCareBaseController.underConstruction())
            }
          }
        )
      case _ =>
        Logger.warn("Invalid PageObjects in HowManyChildrenController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from HowManyChildrenController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def modifyPageObjects(pageObjects: PageObjects, newHowManyChildren: Option[Int]): PageObjects = {
    pageObjects.copy(
      howManyChildren = newHowManyChildren
    )
  }


  private def getBackUrl(pageObjects: PageObjects): Call = {
    val hasChild3Or4 = pageObjects.household.childAgedThreeOrFour.getOrElse(false)

    if(HelperManager.checkMaxHoursEligibility(pageObjects) && hasChild3Or4) {
      routes.MaxFreeHoursInfoController.onPageLoad()
    } else {
      routes.CreditsController.onPageLoad()
    }
  }
}


