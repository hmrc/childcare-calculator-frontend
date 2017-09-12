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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.MaximumEarningsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YouPartnerBothEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maximumEarnings

@Singleton
class MaximumEarningsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def validatePageObjects(pageObjects: PageObjects): Boolean = {
    pageObjects.livingWithPartner.isDefined
  }

  private def getBackUrl(pageObjects: PageObjects,
                         hasPartner: Boolean,
                         isPartner: Boolean): Call = {
    if(hasPartner) {
      // if parent doesn't meet minimum earning and partner does then go back to partner minimum earning page
      if (pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity) &&
          !pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity) ||
          //OR if both parent and partner both meet minimum earning then go back to partner minimum earning page
          pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity) &&
          pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity)) {
        routes.MinimumEarningsController.onPageLoad(true)
        // if partner doesn't meet minimum earning and parent does AND partner is in self employment then go back to partner self employed page
      } else if (!pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity) &&
        pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity) &&
        pageObjects.household.partner.get.minimumEarnings.get.selfEmployedIn12Months.fold(false)(identity)) {
        routes.SelfEmployedController.onPageLoad(true)
        // if partner doesn't meet minimum earning and parent does AND partner is apprentice/neither then go back to partner self employed/apprentice page
      } else if (!pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity) &&
        pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity) &&
        !pageObjects.household.partner.get.minimumEarnings.get.selfEmployedIn12Months.fold(false)(identity)) {
        routes.SelfEmployedOrApprenticeController.onPageLoad(true)
        // if parent doesn't meet minimum earning and partner does AND parent is in self employment then go back to parent self employed page
      } else if (pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity) &&
        !pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity) &&
        pageObjects.household.parent.minimumEarnings.get.selfEmployedIn12Months.fold(false)(identity)) {
        routes.SelfEmployedController.onPageLoad(false)
        // if parent doesn't meet minimum earning and partner does AND parent is apprentice/neither then go back to parent self employed/apprentice page
      } else {
        routes.SelfEmployedOrApprenticeController.onPageLoad(false)
      }
    // if doesn't have a partner then go back to parent minimum earning page
    } else {
      routes.MinimumEarningsController.onPageLoad(false)
    }
  }

  def defineMaximumEarnings(isPartner: Boolean, pageObjects: PageObjects): Option[Boolean] = {
    if(isPartner) {
      if(pageObjects.household.partner.isDefined && pageObjects.household.partner.get.maximumEarnings.isDefined &&
        pageObjects.household.partner.get.maximumEarnings.isDefined) {
        pageObjects.household.partner.get.maximumEarnings
      } else {
        None
      }
    } else {
      if(pageObjects.household.parent.maximumEarnings.isDefined && pageObjects.household.parent.maximumEarnings.isDefined) {
        pageObjects.household.parent.maximumEarnings
      } else {
        None
      }
    }
  }

  def onPageLoad(hasPartner: Boolean, isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) if validatePageObjects(pageObjects) =>
//        val youPartnerBoth = if (pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity) &&
//          pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity)) {
//          YouPartnerBothEnum.BOTH.toString
//        } else {
//          if (!pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity) &&
//               pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity)) {
//            YouPartnerBothEnum.PARTNER.toString
//          } else {
//            YouPartnerBothEnum.YOU.toString
//          }
//        }
        val youPartnerBoth = if(hasPartner) {
          YouPartnerBothEnum.BOTH.toString
        } else if(isPartner) {
          YouPartnerBothEnum.PARTNER.toString
        } else {
          YouPartnerBothEnum.YOU.toString
        }
        Ok(maximumEarnings(
          new MaximumEarningsForm(youPartnerBoth, messagesApi).form.fill(defineMaximumEarnings(isPartner, pageObjects)),
          youPartnerBoth,
          getBackUrl(pageObjects, hasPartner, isPartner))
        )
      case _ =>
        Logger.warn("Invalid PageObjects in MaximumEarningsController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }.recover {
      case ex: Exception =>
        Logger.warn(s"Exception from MaximumEarningsController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit(hasPartner: Boolean, isPartner: Boolean): Action[AnyContent] = withSession { ??? }

}
