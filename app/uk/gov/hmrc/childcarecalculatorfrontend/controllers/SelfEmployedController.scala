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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.SelfEmployedForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.selfEmployed

import scala.concurrent.Future

@Singleton
class SelfEmployedController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def getBackUrl(isPartner: Boolean, pageObjects: PageObjects): Call = {
    if(!isPartner) {
      // TODO - redirect to 'selfemployed or apprentice' with ispartner flag set to false - It's a partner
      routes.ChildCareBaseController.underConstruction()
    } else {
      // TODO - redirect to 'selfemployed or apprentice' with ispartner flag set to false - It's a parent
      routes.ChildCareBaseController.underConstruction()
    }
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
            selfEmployed(
              new SelfEmployedForm(isPartner, messagesApi).form.fill(Some(partnerSelfEmployedIn12Months)), isPartner, getBackUrl(isPartner, pageObjects)
            )
          )
        } else {
          val parentSelfEmployedIn12Months = pageObjects.household.parent.minimumEarnings.get.selfEmployedIn12Months.get
          Ok(
            selfEmployed(
              new SelfEmployedForm(isPartner, messagesApi).form.fill(Some(parentSelfEmployedIn12Months)), isPartner, getBackUrl(isPartner, pageObjects)
            )
          )
        }
      case _ =>
        Logger.warn("Invalid PageObjects in SelfEmployedController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from SelfEmployedController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def modifyPageObjects(isPartner: Boolean, oldPageObjects: PageObjects, newSelfEmployedLessThanTwelveMonths: Boolean): PageObjects = {
    println(oldPageObjects)
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
/*
A single user who checks "Yes" or 'No' and continue, will be taken to the ''Do you get tax credits or universal credit?" screen
If a user with a partner checks 'Yes' and that partner does not satisfy the minimum earnings rule they will be taken to the ''Is your partner self employed or an apprentice?'' screen
If a user responding about a partner checks 'Yes' and that partner satisfies the minimum earnings rule they will be taken to the ''Will your partner earn more than £100,000 a year?' screen
If a user responding about a partner and that partner doesn't satisfy the minimum income rule, checks 'No' they will be taken to "Do you get tax credits or universal credit' screen
 */
  private def getNextPage(pageObjects: PageObjects, isPartner: Boolean): Call = {
    val familyEmploymentStatus: Option[YouPartnerBothEnum] = pageObjects.whichOfYouInPaidEmployment
//This far in journey minimumEarnings will exist
    val parentMinimumWage: Option[Boolean] = pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW
    if(familyEmploymentStatus == YouPartnerBothEnum.YOU){
      if(parentMinimumWage == Some(true)) {
        //TODO set correct link to 'Earn more than 100000' page with ispartner flag set to false
        routes.ChildCareBaseController.underConstruction()
      } else {
        //TODO set correct link to 'Do you get tax credits or universal credit' page
        routes.ChildCareBaseController.underConstruction()
      }
    } else if (familyEmploymentStatus == YouPartnerBothEnum.PARTNER) {
      val partnerMinimumWage: Option[Boolean] = pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW
      if(partnerMinimumWage == Some(true)) {
        //TODO set correct link to 'Earn more than 100000' page with ispartner flag set to true
        routes.ChildCareBaseController.underConstruction()
      } else {
        //TODO set correct link to 'Do you get tax credits or universal credit' page
        routes.ChildCareBaseController.underConstruction()
      }
    } else {
      if(isPartner) {
        val partnerMinimumWage: Option[Boolean] = pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW
        if(partnerMinimumWage == Some(true)) {
          //TODO set correct link to 'Earn more than 100000' page with ispartner flag set to true
          routes.ChildCareBaseController.underConstruction()
        } else if(parentMinimumWage == Some(true)) {
          //TODO set correct link to 'Earn more than 100000' page with ispartner flag set to false
          routes.ChildCareBaseController.underConstruction()
        } else {
          //TODO set correct link to 'Do you get tax credits or universal credit' page
          routes.ChildCareBaseController.underConstruction()
        }
      } else {
        // TODO - redirect to 'selfemployed or apprentice' with ispartner flag set to false
        routes.ChildCareBaseController.underConstruction()
      }
    }
  }

  def onSubmit(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects].flatMap {
      case Some(pageObjects) =>

        new SelfEmployedForm(isPartner, messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                selfEmployed(errors, isPartner, getBackUrl(isPartner, pageObjects))
              )
            ),
          success => {
            val modifiedPageObjects = modifyPageObjects(isPartner, pageObjects, success.get)
            keystore.cache(modifiedPageObjects).map { result =>
              Redirect(getNextPage(modifiedPageObjects, isPartner))
            }
          }
        )
      case _ =>
        Logger.warn("Invalid PageObjects in SelfEmployedController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from SelfEmployedController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }
}
