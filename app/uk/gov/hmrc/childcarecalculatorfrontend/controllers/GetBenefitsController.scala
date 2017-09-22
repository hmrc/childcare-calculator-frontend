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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.GetBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{PageObjects, YesNoUnsureEnum, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CCConstants, HelperManager}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.getBenefits

import scala.concurrent.Future

@Singleton
class GetBenefitsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) =>
        val hasPartner = isLivingWithPartner(pageObjects)
        Ok(
          getBenefits(
            new GetBenefitsForm(hasPartner, messagesApi).form.fill(pageObjects.getBenefits),
            hasPartner,
            getBackUrl(pageObjects)
          )
        )
      case _ =>
        Logger.warn("Invalid PageObjects in GetBenefitsController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from GetBenefitsController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects].flatMap {
      case Some(pageObjects) =>
        val hasPartner = isLivingWithPartner(pageObjects)
        new GetBenefitsForm(hasPartner, messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                getBenefits(errors, hasPartner, getBackUrl(pageObjects))
              )
            ),
          success => {
            val modifiedPageObjects = modifyPageObjects(pageObjects, success.get)
            keystore.cache(modifiedPageObjects).map { result =>
              Redirect(getNextPage(pageObjects, hasPartner, success.get))
            }
          }
        )
      case _ =>
        Logger.warn("Invalid PageObjects in GetBenefitsController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from GetBenefitsController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def modifyPageObjects(oldPageObjects: PageObjects, newGetBenefits: Boolean): PageObjects = {
    if(oldPageObjects.getBenefits.contains(newGetBenefits)) {
      oldPageObjects
    } else {
      val modifiedObject = oldPageObjects.copy(getBenefits = Some(newGetBenefits))

      val hasPartner = isLivingWithPartner(oldPageObjects)

      modifiedObject.copy(household = modifiedObject.household.copy(
        parent = modifiedObject.household.parent.copy(benefits = None),
        partner = modifiedObject.household.partner.map { x => x.copy(benefits = None) }
      ))

      (hasPartner, newGetBenefits) match {
        case (false, true) =>  {
          modifiedObject.copy(household = modifiedObject.household.copy(
            partner = modifiedObject.household.partner.map { x => x.copy(benefits = None)}
          ))
        }
        case (false, false) => {
          modifiedObject.copy(household = modifiedObject.household.copy(
            partner = modifiedObject.household.partner.map { x => x.copy(benefits = None)},
            parent = modifiedObject.household.parent.copy(benefits = None)
          ))
        }
        case (true, true) => {
          modifiedObject.copy(household = modifiedObject.household.copy(
            parent = modifiedObject.household.parent.copy(benefits = None)
          ))
        }
        case (true, false) => {
          modifiedObject.copy(household = modifiedObject.household.copy(
            parent = modifiedObject.household.parent.copy(benefits = None),
            partner = modifiedObject.household.partner.map { x => x.copy(benefits = None) }
          ))
        }
      }

    }
  }

  private def getBackUrl(pageObjects: PageObjects): Call = {
    if(pageObjects.whichOfYouInPaidEmployment.contains(YouPartnerBothEnum.BOTH) &&
      pageObjects.getVouchers.contains(YesNoUnsureEnum.YES)) {
      routes.WhoGetsVouchersController.onPageLoad()
    } else {
      routes.VouchersController.onPageLoad()
    }
  }

  private def getNextPage(pageObjects: PageObjects, hasPartner: Boolean, getBenefits: Boolean): Call = {
    if(getBenefits) {
      if(hasPartner) {
        routes.WhoGetsBenefitsController.onPageLoad()
      } else {
        routes.WhichBenefitsDoYouGetController.onPageLoad(false)
      }
    } else {
      if(hasPartner && pageObjects.whichOfYouInPaidEmployment.contains(YouPartnerBothEnum.PARTNER)) {
        routes.WhatsYourAgeController.onPageLoad(true)
      } else {
        routes.WhatsYourAgeController.onPageLoad(false)
      }
    }
  }

  private def isLivingWithPartner(pageObjects: PageObjects) = {
    HelperManager.isLivingWithPartner(pageObjects, CCConstants.getBenefitsControllerId)
  }

}
