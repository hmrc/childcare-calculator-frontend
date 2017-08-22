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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{GetBenefitsForm, PaidEmploymentForm}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Claimant, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.getBenefits

import scala.concurrent.Future

@Singleton
class GetBenefitsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def validatePageObjects(pageObjects: PageObjects): Boolean = {
    pageObjects.livingWithPartner.isDefined
  }

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) if validatePageObjects(pageObjects) =>
        val hasPartner = pageObjects.livingWithPartner.get
        Ok(
          getBenefits(
            new GetBenefitsForm(hasPartner, messagesApi).form.fill(pageObjects.paidOrSelfEmployed),
            hasPartner
          )
        )
      case _ =>
        Logger.warn("Invalid PageObjects in GetBenefitsController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from GetBenefitsController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def modifyPageObjects(oldPageObjects: PageObjects, newPaidOrSelfEmployed: Boolean): PageObjects = {
    if(oldPageObjects.paidOrSelfEmployed == Some(newPaidOrSelfEmployed)) {
      oldPageObjects
    }
    else {
      oldPageObjects.copy(
        paidOrSelfEmployed = Some(newPaidOrSelfEmployed),
        whichOfYouInPaidEmployment = None,
        getVouchers = None,
        household = oldPageObjects.household.copy(
          parent = Claimant(),
          partner = oldPageObjects.household.partner.map{x => Claimant()}
        )
      )
    }
  }

  private def getNextPage(hasPartner: Boolean, newPaidEmployment: Boolean): Call = {
    if(newPaidEmployment) {
      if(hasPartner) {
        routes.WhichOfYouInPaidEmploymentController.onPageLoad()
      } else {
        routes.HoursController.onPageLoad(isPartner = false)
      }
    } else {
      //TODO - redirect to result page when prototype is ready
      routes.ChildCareBaseController.underConstruction()
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects].flatMap {
      case Some(pageObjects) if validatePageObjects(pageObjects) =>
        val hasPartner = pageObjects.livingWithPartner.get
        new PaidEmploymentForm(hasPartner, messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                getBenefits(
                  errors, hasPartner
                )
              )
            ),
          success => {
            val modifiedPageObjects = modifyPageObjects(pageObjects, success.get)
            keystore.cache(modifiedPageObjects).map { result =>
              Redirect(getNextPage(hasPartner, success.get))
            }
          }
        )
      case _ =>
        Logger.warn("Invalid PageObjects in GetBenefitsController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from GetBenefitsController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }
}
