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
import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.forms.HoursForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{PageObjects, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.hours
import scala.concurrent.Future

@Singleton
class HoursController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def isDataValid(pageObjects: PageObjects, isPartner: Boolean): Boolean = {
    pageObjects.livingWithPartner.isDefined && (
      !pageObjects.livingWithPartner.get || (
        pageObjects.livingWithPartner.get && pageObjects.whichOfYouInPaidEmployment.isDefined
      )
    ) && (
      !isPartner ||
        (isPartner && pageObjects.household.partner.isDefined)
    )
  }

  private def getBackUrl(pageObjects: PageObjects, isPartner: Boolean): Call = {
    if(pageObjects.livingWithPartner.get) {
      if(!isPartner && pageObjects.whichOfYouInPaidEmployment.get == YouPartnerBothEnum.BOTH) {
        routes.HoursController.onPageLoad(isPartner = !isPartner)
      }
      else {
        routes.WhichOfYouInPaidEmploymentController.onPageLoad()
      }
    }
    else {
      routes.PaidEmploymentController.onPageLoad()
    }
  }

  def onPageLoad(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) if isDataValid(pageObjects, isPartner) =>
        val poHours: Option[BigDecimal] = if(!isPartner) {
          pageObjects.household.parent.hours
        }
        else {
          pageObjects.household.partner.get.hours
        }
        Ok(
          hours(
            new HoursForm(messagesApi).form.fill(poHours),
            isPartner,
            getBackUrl(pageObjects, isPartner)
          )
        )
      case _ =>
        Logger.warn("Invalid PageObjects in HoursController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from HoursController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def modifyPageObjects(pageObjects: PageObjects, isPartner: Boolean, newHours: Option[BigDecimal]): PageObjects = {
    if (!isPartner) {
      pageObjects.copy(
        household = pageObjects.household.copy(
          parent = pageObjects.household.parent.copy(
            hours = newHours
          )
        )
      )
    }
    else {
      pageObjects.copy(
        household = pageObjects.household.copy(
          partner = Some(
            pageObjects.household.partner.get.copy(
              hours = newHours
            )
          )
        )
      )
    }
  }


  private def getNextPage(pageObjects: PageObjects, isPartner: Boolean): Call = {
    if(isPartner && pageObjects.whichOfYouInPaidEmployment.get == YouPartnerBothEnum.BOTH) {
      routes.HoursController.onPageLoad(isPartner = !isPartner)
    }
    else {
      routes.VouchersController.onPageLoad()
    }
  }

  def onSubmit(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) if isDataValid(pageObjects, isPartner) =>
        new HoursForm(messagesApi).form.bindFromRequest().fold(
          errors => {
            Future(
              BadRequest(
                hours(
                  errors,
                  isPartner,
                  getBackUrl(pageObjects, isPartner)
                )
              )
            )
          },
          success => {
            val modifiedPageObject = modifyPageObjects(pageObjects, isPartner, success)
            keystore.cache(modifiedPageObject).map { res =>
              Redirect(getNextPage(pageObjects, isPartner))
            }
          }
        )
      case _ =>
        Logger.warn("Invalid PageObjects in HoursController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from HoursController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

}
