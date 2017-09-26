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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Benefits, Claimant, YouPartnerBothEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
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
            new WhichOfYouPaidEmploymentForm(messagesApi).form.fill(pageObjects.whichOfYouInPaidEmployment.map(_.toString))
          )
        )
      case _ =>
        Logger.warn("Invalid PageObjects in WhichOfYouInPaidEmploymentController.onPageLoad")
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
            val modifiedPageObjects = updatePageObjects(pageObjects, success.get)
            keystore.cache(modifiedPageObjects).map {
              result =>
                Redirect(
                  routes.HoursController.onPageLoad(
                    isPartner = YouPartnerBothEnum.withName(success.get) != YouPartnerBothEnum.YOU
                  )
                )
            }
          }
        )
      case _ =>
        Logger.warn("Invalid PageObjects in WhichOfYouInPaidEmploymentController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from WhichOfYouInPaidEmploymentController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }


  private def updatePageObjects(oldPageObjects: PageObjects, newWhichOfYouInPaidEmployment: String): PageObjects = {
    val newPaidEmployment: YouPartnerBothEnum = YouPartnerBothEnum.withName(newWhichOfYouInPaidEmployment)

    if (oldPageObjects.whichOfYouInPaidEmployment.contains(newPaidEmployment)) {
      oldPageObjects
    } else {
      val existingPaidEmployment = oldPageObjects.whichOfYouInPaidEmployment
      val updatedPageObjects = oldPageObjects.copy(whichOfYouInPaidEmployment = Some(newPaidEmployment))

      if (existingPaidEmployment.isEmpty) {
        updatedPageObjects
      } else {
       updatePageObjectsWithResetValuesForParentAndPartner(updatedPageObjects,
                                                            existingPaidEmployment,
                                                            newPaidEmployment)
      }
    }
  }

  /**
    * Resets the parent and partner value as per the change in selection
    * @param pageObjects
    * @param existingPaidEmployment
    * @param newPaidEmployment
    * @return
    */
  private def  updatePageObjectsWithResetValuesForParentAndPartner(pageObjects: PageObjects,
                                                                   existingPaidEmployment: Option[YouPartnerBothEnum],
                                                                   newPaidEmployment: YouPartnerBothEnum) = {

    val pageObjectsWithResetValues = pageObjects.copy(getVouchers = None, whoGetsVouchers = None)
    val houseHoldValue = pageObjectsWithResetValues.household

    val existingParent = houseHoldValue.parent
    val existingPartner = houseHoldValue.partner

    val existingParentBenefits = existingParent.benefits
    val existingPartnerBenefits = existingPartner.fold[Option[Benefits]](None)(_.benefits)

    val existingParentWithNoMaximumEarnings = existingParent.copy(maximumEarnings = None)
    val existingPartnerWithNoMaximumEarnings = Some(existingPartner.getOrElse(Claimant()).copy(maximumEarnings = None))

      (existingPaidEmployment, newPaidEmployment) match {

      case (Some(YouPartnerBothEnum.BOTH), YouPartnerBothEnum.YOU) => {
        pageObjectsWithResetValues.copy(household = houseHoldValue.copy(partner = Some(Claimant(benefits = existingPartnerBenefits)),
          parent = existingParentWithNoMaximumEarnings))
      }
      case (Some(YouPartnerBothEnum.BOTH), YouPartnerBothEnum.PARTNER) => {
        pageObjectsWithResetValues.copy(household = houseHoldValue.copy(parent = Claimant(benefits = existingParentBenefits),
          partner = existingPartnerWithNoMaximumEarnings))
      }
      case (Some(YouPartnerBothEnum.PARTNER), YouPartnerBothEnum.YOU) => {
        pageObjectsWithResetValues.copy(household = houseHoldValue.copy(partner = Some(Claimant(benefits = existingPartnerBenefits)),
          parent = existingParentWithNoMaximumEarnings))
      }
      case (Some(YouPartnerBothEnum.PARTNER), YouPartnerBothEnum.BOTH) => {
        pageObjectsWithResetValues.copy(household = houseHoldValue.copy(parent = Claimant(benefits = existingParentBenefits),
          partner = existingPartnerWithNoMaximumEarnings))
      }
      case (Some(YouPartnerBothEnum.YOU), YouPartnerBothEnum.PARTNER) => {
        pageObjectsWithResetValues.copy(household = houseHoldValue.copy(parent = Claimant(benefits = existingParentBenefits),
          partner = existingPartnerWithNoMaximumEarnings))
      }
      case (Some(YouPartnerBothEnum.YOU), YouPartnerBothEnum.BOTH) => {
        pageObjectsWithResetValues.copy(household = houseHoldValue.copy(partner = Some(Claimant(benefits = existingPartnerBenefits)),
          parent = existingParentWithNoMaximumEarnings))
      }
      case (_, _) => pageObjectsWithResetValues

    }
  }

}
