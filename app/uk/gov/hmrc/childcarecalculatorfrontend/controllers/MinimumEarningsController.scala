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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{PageObjects, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.HelperManager
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.minimumEarning

import scala.concurrent.Future

@Singleton
class MinimumEarningsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController with HelperManager {

  val keystore: KeystoreService = KeystoreService

  private def backURL(pageObjects: PageObjects, isPartner: Boolean): Call = {
    routes.WhatsYourAgeController.onPageLoad(isPartner)
  }

  def getMinWageForScreen(pageObjects: PageObjects, isPartner: Boolean): BigDecimal = {
    if (isPartner) {
      getMinimumEarningsAmountForAgeRange(pageObjects.household.partner.get.ageRange.map(_.toString))
    } else {
      getMinimumEarningsAmountForAgeRange(pageObjects.household.parent.ageRange.map(_.toString))
    }
  }

  def onPageLoad(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects)  =>
        val minimumEarnings: Boolean = if(isPartner) {
          pageObjects.household.partner.get.minimumEarnings.isDefined
        } else {
          pageObjects.household.parent.minimumEarnings.isDefined
        }
        Ok(
          minimumEarning(
            new MinimumEarningsForm(isPartner, getMinWageForScreen(pageObjects, isPartner), messagesApi).form.fill(Some(minimumEarnings)),
            isPartner, getMinWageForScreen(pageObjects, isPartner), backURL(pageObjects, isPartner)
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

  def onSubmit(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) =>
        new MinimumEarningsForm(isPartner, getMinWageForScreen(pageObjects, isPartner), messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                minimumEarning(
                  errors, isPartner, getMinWageForScreen(pageObjects, isPartner), backURL(pageObjects, isPartner)
                )
              )
            ),
          success => {
            val minEarnings: Boolean = success.get
            keystore.cache(getModifiedPageObjects(minEarnings, pageObjects, isPartner)).map { _ =>
              if (minEarnings && pageObjects.whichOfYouInPaidEmployment.get == YouPartnerBothEnum.BOTH) {
                Redirect(routes.MinimumEarningsController.onPageLoad(true))
              } else if (minEarnings && (pageObjects.whichOfYouInPaidEmployment.get == YouPartnerBothEnum.YOU ||
                  pageObjects.whichOfYouInPaidEmployment.get == YouPartnerBothEnum.PARTNER)) {
                //TODO redirect to max earnings page
                Redirect(routes.ChildCareBaseController.underConstruction())
              } else {
                //TODO redirect to either of you self employed and fix tests
                Redirect(routes.ChildCareBaseController.underConstruction())
              }
            }
          }
        )
      case _ =>
        Logger.warn("PageObjects object is missing in MinimumEarningsController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from MinimumEarningsController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def getModifiedPageObjects(minEarningsBoolean: Boolean, pageObjects: PageObjects, isPartner: Boolean): PageObjects = {
    if(isPartner) {
      pageObjects.copy(
        household = pageObjects.household.copy(
          partner = pageObjects.household.partner.map { x => x.copy(
            minimumEarnings = pageObjects.household.partner.get.minimumEarnings.map { y => y.copy(earnMoreThanNMW = Some(minEarningsBoolean))}) }
        )
      )
    } else {
      pageObjects.copy(
        household = pageObjects.household.copy(
          parent = pageObjects.household.parent.copy(
            minimumEarnings = pageObjects.household.parent.minimumEarnings.map { x => x.copy(earnMoreThanNMW = Some(minEarningsBoolean))}
          )
        )
      )
    }
  }

}
