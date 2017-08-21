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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.PaidEmploymentForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Claimant, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.paidEmployment

import scala.concurrent.Future

@Singleton
class PaidEmploymentController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def modifyPageObjects(oldPageObjects: PageObjects, newPaidOrSelfEmployed: Boolean): PageObjects = {
    newPaidOrSelfEmployed match {
      case true => oldPageObjects.copy(
        paidOrSelfEmployed = Some(newPaidOrSelfEmployed)
      )
      case false if oldPageObjects.household.partner.isDefined => oldPageObjects.copy(
        paidOrSelfEmployed = Some(newPaidOrSelfEmployed),
        whichOfYouInPaidEmployment = None,
        getVouchers = None,
        household = oldPageObjects.household.copy(
          parent = Claimant(),
          partner = Some(Claimant())
        )
      )
      case false => oldPageObjects.copy(
        paidOrSelfEmployed = Some(newPaidOrSelfEmployed),
        whichOfYouInPaidEmployment = None,
        getVouchers = None,
        household = oldPageObjects.household.copy(
          parent = Claimant(),
          partner = None
        )
      )
    }

  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects].flatMap {
      case Some(pageObjects) =>
        val hasPartner = pageObjects.livingWithPartner.getOrElse(false)
        new PaidEmploymentForm(hasPartner, messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                paidEmployment(
                  errors, hasPartner
                )
              )
            ),
          success => {
            val modifiedPageObjects = modifyPageObjects(pageObjects, success.get)
            keystore.cache(modifiedPageObjects).map { result =>
              if(success.get) {
                if(hasPartner) {
                  Redirect(routes.WhichOfYouInPaidEmploymentController.onPageLoad())
                } else {
                  Redirect(routes.HoursController.onPageLoad(isPartner = false))
                }
              } else {
                //TODO - redirect to result page when prototype is ready
                Redirect(routes.ChildCareBaseController.underConstruction())
              }
            }
          }
        )
      case _ =>
        Logger.warn("PageObjects object is missing in PaidEmploymentController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from PaidEmploymentController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) =>
        val hasPartner = pageObjects.livingWithPartner.getOrElse(false)
        Ok(
          paidEmployment(new PaidEmploymentForm(hasPartner, messagesApi).form.fill(pageObjects.paidOrSelfEmployed),
            hasPartner
          )
        )
      case _ =>
        Logger.warn("PageObjects object is missing in PaidEmploymentController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from PaidEmploymentController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }
}
