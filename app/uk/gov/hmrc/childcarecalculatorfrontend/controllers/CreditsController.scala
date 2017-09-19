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
import play.api.mvc.{Action, AnyContent, Result}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.CreditsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{CreditsEnum, Household, LocationEnum, PageObjects}
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.credits
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class CreditsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map { pageObjects =>
      Ok(credits(new CreditsForm(messagesApi).form.fill(pageObjects.map(_.household.location.toString))))
    }.recover {
      case ex: Exception =>
        Logger.warn(s"Exception from CreditsController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def getModifiedPageObjects(pageObjects: Option[PageObjects], selectedCredits: String): PageObjects = {
    pageObjects match {
      case Some(po) =>
//        val modifiedChildAgedTwo = if(selectedCredits == CreditsEnum.TAXCREDITS.toString) {
//          None
//        } else {
//          po.childAgedTwo
//        }
//
//        val modifiedHousehold = po.household.copy(credits = Some(CreditsEnum.withName(selectedCredits)))
//
//        po.copy(household = modifiedHousehold, childAgedTwo = modifiedChildAgedTwo)
        po

      case _ =>
        PageObjects(
          household = Household(credits = Some(CreditsEnum.withName(selectedCredits)),
            location = LocationEnum.SCOTLAND)
        )
    }
  }

  private def saveAndGoToNextPage(pageObjects: Option[PageObjects], selectedCredits: String)(implicit hc: HeaderCarrier): Future[Result] = {
    val modifiedPageObjects: PageObjects = getModifiedPageObjects(pageObjects, selectedCredits)

    keystore.cache(modifiedPageObjects).map { res =>
      if (selectedCredits == CreditsEnum.TAXCREDITS.toString) {
        Redirect(routes.ChildAgedThreeOrFourController.onPageLoad(false))
      } else if (selectedCredits == CreditsEnum.UNIVERSALCREDIT.toString) {
        Redirect(routes.ChildAgedTwoController.onPageLoad(false))
      } else {
        Redirect(routes.ChildAgedTwoController.onPageLoad(false))
      }
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    new CreditsForm(messagesApi).form.bindFromRequest().fold(
      errors => {
        Future(BadRequest(credits(errors)))
      },
      success => {
        keystore.fetch[PageObjects]().flatMap { pageObjects =>
          saveAndGoToNextPage(pageObjects, success.get)
        } recover {
          case ex: Exception =>
            Logger.warn(s"Exception from CreditsController.onSubmit: ${ex.getMessage}")
            Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
        }
      }
    )
  }

}
