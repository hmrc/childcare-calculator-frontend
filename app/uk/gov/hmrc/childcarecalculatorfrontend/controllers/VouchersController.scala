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
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YouPartnerBothEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.vouchers

@Singleton
class VouchersController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  def getBackUrl(inPaidEmployment: YouPartnerBothEnum): Call = {
    routes.HoursController.onPageLoad(isPartner = (inPaidEmployment == YouPartnerBothEnum.PARTNER))
  }

  def onPageLoad: Action[AnyContent] =  withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) if pageObjects.paidOrSelfEmployed.getOrElse(false) =>
        val inPaidEmployment: YouPartnerBothEnum = pageObjects.whichOfYouInPaidEmployment.getOrElse(YouPartnerBothEnum.YOU)
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

  // TODO: Implement logic
  def onSubmit: Action[AnyContent] = ???
}
