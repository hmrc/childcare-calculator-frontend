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
import play.api.mvc.{AnyContent, Action}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoGetsVouchersForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YesNoUnsureEnum, YouPartnerBothEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoGetsVouchers

import scala.concurrent.Future

/**
 * Created by user on 31/08/17.
 */
@Singleton
class WhoGetsVouchersController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def validatePageObjects(pageObjects: PageObjects): Boolean = {
    pageObjects.livingWithPartner.isDefined
  }

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) if validatePageObjects(pageObjects) =>
        Ok(whoGetsVouchers(
          new WhoGetsVouchersForm(messagesApi).form.fill(pageObjects.whoGetsVouchers.map(_.toString))
        )
      )
      case _ =>
        Logger.warn("Invalid PageObjects in WhoGetsVouchersController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }.recover {
      case ex: Exception =>
        Logger.warn(s"Exception from WhoGetsVouchersController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def modifyPageObject(oldPageObject: PageObjects, newWhoGetsVouchers: String): PageObjects = {
    val gettingVouchers: YouPartnerBothEnum = YouPartnerBothEnum.withName(newWhoGetsVouchers)
    if(oldPageObject.whoGetsVouchers == Some(gettingVouchers)) {
      oldPageObject
    }
    else {
      val modified = oldPageObject.copy(
        whoGetsVouchers = Some(gettingVouchers),
        household = oldPageObject.household.copy(
          parent = oldPageObject.household.parent.copy(
            escVouchers = Some(YesNoUnsureEnum.YES)
          ),
          partner = Some(
            oldPageObject.household.partner.get.copy(
              escVouchers = Some(YesNoUnsureEnum.YES)
            )
          )
        )
      )
      gettingVouchers match {
        case YouPartnerBothEnum.PARTNER => modified.copy(
          household = modified.household.copy(
            partner = Some(
              modified.household.partner.get.copy(
                escVouchers = Some(YesNoUnsureEnum.YES)
              )
            ),
            parent = modified.household.parent.copy(
              escVouchers = None
            )
          )
        )
        case YouPartnerBothEnum.YOU => modified.copy(
          household = modified.household.copy(
            parent = modified.household.parent.copy(
              escVouchers = Some(YesNoUnsureEnum.YES)
            ),
            partner = Some(
              modified.household.partner.get.copy(
                escVouchers = None
              )
            )
          )
        )
        case YouPartnerBothEnum.BOTH => modified
      }
    }
  }
  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().flatMap {
      case Some(pageObjects) if validatePageObjects(pageObjects) =>
        new WhoGetsVouchersForm(messagesApi).form.bindFromRequest().fold(
          errors =>
            Future(
              BadRequest(
                whoGetsVouchers(errors)
              )
            ),
          success => {
            val modifiedPageObjects = modifyPageObject(pageObjects, success.get)
            keystore.cache(modifiedPageObjects).map {
              result =>
                Redirect(
                  routes.GetBenefitsController.onPageLoad()
                )
            }
          }
        )
      case _ =>
        Logger.warn("Invalid PageObjects in WhoGetsVouchersController.onSubmit")
        Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from WhoGetsVouchersController.onSubmit: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

}
