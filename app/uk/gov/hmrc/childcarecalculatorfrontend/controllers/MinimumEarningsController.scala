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
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.{MinimumEarnings, PageObjects, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.HelperManager
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.minimumEarnings

import scala.concurrent.Future

@Singleton
class MinimumEarningsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController with HelperManager {

  val keystore: KeystoreService = KeystoreService

  private def backURL(inPaidEmployment: YouPartnerBothEnum, isPartner: Boolean): Call = {
    if(isPartner && inPaidEmployment == YouPartnerBothEnum.BOTH) {
      routes.MinimumEarningsController.onPageLoad(false)
    } else if(!isPartner && inPaidEmployment == YouPartnerBothEnum.BOTH) {
      routes.WhatsYourAgeController.onPageLoad(true)
    } else {
      routes.WhatsYourAgeController.onPageLoad(isPartner)
    }
  }

  private def getMinWageForScreen(pageObjects: PageObjects, isPartner: Boolean): BigDecimal = {
    if (isPartner) {
      getMinimumEarningsAmountForAgeRange(pageObjects.household.partner.get.ageRange.map(_.toString))
    } else {
      getMinimumEarningsAmountForAgeRange(pageObjects.household.parent.ageRange.map(_.toString))
    }
  }

  def onPageLoad(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects)  =>
        Ok(
          minimumEarnings(
            new MinimumEarningsForm(isPartner, getMinWageForScreen(pageObjects, isPartner), messagesApi).form.fill(
              defineMinimumEarnings(isPartner, pageObjects)),
              isPartner, getMinWageForScreen(pageObjects, isPartner), backURL(defineInPaidEmployment(pageObjects), isPartner)
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
      case Some(pageObjects) => {
        val inPaidEmployment: YouPartnerBothEnum = defineInPaidEmployment(pageObjects)
        new MinimumEarningsForm(isPartner, getMinWageForScreen(pageObjects, isPartner), messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                minimumEarnings(
                  errors, isPartner, getMinWageForScreen(pageObjects, isPartner), backURL(inPaidEmployment, isPartner)
                )
              )
            ),
          success => {
            val minEarnings: Boolean = success.get
            keystore.cache(getModifiedPageObjects(minEarnings, pageObjects, isPartner)).map { _ =>
              Redirect(getNextPage(pageObjects, minEarnings, isPartner))
            }
          }
        )
      }
      case _ =>
        Logger.warn("PageObjects object is missing in MinimumEarningsController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from MinimumEarningsController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def getNextPage(pageObjects: PageObjects, minEarnings: Boolean, isPartner: Boolean): Call = {
    val inPaidEmployment: YouPartnerBothEnum = HelperManager.defineInPaidEmployment(pageObjects)
    if(isPartner) {
      inPaidEmployment match {
        case YouPartnerBothEnum.BOTH => {

          val  parentEarnMoreThanMW= pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW.fold(false)(identity)

          if(minEarnings) {
            if(parentEarnMoreThanMW) {
              routes.MaximumEarningsController.onPageLoad(YouPartnerBothEnum.BOTH.toString)

            } else {
              routes.SelfEmployedOrApprenticeController.onPageLoad(false)
            }
          } else {
            if(parentEarnMoreThanMW) {
              routes.SelfEmployedOrApprenticeController.onPageLoad(true)
            } else {
              routes.SelfEmployedOrApprenticeController.onPageLoad(false)
            }
          }
        }
        case YouPartnerBothEnum.PARTNER => {
          if(minEarnings) {
            routes.MaximumEarningsController.onPageLoad(YouPartnerBothEnum.PARTNER.toString)
          } else {
            routes.SelfEmployedOrApprenticeController.onPageLoad(true)
          }
        }
      }
    } else {
      inPaidEmployment match {
        case YouPartnerBothEnum.BOTH => {
          routes.MinimumEarningsController.onPageLoad(true)
        }
        case YouPartnerBothEnum.YOU => {
          if(minEarnings) {
            routes.MaximumEarningsController.onPageLoad(YouPartnerBothEnum.YOU.toString)
          } else {
            routes.SelfEmployedOrApprenticeController.onPageLoad(false)
          }
        }
      }
    }

  }

  private def getModifiedPageObjects(minEarnings: Boolean, pageObjects: PageObjects, isPartner: Boolean): PageObjects = {
    if(isPartner && (defineInPaidEmployment(pageObjects) == YouPartnerBothEnum.PARTNER)) {
      pageObjects.copy(household = pageObjects.household.copy(
        partner = pageObjects.household.partner.map(x => x.copy(
          minimumEarnings = Some(x.minimumEarnings.fold(
            MinimumEarnings(earnMoreThanNMW = Some(minEarnings)))(_.copy(
            earnMoreThanNMW = Some(minEarnings), amount = getMinWageForScreen(pageObjects, isPartner))))
        ))
      ))
    } else {
      pageObjects.copy(household = pageObjects.household.copy(
        parent = pageObjects.household.parent.copy(
          minimumEarnings = Some(pageObjects.household.parent.minimumEarnings.fold(
            MinimumEarnings(earnMoreThanNMW = Some(minEarnings)))(_.copy(
            earnMoreThanNMW = Some(minEarnings), amount = getMinWageForScreen(pageObjects, isPartner)))
          )
        )
      ))
    }

  }

}
