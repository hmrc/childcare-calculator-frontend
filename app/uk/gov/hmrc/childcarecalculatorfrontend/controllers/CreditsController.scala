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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.CreditsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.HelperManager
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.credits
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class CreditsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) =>
        val creditsValue = pageObjects.household.credits.getOrElse("").toString
        Ok(credits(
          new CreditsForm(messagesApi).form.fill(Some(creditsValue)), getBackUrl(pageObjects)
        ))
      case _ =>
        Logger.warn("Invalid PageObjects in CreditsController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }.recover {
      case ex: Exception =>
        Logger.warn(s"Exception from CreditsController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) =>
        new CreditsForm(messagesApi).form.bindFromRequest().fold(
          errors => {
            Future(BadRequest(credits(errors, getBackUrl(pageObjects))))
          },
          success => {
            nextPage(pageObjects, success.get)
          }
        )
      case _ =>
        Logger.warn("PageObjects object is missing in CreditsController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from CreditsController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }

  }

  private def getBackUrl(pageObjects: PageObjects): Call = {
    val paidEmployment: YouPartnerBothEnum = HelperManager.defineInPaidEmployment(pageObjects)
    val hasParentMinEarnings = pageObjects.household.parent.minimumEarnings.fold(false)(_.earnMoreThanNMW.fold(false)(identity))

    paidEmployment match {
      case YouPartnerBothEnum.YOU => {
        if (hasParentMinEarnings) {
          routes.MaximumEarningsController.onPageLoad(YouPartnerBothEnum.YOU.toString)
        } else if (pageObjects.household.parent.minimumEarnings.get.employmentStatus.contains(EmploymentStatusEnum.SELFEMPLOYED)) {
          routes.SelfEmployedController.onPageLoad(false)
        } else {
          routes.SelfEmployedOrApprenticeController.onPageLoad(false)
        }
      }
      case YouPartnerBothEnum.PARTNER => {
        val hasPartnerMinEarnings = pageObjects.household.partner.get.minimumEarnings.fold(false)(_.earnMoreThanNMW.fold(false)(identity))

        if (hasPartnerMinEarnings) {
          routes.MaximumEarningsController.onPageLoad(YouPartnerBothEnum.PARTNER.toString)
        } else if (pageObjects.household.partner.get.minimumEarnings.get.employmentStatus.contains(EmploymentStatusEnum.SELFEMPLOYED)) {
          routes.SelfEmployedController.onPageLoad(true)
        } else {
          routes.SelfEmployedOrApprenticeController.onPageLoad(true)
        }
      }
      case YouPartnerBothEnum.BOTH => {
        val hasPartnerMinEarnings = pageObjects.household.partner.get.minimumEarnings.fold(false)(_.earnMoreThanNMW.fold(false)(identity))

        (hasParentMinEarnings, hasPartnerMinEarnings) match {
          case (true, true) => routes.MaximumEarningsController.onPageLoad(YouPartnerBothEnum.BOTH.toString)
          case (true, false) => routes.MaximumEarningsController.onPageLoad(YouPartnerBothEnum.YOU.toString)
          case (false, true) => routes.MaximumEarningsController.onPageLoad(YouPartnerBothEnum.PARTNER.toString)
          case (_, _) => if (pageObjects.household.partner.get.minimumEarnings.get.employmentStatus.contains(EmploymentStatusEnum.SELFEMPLOYED)) {
            routes.SelfEmployedController.onPageLoad(true)
          } else {
            routes.SelfEmployedOrApprenticeController.onPageLoad(true)
          }
        }
      }
    }
  }

  private def validMinEarnings(modifiedPageObjects: PageObjects): Boolean = {
    val parent = modifiedPageObjects.household.parent
    val parentNMW = parent.minimumEarnings.fold(false)(_.earnMoreThanNMW.fold(false)(identity))
    val parentApprentice = parent.minimumEarnings.fold(false)(_.employmentStatus.contains(EmploymentStatusEnum.APPRENTICE))
    val parentSelfEmployed = parent.minimumEarnings.fold(false)(_.selfEmployedIn12Months.fold(false)(identity))

    val partnerNMW = modifiedPageObjects.household.partner.fold(false)(_.minimumEarnings.fold(false)
      (_.earnMoreThanNMW.fold(false)(identity)))
    val partnerApprentice = modifiedPageObjects.household.partner.fold(false)(_.minimumEarnings.fold(false)
      (_.employmentStatus.contains(EmploymentStatusEnum.APPRENTICE)))
    val partnerSelfEmployed = modifiedPageObjects.household.partner.fold(false)(_.minimumEarnings.fold(false)
      (_.selfEmployedIn12Months.fold(false)(identity)))

    val paidEmployment: YouPartnerBothEnum = HelperManager.defineInPaidEmployment(modifiedPageObjects)
    paidEmployment match {
      case YouPartnerBothEnum.BOTH =>
        (parentNMW, partnerNMW) match {
          case (true, true) => true
          case (true, false) => minEarnEligibility (partnerApprentice, partnerSelfEmployed)
          case (false, true) => minEarnEligibility (parentApprentice, parentSelfEmployed)
          case (false, false) => minEarnEligibility (partnerApprentice, partnerSelfEmployed) &&
          minEarnEligibility (parentApprentice, parentSelfEmployed)
        }
      case YouPartnerBothEnum.YOU => if(parentNMW) true else { minEarnEligibility (parentApprentice, parentSelfEmployed) }
      case YouPartnerBothEnum.PARTNER => if(partnerNMW) true else { minEarnEligibility (partnerApprentice, partnerSelfEmployed) }
    }

  }

  private def minEarnEligibility(isApp: Boolean, isSelfEmployed: Boolean): Boolean = {
    isApp || isSelfEmployed
  }

  private def validMaxEarnings(modifiedPageObjects: PageObjects): Boolean = {
    val parentMaxEarnings = modifiedPageObjects.household.parent.maximumEarnings.fold(false)(identity)
    val partnerMaxEarnings = modifiedPageObjects.household.partner.fold(false)(_.maximumEarnings.fold(false)(identity))

    val paidEmployment: YouPartnerBothEnum = HelperManager.defineInPaidEmployment(modifiedPageObjects)
    paidEmployment match {
      case YouPartnerBothEnum.BOTH =>
        (parentMaxEarnings, partnerMaxEarnings) match {
          case (true, true) => false
          case (_, _) => true
        }
      case YouPartnerBothEnum.YOU => !parentMaxEarnings
      case YouPartnerBothEnum.PARTNER => !partnerMaxEarnings
    }
  }

  private def checkMaxHoursEligibility(modifiedPageObjects: PageObjects): Boolean = {
    validMinEarnings(modifiedPageObjects) && validMaxEarnings(modifiedPageObjects) &&
      modifiedPageObjects.household.location == LocationEnum.ENGLAND
  }

  private def nextPage(pageObjects: PageObjects, selectedCredits: String)(implicit hc: HeaderCarrier): Future[Result] = {
    val modifiedPageObjects: PageObjects = getModifiedPageObjects(pageObjects, selectedCredits)
    val paidEmployment: YouPartnerBothEnum = HelperManager.defineInPaidEmployment(pageObjects)

    val hasChild3Or4 = modifiedPageObjects.household.childAgedThreeOrFour.getOrElse(false)
    val hasChildcareCost = modifiedPageObjects.expectChildcareCosts.getOrElse(false)

    keystore.cache(modifiedPageObjects).map { res =>
      (checkMaxHoursEligibility(modifiedPageObjects), hasChild3Or4) match {
        case (true, true) => Redirect(routes.MaxFreeHoursInfoController.onPageLoad()) //TODO Maximum hours info
        case (true, false) => Redirect(routes.ChildCareBaseController.underConstruction()) //TODO How many children
        case (_, _) => Redirect(routes.ChildCareBaseController.underConstruction()) //TODO Results page
      }
    }

  }

  private def getModifiedPageObjects(pageObjects: PageObjects, selectedCredits: String): PageObjects = {
    pageObjects.copy(
      household = pageObjects.household.copy(credits = Some(CreditsEnum.withName(selectedCredits)))
    )
  }

}
