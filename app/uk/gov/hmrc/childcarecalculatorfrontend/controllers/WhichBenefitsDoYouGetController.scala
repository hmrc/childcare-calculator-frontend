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
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, Call}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichBenefitsDoYouGetForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Claimant, Benefits, PageObjects, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.FormManager
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.benefits

import scala.concurrent.Future

@Singleton
class WhichBenefitsDoYouGetController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController with FormManager {

  val keystore: KeystoreService = KeystoreService

  def onPageLoad(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) =>

        val claimantBenefits: Option[Benefits] = if(!isPartner) {
          pageObjects.household.parent.benefits
        } else {
          pageObjects.household.partner.fold[Option[Benefits]](None)(_.benefits)
        }
        Ok(
          benefits(
            new WhichBenefitsDoYouGetForm(isPartner, messagesApi).form.fill(claimantBenefits.getOrElse(Benefits())),
            isPartner,
            backURL(isPartner, pageObjects)
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

  def onSubmit(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObject) => {
        new WhichBenefitsDoYouGetForm(isPartner, messagesApi).form.bindFromRequest().fold(
          errors => {
            val userType = getUserType(isPartner)
            val modifiedErrors = overrideFormErrorKey[Benefits](
                form = errors,
                newMessageKeys=Map(Messages(s"which.benefits.do.you.get.not.selected.${userType}.error") -> "benefits")
            )
            Future(
              BadRequest(
                benefits(
                  modifiedErrors,
                  isPartner,
                  backURL(isPartner, pageObject)
                )
              )
            )
          },
          success => {
            val modifiedPageObject = modifyPageObject(pageObject, success, isPartner)
            keystore.cache(modifiedPageObject).map { result =>
              Redirect(nextPage(isPartner, pageObject))
            }
          }
        )
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


  private def backURL(isPartner: Boolean, pageObjects: PageObjects): Call = {
    if(pageObjects.livingWithPartner.get) {
      if(isPartner && pageObjects.household.parent.benefits.isDefined) {
        routes.WhichBenefitsDoYouGetController.onPageLoad(false)
      } else {
        routes.WhoGetsBenefitsController.onPageLoad()
      }
    } else {
      routes.GetBenefitsController.onPageLoad()
    }
  }

  private def modifyPageObject(pageObjects: PageObjects,
                               selectedBenefits : Benefits,
                               isPartner: Boolean) : PageObjects = {
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
    } else {
      pageObjects.copy(
        household = pageObjects.household.copy(
          parent = pageObjects.household.parent.copy(
            benefits = Some(selectedBenefits)
          )
        )
      )
    }
  }

  private def nextPage(isPartner: Boolean,
                       pageObjects: PageObjects): Call = {
    if (!isPartner && pageObjects.household.partner.isDefined && pageObjects.household.partner.get.benefits.isDefined) {
      routes.WhichBenefitsDoYouGetController.onPageLoad(true)
    } else {
      if(pageObjects.whichOfYouInPaidEmployment.contains(YouPartnerBothEnum.PARTNER)) {
        routes.WhatsYourAgeController.onPageLoad(true)
      } else {
        routes.WhatsYourAgeController.onPageLoad(false)
      }
    }
  }

}
