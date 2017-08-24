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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhatsYourAgeForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AgeRangeEnum, PageObjects, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html._

import scala.concurrent.Future

@Singleton
class WhatsYourAgeController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def getBackUrl(pageObjects: PageObjects, isPartner: Boolean): Call = {
    if(isPartner) {
      if (pageObjects.whichOfYouInPaidEmployment == Some(YouPartnerBothEnum.BOTH)) {
        routes.WhatsYourAgeController.onPageLoad(false)
      } else if (pageObjects.whichOfYouInPaidEmployment == Some(YouPartnerBothEnum.PARTNER) &&
        pageObjects.household.partner.get.benefits.isDefined) {
        //TODO redirect to partner's which benefits page and change tests
        routes.ChildCareBaseController.underConstruction()
      } else if (pageObjects.whichOfYouInPaidEmployment == Some(YouPartnerBothEnum.BOTH) &&
        pageObjects.household.parent.benefits.isDefined) {
        //TODO redirect to parent's which benefits page and change tests
        routes.ChildCareBaseController.underConstruction()
      } else {
        routes.GetBenefitsController.onPageLoad()
      }
    } else {
      if(pageObjects.getBenefits == Some(false)) {
        routes.GetBenefitsController.onPageLoad()
      } else if (pageObjects.household.partner.get.benefits.isDefined &&
          (pageObjects.whichOfYouInPaidEmployment == Some(YouPartnerBothEnum.BOTH) ||
           pageObjects.whichOfYouInPaidEmployment == Some(YouPartnerBothEnum.PARTNER)
          )
        ) {
        //TODO redirect to parent's which benefits page and change tests
        routes.ChildCareBaseController.underConstruction()
      } else {
        //TODO redirect to partner's which benefits page and change tests
        routes.ChildCareBaseController.underConstruction()
      }
    }
  }

  def onPageLoad(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) if (!isPartner || (pageObjects.household.partner.isDefined && isPartner)) =>
        val agePageObject = if(isPartner) {
          pageObjects.household.partner.get.ageRange.map(_.toString)
        } else {
          pageObjects.household.parent.ageRange.map(_.toString)
        }
        Ok(
          whatsYourAge(
            new WhatsYourAgeForm(isPartner, messagesApi).form.fill(agePageObject),
            getBackUrl(pageObjects, isPartner),
            isPartner
          )
        )
      case _ =>
        Logger.warn("Invalid PageObjects in GetBenefitsController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())


    }.recover {
      case ex: Exception =>
        Logger.warn(s"Exception from WhatsYourAgeController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) =>
        new WhatsYourAgeForm(isPartner, messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                whatsYourAge(
                  errors, getBackUrl(pageObjects, isPartner), isPartner
                )
              )
            ),
          success => {
            val enumValue: AgeRangeEnum.Value = AgeRangeEnum.withName(success.get)
            keystore.cache(getModifiedPageObjects(enumValue, pageObjects, isPartner)).map { result =>
              if(isPartner) {
                //TODO redirect to min earnings partner page and change tests
                Redirect(routes.ChildCareBaseController.underConstruction())
              } else {
                if(pageObjects.whichOfYouInPaidEmployment == Some(YouPartnerBothEnum.BOTH)) {
                  Redirect(routes.WhatsYourAgeController.onPageLoad(true))
                } else {
                  //TODO redirect to min earnings parent page and change tests
                  Redirect(routes.ChildCareBaseController.underConstruction())
                }
              }
            }
          }
        )
      case _ =>
        Logger.warn("PageObjects object is missing in WhatsYourAgeController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from WhatsYourAgeController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def getModifiedPageObjects(enumValue: AgeRangeEnum.Value, pageObjects: PageObjects, isPartner: Boolean): PageObjects = {
    if(isPartner) {
      pageObjects.copy(
        household = pageObjects.household.copy(
          partner = pageObjects.household.partner.map { x => x.copy(ageRange = Some(enumValue)) }
        )
      )
    } else {
      pageObjects.copy(
        household = pageObjects.household.copy(
          parent = pageObjects.household.parent.copy(ageRange = Some(enumValue))
        )
      )
    }

  }
}
