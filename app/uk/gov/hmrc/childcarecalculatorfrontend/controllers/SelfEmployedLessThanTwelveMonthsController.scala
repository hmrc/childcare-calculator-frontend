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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.SelfEmployedLessThanTwelveMonthsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.{benefits, selfEmployedLessThanTwelveMonths}

import scala.concurrent.Future

@Singleton
class SelfEmployedLessThanTwelveMonthsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def getBackUrl(isPartner: Boolean, pageObjects: PageObjects): Call = {
      // TODO - redirect to 'selfemployed or apprentice'
      routes.ChildCareBaseController.underConstruction()
      //routes.SelfemployedOrApprenticeController.onPageLoad(pageObjects.livingWithPartner)
  }

  def onPageLoad(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) if ( (isPartner && pageObjects.household.partner.isDefined
                                  && pageObjects.household.partner.get.minimumEarnings.isDefined
                                  && pageObjects.household.partner.get.minimumEarnings.get.selfEmployedIn12Months.isDefined )
                                  ||
                                  (isPartner && pageObjects.household.partner.isDefined
                                  && pageObjects.household.partner.get.minimumEarnings.isDefined
                                  && pageObjects.household.partner.get.minimumEarnings.get.selfEmployedIn12Months.isDefined) ) =>

        if(isPartner){
          val partnerSelfEmployedIn12Months: Boolean = pageObjects.household.partner.get.minimumEarnings.get.selfEmployedIn12Months.get
          Ok(
            selfEmployedLessThanTwelveMonths(
              new SelfEmployedLessThanTwelveMonthsForm(isPartner, messagesApi).form.fill(Some(partnerSelfEmployedIn12Months)), isPartner, getBackUrl(isPartner, pageObjects)
            )
          )
        } else {
          val parentSelfEmployedIn12Months = pageObjects.household.parent.minimumEarnings.get.selfEmployedIn12Months.get
          Ok(
            selfEmployedLessThanTwelveMonths(
              new SelfEmployedLessThanTwelveMonthsForm(isPartner, messagesApi).form.fill(Some(parentSelfEmployedIn12Months)), isPartner, getBackUrl(isPartner, pageObjects)
            )
          )
        }
      case _ =>
        Logger.warn("Invalid PageObjects in SelfEmployedLessThanTwelveMonthsController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from SelfEmployedLessThanTwelveMonthsController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def modifyPageObjects(isPartner: Boolean, oldPageObjects: PageObjects, newSelfEmployedLessThanTwelveMonths: Boolean): PageObjects = {
    if(isPartner) {
      //it's the partner info
      oldPageObjects.household.partner.flatMap[PageObjects](partner => {
        partner.minimumEarnings.flatMap[PageObjects](minEarnings => {
          minEarnings.selfEmployedIn12Months.flatMap[PageObjects](existingPartnerSelfEmployedIn12Months => {
            if (newSelfEmployedLessThanTwelveMonths == existingPartnerSelfEmployedIn12Months) {
              //same so just return the existing info
              Some(oldPageObjects)
            } else {
              //need to substitute new value
              val newMinimumEarningsObject: MinimumEarnings = oldPageObjects.household.partner.get.minimumEarnings.get.copy(selfEmployedIn12Months = Some(newSelfEmployedLessThanTwelveMonths))
              val newPartnerObject: Claimant = oldPageObjects.household.partner.get.copy(minimumEarnings = Some(newMinimumEarningsObject))
              val newHouseholdObject: Household = oldPageObjects.household.copy(partner = Some(newPartnerObject))
              Some(oldPageObjects.copy(household = newHouseholdObject))
            }
          })
        })
      }).getOrElse(oldPageObjects)
    } else {
      //its parent info
      oldPageObjects.household.parent.minimumEarnings.flatMap[PageObjects](minEarnings => {
        minEarnings.selfEmployedIn12Months.flatMap[PageObjects](existingParentSelfEmployedIn12Months => {
          if(newSelfEmployedLessThanTwelveMonths == existingParentSelfEmployedIn12Months) {
            //same so just return the existing info
            Some(oldPageObjects)
          } else {
            //need to substitute new value
            val newMinimumEarningsObject: MinimumEarnings = oldPageObjects.household.parent.minimumEarnings.get.copy(selfEmployedIn12Months = Some(newSelfEmployedLessThanTwelveMonths))
            val newParentObject: Claimant = oldPageObjects.household.parent.copy(minimumEarnings = Some(newMinimumEarningsObject))
            val newHouseholdObject: Household = oldPageObjects.household.copy(parent = newParentObject)
            Some(oldPageObjects.copy(household = newHouseholdObject))
          }
        })
      }).getOrElse(oldPageObjects)
    }
  }

  private def getNextPage(pageObjects: PageObjects, isPartner: Boolean, selfEmployedLessThanTwelveMonths: Boolean): Call = {
    if(selfEmployedLessThanTwelveMonths) {
      if(isPartner) {
        //TODO set correct link
        routes.ChildCareBaseController.underConstruction()
      } else {
        //TODO set correct link
        routes.ChildCareBaseController.underConstruction()
      }
    } else {
      //TODO set correct link
      routes.ChildCareBaseController.underConstruction()
    }
  }

  def onSubmit(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects].flatMap {
      case Some(pageObjects) =>

        new SelfEmployedLessThanTwelveMonthsForm(isPartner, messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                selfEmployedLessThanTwelveMonths(errors, isPartner, getBackUrl(isPartner, pageObjects))
              )
            ),
          success => {
            val modifiedPageObjects = modifyPageObjects(isPartner, pageObjects, success.get)
            keystore.cache(modifiedPageObjects).map { result =>
              Redirect(getNextPage(pageObjects, isPartner, success.get))
            }
          }
        )
      case _ =>
        Logger.warn("Invalid PageObjects in SelfEmployedLessThanTwelveMonthsController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from SelfEmployedLessThanTwelveMonthsController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }
}
