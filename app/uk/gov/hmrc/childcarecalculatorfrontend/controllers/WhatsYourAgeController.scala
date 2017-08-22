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
import play.api.mvc.{Action, AnyContent, Call, Result}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{LivingWithPartnerForm, LocationForm, WhatsYourAgeForm, WhichOfYouPaidEmploymentForm}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AgeRangeEnum, PageObjects, _}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html._
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class WhatsYourAgeController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def getBackUrl(isPartner: Boolean): Call = {
    if (isPartner) {
      //TODO redirect to correct page and change tests
      routes.WhatYouNeedController.onPageLoad()
    } else {
      //TODO redirect to correct page and change tests
      routes.WhatYouNeedController.onPageLoad()
    }
  }

  def onPageLoad(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map { pageObjects =>
      if(isPartner) {
        Ok(
          whatsYourAge(
            new WhatsYourAgeForm(isPartner, messagesApi).form.fill(pageObjects.get.household.partner.get.ageRange.map(_.toString)),
            getBackUrl(isPartner),
            isPartner
          )
        )
      } else {
        Ok(
          whatsYourAge(
            new WhatsYourAgeForm(isPartner, messagesApi).form.fill(pageObjects.get.household.parent.ageRange.map(_.toString)),
            getBackUrl(isPartner),
            isPartner
          )
        )
      }
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
                  errors,
                  getBackUrl(isPartner), isPartner
                )
              )
            ),
          success => {
            val enumValue: AgeRangeEnum.Value = AgeRangeEnum.withName(success.get)
            keystore.cache(getModifiedPageObjects(enumValue, pageObjects, isPartner)).map { result =>
              //TODO redirect to correct page and change tests
              Redirect(routes.WhatYouNeedController.onPageLoad())
            }
          }
        )
      case _ =>
        Logger.warn("PageObjects object is missing in LivingWithPartnerController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from LivingWithPartnerController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def getModifiedPageObjects(enumValue: AgeRangeEnum.Value, pageObjects: PageObjects, isPartner: Boolean): PageObjects ={
    if(isPartner) {
      val modifiedPartner: Claimant = pageObjects.household.partner.get.copy(
        ageRange = Some(enumValue)
      )

      val modifiedHousehold: Household = pageObjects.household.copy(
        partner = Some(modifiedPartner)
      )

      pageObjects.copy(
        household = modifiedHousehold
      )
    } else {
      val modifiedParent: Claimant = pageObjects.household.parent.copy(
        ageRange = Some(enumValue)
      )

      val modifiedHousehold: Household = pageObjects.household.copy(
        parent = modifiedParent
      )

      pageObjects.copy(
        household = modifiedHousehold
      )
    }
  }
}
