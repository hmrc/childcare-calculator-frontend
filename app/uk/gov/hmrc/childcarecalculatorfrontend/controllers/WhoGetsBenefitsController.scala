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

import javax.inject.{Singleton, Inject}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Call, AnyContent, Action}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoGetsBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoGetsBenefits

import scala.concurrent.Future

@Singleton
class WhoGetsBenefitsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService


  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObject)  =>
        val selection: Option[YouPartnerBothEnum] = getSelection(pageObject.household.parent.benefits,
                                                                 pageObject.household.partner.fold[Option[Benefits]](None)(_.benefits))

        Ok(whoGetsBenefits(new WhoGetsBenefitsForm(messagesApi).form.fill(selection.map(_.toString))))

      case _ =>
        Logger.warn("Invalid PageObjects in WhoGetsBenefitsController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }.recover {
      case ex: Exception =>
        Logger.warn(s"Exception from WhoGetsBenefitsController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }


  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    new WhoGetsBenefitsForm(messagesApi).form.bindFromRequest().fold(
      errors => {
        Future(
          BadRequest(
            whoGetsBenefits(errors)
          )
        )
      },
      success => {
        keystore.fetch[PageObjects]().flatMap {
          case Some(pageObject) =>
            val selectedWhoGetsBenefits: YouPartnerBothEnum = YouPartnerBothEnum.withName(success.get)
            val modifiedPageObject: PageObjects = modifyPageObject(pageObject, selectedWhoGetsBenefits)
            keystore.cache(modifiedPageObject).map { result =>
              Redirect(getNextPage(selectedWhoGetsBenefits))
            }
          case _ =>
            Logger.warn("Invalid PageObjects in WhoGetsBenefitsController.onSubmit")
            Future(Redirect(routes.ChildCareBaseController.onTechnicalDifficulties()))
        }.recover {
          case ex: Exception =>
            Logger.warn(s"Exception from WhoGetsBenefitsController.onSubmit: ${ex.getMessage}")
            Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
        }
      }
    )
  }

  private def modifyPageObject(pageObject: PageObjects,
                               selectedWhoGetsBenefits: YouPartnerBothEnum): PageObjects = {

    if(getSelection(pageObject.household.parent.benefits,
                    pageObject.household.partner.fold[Option[Benefits]](None)(_.benefits)).contains(selectedWhoGetsBenefits)) {
      pageObject
    }
    else {
      selectedWhoGetsBenefits match {
        case YouPartnerBothEnum.YOU => pageObject.copy(
          household = pageObject.household.copy(
            parent = pageObject.household.parent.copy(
              benefits = Some(pageObject.household.parent.benefits.getOrElse(Benefits()))
            ),
            partner = Some(
              pageObject.household.partner.fold(Claimant())(_.copy(benefits = None)
              )
            )
          )
        )

        case YouPartnerBothEnum.PARTNER => pageObject.copy(
          household = pageObject.household.copy(
            parent = pageObject.household.parent.copy(benefits = None),
            partner = Some(
              pageObject.household.partner.fold(Claimant(benefits = Some(Benefits())))( x => x.copy(
                benefits = Some(x.benefits.getOrElse(Benefits()))
              )
            )
          )
        ))

        case YouPartnerBothEnum.BOTH => pageObject.copy(
          household = pageObject.household.copy(
            parent = pageObject.household.parent.copy(
              benefits = Some(pageObject.household.parent.benefits.getOrElse(Benefits()))
            ),
            partner = Some(
              pageObject.household.partner.fold(Claimant(benefits = Some(Benefits())))( x => x.copy(
                benefits = Some(x.benefits.getOrElse(Benefits()))
              )
              )
            )
          )
        )
      }
    }
  }

  private def getSelection(parentBenefits: Option[Benefits],
                           partnerBenefits: Option[Benefits]): Option[YouPartnerBothEnum] = {
    (parentBenefits, partnerBenefits) match {
      case (None, None) => None
      case (Some(_), None) => Some(YouPartnerBothEnum.YOU)
      case (None, Some(_)) => Some(YouPartnerBothEnum.PARTNER)
      case (Some(_), Some(_)) => Some(YouPartnerBothEnum.BOTH)
    }
  }


  private def getNextPage(selectedWhoGetsBenefits: YouPartnerBothEnum): Call = {
    routes.WhichBenefitsDoYouGetController.onPageLoad(selectedWhoGetsBenefits == YouPartnerBothEnum.PARTNER)
  }

}
