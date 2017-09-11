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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.SelfEmployedForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.HelperManager
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.selfEmployed

import scala.concurrent.Future

@Singleton
class SelfEmployedController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController with HelperManager {

  val keystore: KeystoreService = KeystoreService

  private def getBackUrl(isPartner: Boolean, isSelfEmployed: Boolean): Call = {
    if (isPartner) { //Is employmentStatus == Self-Employed for partner
      routes.SelfEmployedOrApprenticeController.onPageLoad(true)
    } else {//Is employmentStatus == Self-Employed for parent
      routes.SelfEmployedOrApprenticeController.onPageLoad(false)
    }
  }

  private def defineSelfEmployed(isPartner: Boolean, pageObjects: PageObjects): Option[Boolean] = {
    if(isPartner) {
      if(pageObjects.household.partner.isDefined && pageObjects.household.partner.get.minimumEarnings.isDefined &&
        pageObjects.household.partner.get.minimumEarnings.get.selfEmployedIn12Months.isDefined ) {
        pageObjects.household.partner.get.minimumEarnings.get.selfEmployedIn12Months
      } else {
        None
      }
    } else {
      if(pageObjects.household.parent.minimumEarnings.isDefined &&
        pageObjects.household.parent.minimumEarnings.get.selfEmployedIn12Months.isDefined) {
        pageObjects.household.parent.minimumEarnings.get.selfEmployedIn12Months
      } else {
        None
      }
    }
  }

  def onPageLoad(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) =>
        Ok(
          selfEmployed(
            new SelfEmployedForm(isPartner, messagesApi).form.fill(defineSelfEmployed(isPartner, pageObjects)), isPartner,
              getBackUrl(isPartner, defineMinimumEarnings(isPartner, pageObjects).getOrElse(false))
          )
        )
      case _ =>
        Logger.warn("Invalid PageObjects in SelfEmployedController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from SelfEmployedController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit(isPartner: Boolean): Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects].flatMap {
      case Some(pageObjects) =>
        new SelfEmployedForm(isPartner, messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                selfEmployed(errors, isPartner, getBackUrl(isPartner, defineMinimumEarnings(isPartner, pageObjects).getOrElse(false)))
              )
            ),
          success => {
            val modifiedPageObjects = getModifiedPageObjects(success.get, pageObjects, isPartner)
            keystore.cache(modifiedPageObjects).map { result =>
              Redirect(getNextPage(modifiedPageObjects, isPartner))
            }
          }
        )
      case _ =>
        Logger.warn("Invalid PageObjects in SelfEmployedController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from SelfEmployedController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def getNextPage(pageObjects: PageObjects, isPartner: Boolean): Call = {
    val inPaidEmployment: YouPartnerBothEnum = defineInPaidEmployment(pageObjects)
    val earnMoreThanNMW = defineMinimumEarnings(isPartner, pageObjects).getOrElse(false)

    if(!isPartner) {
      if(!earnMoreThanNMW && inPaidEmployment == YouPartnerBothEnum.BOTH) {
        //TODO partner's self or apprentice page
        println(s"pageObjects in getModifiedPageObjects(!earnMoreThanNMW && inPaidEmployment)>>$pageObjects")
        routes.SelfEmployedOrApprenticeController.onPageLoad(true)
      } else if(earnMoreThanNMW && inPaidEmployment == YouPartnerBothEnum.BOTH) {
        //TODO partner's max earnings page
        println(s"pageObjects in getModifiedPageObjects(!earnMoreThanNMW && inPaidEmployment)>>$pageObjects")
        routes.ChildCareBaseController.underConstruction()
      } else {
        //TODO redirect to TC/UC page
        println(s"pageObjects in getModifiedPageObjects(earnMoreThanNMW && !inPaidEmployment)>>$pageObjects")
        routes.ChildCareBaseController.underConstruction()
      }
    } else {
      if(earnMoreThanNMW && inPaidEmployment == YouPartnerBothEnum.BOTH) {
        //TODO redirect to parent's max earnings page
        println(s"pageObjects in getModifiedPageObjects(inPaidEmployment)>>$pageObjects")
        routes.ChildCareBaseController.underConstruction()
      } else {
        //TODO redirect to TC/UC page
        println(s"pageObjects in getModifiedPageObjects(!inPaidEmployment)>>$pageObjects")
        routes.ChildCareBaseController.underConstruction()
      }
    }

  }

  private def getModifiedPageObjects(selfEmployed: Boolean, pageObjects: PageObjects, isPartner: Boolean): PageObjects = {
    if(!isPartner && (defineInPaidEmployment(pageObjects) == YouPartnerBothEnum.BOTH ||
      defineInPaidEmployment(pageObjects) == YouPartnerBothEnum.YOU)) {
      pageObjects.copy(household = pageObjects.household.copy(
        parent = pageObjects.household.parent.copy(
          minimumEarnings = Some(pageObjects.household.parent.minimumEarnings.fold(MinimumEarnings())(_.copy(
            selfEmployedIn12Months = Some(selfEmployed)))
          ))
      ))
    } else {
      pageObjects.copy(household = pageObjects.household.copy(
        partner = pageObjects.household.partner.map(x => x.copy(
          minimumEarnings = Some(x.minimumEarnings.fold(MinimumEarnings())(_.copy(
            selfEmployedIn12Months = Some(selfEmployed))))
          ))
      ))
    }

  }

}
