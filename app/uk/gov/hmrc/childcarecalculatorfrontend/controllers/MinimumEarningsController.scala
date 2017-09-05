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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.MinimumEarningsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.PageObjects
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.minimumEarning

@Singleton
class MinimumEarningsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  val amount: BigDecimal = 123

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

  private def isDataValid(pageObjects: PageObjects, isPartner: Boolean): Boolean = {
    (!isPartner || (isPartner && pageObjects.household.partner.isDefined)) && pageObjects.livingWithPartner.isDefined
  }

  def onPageLoad(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) if isDataValid(pageObjects, isPartner) =>
        val minimumEarnings: Boolean = if(isPartner) {
          pageObjects.household.partner.get.minimumEarnings.isDefined
        } else {
          pageObjects.household.parent.minimumEarnings.isDefined
        }
        Ok(
          minimumEarning(
            new MinimumEarningsForm(isPartner, amount, messagesApi).form.fill(Some(minimumEarnings)),
            isPartner,
            amount,
            backURL(isPartner, pageObjects)
          )
        )
      case _ =>
        Logger.warn("Invalid PageObjects in MinimumEarningsController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from MinimumEarningsController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit(isPartner: Boolean): Action[AnyContent] = ???

}
