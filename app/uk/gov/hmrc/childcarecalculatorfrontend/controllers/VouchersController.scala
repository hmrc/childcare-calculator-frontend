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
import play.api.mvc.{Call, AnyContent, Action}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.VouchersForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoUnsureEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YesNoUnsureEnum, YouPartnerBothEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.vouchers

import scala.concurrent.Future

@Singleton
class VouchersController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def getBackUrl(inPaidEmployment: YouPartnerBothEnum): Call = {
    routes.HoursController.onPageLoad(isPartner = (inPaidEmployment == YouPartnerBothEnum.PARTNER))
  }

  private def defineInPaidEmployment(pageObjects: PageObjects): YouPartnerBothEnum = {
    pageObjects.whichOfYouInPaidEmployment.getOrElse(YouPartnerBothEnum.YOU)
  }

  private def validatePageObjects(pageObjects: PageObjects): Boolean = {
    pageObjects.paidOrSelfEmployed.getOrElse(false) && (
      defineInPaidEmployment(pageObjects) == YouPartnerBothEnum.YOU || pageObjects.household.partner.isDefined
    )
  }

  def onPageLoad: Action[AnyContent] =  withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) if validatePageObjects(pageObjects) =>
        val inPaidEmployment: YouPartnerBothEnum = defineInPaidEmployment(pageObjects)
        Ok(
          vouchers(
            new VouchersForm(inPaidEmployment, messagesApi).form.fill(pageObjects.getVouchers.map(_.toString)),
            inPaidEmployment,
            getBackUrl(inPaidEmployment)
          )
        )
      case _ =>
        Logger.warn("PageObjects is invalid in VouchersController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }.recover {
      case ex: Exception =>
        Logger.warn(s"Exception from VouchersController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def getNextPage(inPaidEmployment: YouPartnerBothEnum, selectedVouchers: YesNoUnsureEnum): Call = {
    if(inPaidEmployment == YouPartnerBothEnum.BOTH && selectedVouchers == YesNoUnsureEnum.YES) {
      // redirect to 'Which of you is offered vouchers'
      routes.ChildCareBaseController.underConstruction()
    }
    else {
      // redirect to benefits page
      routes.GetBenefitsController.onPageLoad()
    }
  }

  private def modifyPageObject(pageObjects: PageObjects, selectedVouchers: YesNoUnsureEnum): PageObjects = {
    if(pageObjects.getVouchers == Some(selectedVouchers)) {
      pageObjects
    }
    else {
      val modified: PageObjects = pageObjects.copy(
        getVouchers = Some(selectedVouchers)
      )
      defineInPaidEmployment(pageObjects) match {
        case YouPartnerBothEnum.YOU => modified.copy(
          household = modified.household.copy(
            parent = modified.household.parent.copy(
              escVouchers = Some(selectedVouchers)
            )
          )
        )
        case YouPartnerBothEnum.PARTNER => modified.copy(
          household = modified.household.copy(
            partner = Some(
              modified.household.partner.get.copy(
                escVouchers = Some(selectedVouchers)
              )
            )
          )
        )
        case YouPartnerBothEnum.BOTH if (selectedVouchers == YesNoUnsureEnum.YES) =>
          // Next page will define exactly who gets vouchers
          modified
        case YouPartnerBothEnum.BOTH => modified.copy(
          household = modified.household.copy(
            parent = modified.household.parent.copy(
              escVouchers = Some(selectedVouchers)
            ),
            partner = Some(
              modified.household.partner.get.copy(
                escVouchers = Some(selectedVouchers)
              )
            )
          )
        )
      }
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) if validatePageObjects(pageObjects) =>
        val inPaidEmployment: YouPartnerBothEnum = defineInPaidEmployment(pageObjects)
        new VouchersForm(inPaidEmployment, messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                vouchers(
                  errors,
                  inPaidEmployment,
                  getBackUrl(inPaidEmployment)
                )
              )
            ),
          success => {
            val selectedVouchers: YesNoUnsureEnum = YesNoUnsureEnum.withName(success.get)
            val modifiedPageObjects = modifyPageObject(pageObjects, selectedVouchers)
            keystore.cache(modifiedPageObjects).map {
              result =>
                Redirect(
                  getNextPage(inPaidEmployment, selectedVouchers)
                )
            }
          }
        )
      case _ =>
        Logger.warn("PageObjects is invalid in VouchersController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from VouchersController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

}
