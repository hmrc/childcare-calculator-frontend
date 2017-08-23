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
import play.api.mvc.{Call, AnyContent, Action}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichBenefitsDoYouGetForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Benefits, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.benefits

import scala.concurrent.Future

/**
 * Created by user on 23/08/17.
 */
@Singleton
class WhichBenefitsDoYouGetController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def isDataValid(pageObjects: PageObjects, isPartner: Boolean): Boolean = {
    !isPartner || (isPartner && pageObjects.household.partner.isDefined)
  }

  def onPageLoad(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) if isDataValid(pageObjects, isPartner) =>
        val claimantBenefits: Option[Benefits] = if(!isPartner) {
          pageObjects.household.parent.benefits
        }
        else {
          pageObjects.household.partner.get.benefits
        }
        Ok(
          benefits(
            new WhichBenefitsDoYouGetForm(isPartner, messagesApi).form.fill(claimantBenefits.getOrElse(Benefits())),
            isPartner
          )
        )
      case _ =>
        Logger.warn("Invalid PageObjects in WhichBenefitsDoYouGetController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from WhichBenefitsDoYouGetController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }
private def modifyPageObject(pageObjects: PageObjects, selectedBenefits : Benefits, isPartner: Boolean) : PageObjects = {
    if (isPartner) {
      pageObjects.copy(
        household = pageObjects.household.copy(
          partner = Some(
            pageObjects.household.partner.get.copy(
                benefits = Some(selectedBenefits)
            )
          )
        )
      )
    }
    else {
      pageObjects.copy(
        household = pageObjects.household.copy(
          parent = pageObjects.household.parent.copy(
              benefits = Some(selectedBenefits)
          )
        )
      )
    }
}

  private def nextPage(isPartner: Boolean): Call = {
    if (isPartner) {
      // TODO: Age page not yet created
      routes.ChildCareBaseController.underConstruction()
    }
    else {
      routes.WhichBenefitsDoYouGetController.onPageLoad(true)
    }
  }

  def onSubmit(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    new WhichBenefitsDoYouGetForm(isPartner, messagesApi).form.bindFromRequest().fold(
      errors => {
        Future(
          BadRequest(
            benefits(
              errors,
              isPartner
            )
          )
        )
      },
      success => {
        keystore.fetch[PageObjects]().flatMap {
          case Some(pageObject) if isDataValid(pageObject, isPartner) => {
            val modifiedPageObject = modifyPageObject(pageObject, success, isPartner)

            keystore.cache(modifiedPageObject).map { result =>
              Redirect(nextPage(isPartner))
            }
          }
          case _ =>
            Logger.warn("Invalid PageObjects in WhichBenefitsDoYouGetController.onPageLoad")
            Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
        }.recover {
          case ex: Exception =>
            Logger.warn(s"Exception from WhichBenefitsDoYouGetController.onPageLoad: ${ex.getMessage}")
            Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
        }
      }
    )
  }

}
