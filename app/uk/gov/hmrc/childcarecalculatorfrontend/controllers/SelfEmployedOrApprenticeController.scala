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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.SelfEmployedOrApprenticeForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.HelperManager
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.selfEmployedOrApprentice

import scala.concurrent.Future

@Singleton
class SelfEmployedOrApprenticeController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  /**
    * Fills the form with Employment status value
    * @param pageObjects
    * @param isPartner
    * @return
    */
  private def filledForm(pageObjects: PageObjects, isPartner: Boolean) = {
    new SelfEmployedOrApprenticeForm(isPartner, messagesApi).form.fill(
      if (isPartner) {
        pageObjects.household.partner match {
          case Some(claimant) => claimant.minimumEarnings.map(_.employmentStatus.getOrElse("").toString)
          case _ => None
        }
      } else {
        pageObjects.household.parent.minimumEarnings.map(_.employmentStatus.getOrElse("").toString)
      }

    )
  }

  /**
    * Called on page load with default isPartner = false for in partner mode and
    * isPartner = true in partner mode
    * @param isPartner
    * @return
    */
  def onPageLoad(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects)  =>
        Ok(selfEmployedOrApprentice(filledForm(pageObjects, isPartner),
          isPartner,
          getBackUrl(pageObjects, isPartner)
        ))

      case _ =>
        Logger.warn("Invalid PageObjects in SelfEmployedOrApprenticeController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }.recover {
      case ex: Exception =>
        Logger.warn(s"Exception from SelfEmployedOrApprenticeController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  /**
    * Called on page submission with default isPartner = false for in partner mode and
    * isPartner = true in partner mode
    * @param isPartner
    * @return
    */
  def onSubmit(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) =>
        val paidEmployment = HelperManager.defineInPaidEmployment(pageObjects)
        new SelfEmployedOrApprenticeForm(isPartner, messagesApi).form.bindFromRequest().fold(
          errors =>{
            Future(
              BadRequest(
                selfEmployedOrApprentice(
                  errors, isPartner, getBackUrl(pageObjects, isPartner)
                )
              )
            )},

          selectedEmployedStatus => {
            val employedStatusValue = EmploymentStatusEnum.withName(selectedEmployedStatus.get)
            keystore.cache(getModifiedPageObjects(employedStatusValue, pageObjects, isPartner)).map { _ =>
              Redirect(getNextPageUrl(pageObjects, isPartner, selectedEmployedStatus.get))
            }
          }
        )
      case _ =>
        Logger.warn("PageObjects object is missing in SelfEmployedOrApprenticeController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from SelfEmployedOrApprenticeController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  /**
    * Gets the back url
    * @param pageObjects
    * @param isPartner
    * @return
    */
  private def getBackUrl(pageObjects: PageObjects, isPartner: Boolean): Call = {

    val paidEmployment = HelperManager.defineInPaidEmployment(pageObjects)

    if (isPartner) {
      paidEmployment match {
        case YouPartnerBothEnum.BOTH => {
          if(pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity)) {
            routes.MinimumEarningsController.onPageLoad(true)
          } else if(pageObjects.household.parent.minimumEarnings.get.employmentStatus.contains(Some(EmploymentStatusEnum.SELFEMPLOYED))) {
            routes.SelfEmployedController.onPageLoad(false)
          } else {
            routes.SelfEmployedOrApprenticeController.onPageLoad(false)
          }
        }
        case YouPartnerBothEnum.PARTNER => {
          routes.MinimumEarningsController.onPageLoad(true)
        }
      }
    } else {
      if(paidEmployment == YouPartnerBothEnum.YOU) {
        routes.MinimumEarningsController.onPageLoad(false)
      } else {
        routes.MinimumEarningsController.onPageLoad(true)
      }
    }
  }

  /**
    * Gets the next page url
    * @param pageObjects
    * @param isPartner
    * @return
    */
  private def getNextPageUrl(pageObjects: PageObjects, isPartner: Boolean, selectedEmployedStatus: String): Call = {
    val paidEmployment: YouPartnerBothEnum = HelperManager.defineInPaidEmployment(pageObjects)

    if(isPartner) {
     paidEmployment match {
        case YouPartnerBothEnum.BOTH => {
          if(selectedEmployedStatus == EmploymentStatusEnum.SELFEMPLOYED.toString) {
            routes.SelfEmployedController.onPageLoad(true)
          } else if(pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity)) {
            routes.MaximumEarningsController.onPageLoad(YouPartnerBothEnum.YOU.toString)
          } else {
             //TODO: redirect to tc/uc page
            routes.ChildCareBaseController.underConstruction()
          }
        }
        case YouPartnerBothEnum.PARTNER => {
          if(selectedEmployedStatus == EmploymentStatusEnum.SELFEMPLOYED.toString) {
            routes.SelfEmployedController.onPageLoad(true)
          } else {
            //TODO: redirect to tc/uc page
            routes.ChildCareBaseController.underConstruction()
          }
        }
      }
    } else {
      paidEmployment match {

        case YouPartnerBothEnum.BOTH => {
          if(selectedEmployedStatus == EmploymentStatusEnum.SELFEMPLOYED.toString) {
            routes.SelfEmployedController.onPageLoad(false)
          } else if(pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity)) {
            routes.MaximumEarningsController.onPageLoad(YouPartnerBothEnum.PARTNER.toString)
          } else {
            routes.SelfEmployedOrApprenticeController.onPageLoad(true)
          }
        }
        case YouPartnerBothEnum.YOU => {
          if(selectedEmployedStatus == EmploymentStatusEnum.SELFEMPLOYED.toString) {
            routes.SelfEmployedController.onPageLoad(false)
          } else {
            //TODO: redirect to tc/uc page
            routes.ChildCareBaseController.underConstruction()
          }
        }
      }
    }
  }

  private def getModifiedPageObjects(employmentStatus: EmploymentStatusEnum.Value,
                                     pageObjects: PageObjects,
                                     isPartner: Boolean): PageObjects = {


    val paidEmployment: YouPartnerBothEnum = HelperManager.defineInPaidEmployment(pageObjects)
    if(isPartner) {
      pageObjects.copy(household = pageObjects.household.copy(
        partner = Some(pageObjects.household.partner.fold(Claimant())(x => x.copy(
          minimumEarnings = Some(x.minimumEarnings.fold(
            MinimumEarnings(employmentStatus = Some(employmentStatus)))(_.copy(
            employmentStatus = Some(employmentStatus))))
        ))
        )
      ))
    } else {
      pageObjects.copy(household = pageObjects.household.copy(
        parent = pageObjects.household.parent.copy(
          minimumEarnings = Some(pageObjects.household.parent.minimumEarnings.fold(MinimumEarnings(employmentStatus = Some(employmentStatus)))(_.copy(
            employmentStatus = Some(employmentStatus)))
          ))
      ))
    }

  }
}
