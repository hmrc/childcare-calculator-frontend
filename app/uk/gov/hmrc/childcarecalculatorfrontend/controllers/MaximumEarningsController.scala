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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.MaximumEarningsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.{EmploymentStatusEnum, MinimumEarnings, PageObjects, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.HelperManager
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maximumEarnings

import scala.concurrent.Future

@Singleton
class MaximumEarningsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def validatePageObjects(pageObjects: PageObjects): Boolean = {
    println("************************ :::In validatePageObjects::: ****************"+pageObjects)
    pageObjects.livingWithPartner.isDefined
  }

  private def getBackUrl(pageObjects: PageObjects,
                         youPartnerBoth: String): Call = {
    println("************************ :::In getBackUrl ::: ****************")
    val paidEmployment: YouPartnerBothEnum = HelperManager.defineInPaidEmployment(pageObjects)
    youPartnerBoth match {
      case "YOU" => {
        println("************************ :::In getBackUrl :::  YOU ****************")
        if (paidEmployment == YouPartnerBothEnum.BOTH) {
          if (pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW.get) {
            routes.MinimumEarningsController.onPageLoad(true)
          } else if (pageObjects.household.partner.get.minimumEarnings.get.employmentStatus == Some(EmploymentStatusEnum.SELFEMPLOYED)) {
            routes.SelfEmployedController.onPageLoad(true)
          } else {
            routes.SelfEmployedOrApprenticeController.onPageLoad(true)
          }
        } else {
          routes.MinimumEarningsController.onPageLoad(false)
        }
      }
      case "PARTNER" => {
        println("************************ :::In getBackUrl :::  PARTNER ****************")
        if (paidEmployment == YouPartnerBothEnum.BOTH) {
          if (pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW.get) {
            routes.MinimumEarningsController.onPageLoad(true)
          } else if (pageObjects.household.parent.minimumEarnings.get.employmentStatus == Some(EmploymentStatusEnum.SELFEMPLOYED)) {
            routes.SelfEmployedController.onPageLoad(false)
          } else {
            routes.SelfEmployedOrApprenticeController.onPageLoad(false)
          }
        } else {
          routes.MinimumEarningsController.onPageLoad(true)
        }
      }
      case "BOTH" => {
        println("************************ :::In getBackUrl :::  PARTNER ****************")
        routes.MinimumEarningsController.onPageLoad(true)
      }
    }
  }

  def defineMaximumEarnings(parentPartnerBoth: String, pageObjects: PageObjects): Option[Boolean] = {
    if(parentPartnerBoth == YouPartnerBothEnum.PARTNER.toString) {
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

  def onPageLoad(youPartnerBoth: String): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) => {
        println("************************ :::In Some(pageObjects) ::: ****************")
        Ok(maximumEarnings(
          new MaximumEarningsForm(youPartnerBoth, messagesApi).form.fill(defineMaximumEarnings(youPartnerBoth, pageObjects)),
          youPartnerBoth,
          getBackUrl(pageObjects, youPartnerBoth))
        )
      }
      case _ =>
        Logger.warn("Invalid PageObjects in MaximumEarningsController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }.recover {
      case ex: Exception =>
        Logger.warn(s"Exception from MaximumEarningsController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit(youPartnerBoth: String): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) =>
        val inPaidEmployment: YouPartnerBothEnum = HelperManager.defineInPaidEmployment(pageObjects)
        new MaximumEarningsForm(youPartnerBoth, messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                maximumEarnings(
                  errors, youPartnerBoth, getBackUrl(pageObjects, youPartnerBoth)
                )
              )
            ),
          success => {
            keystore.cache(getModifiedPageObjects(success.get, pageObjects, youPartnerBoth)).map { _ =>
              //TODO: redirect to tc/uc page
              Redirect(routes.ChildCareBaseController.underConstruction)
            }
          }
        )
      case _ =>
        Logger.warn("PageObjects object is missing in MaximumEarningsController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from MaximumEarningsController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def getModifiedPageObjects(maxEarnings: Boolean, pageObjects: PageObjects, youPartnerBoth: String): PageObjects = {
    if(HelperManager.defineInPaidEmployment(pageObjects) == YouPartnerBothEnum.YOU) {
      pageObjects.copy(household = pageObjects.household.copy(
        parent = pageObjects.household.parent.copy(
          maximumEarnings = Some(maxEarnings)
        )
      ))
    } else if(HelperManager.defineInPaidEmployment(pageObjects) == YouPartnerBothEnum.PARTNER) {
      pageObjects.copy(household = pageObjects.household.copy(
        partner = pageObjects.household.partner.map(x => x.copy(
          maximumEarnings = Some(maxEarnings)
        ))
      ))
    } else {
      pageObjects.copy(household = pageObjects.household.copy(
        parent = pageObjects.household.parent.copy(
          maximumEarnings = Some(maxEarnings)
        ),
        partner = pageObjects.household.partner.map(x => x.copy(
          maximumEarnings = Some(maxEarnings)
        ))
      ))
    }

  }

}