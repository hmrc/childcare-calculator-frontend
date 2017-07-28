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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Call, Action, AnyContent}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ExpectChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.expectChildcareCosts
import uk.gov.hmrc.play.http.HeaderCarrier
import scala.concurrent.Future

@Singleton
class ExpectChildcareCostsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetchEntryForSession[Boolean](expectChildcareCostsKey).map {
      res =>
        Ok(
          expectChildcareCosts(new ExpectChildcareCostsForm(messagesApi).form.fill(res))
        )
    } recover {
      case e : Exception =>
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  private def getNextPage(hasExpectedChildcareCost: Boolean)(implicit hc: HeaderCarrier): Future[Call] = {
    keystore.fetchEntryForSession[Boolean](childAgedTwoKey).flatMap { hasChildAgedTwo =>
      keystore.fetchEntryForSession[Boolean](childAgedThreeOrFourKey).map { hasChildAgedThreeOrFour =>
        if(hasExpectedChildcareCost && !hasChildAgedTwo.getOrElse(false) && !hasChildAgedThreeOrFour.getOrElse(false)) {
          routes.LivingWithPartnerController.onPageLoad
        }
        else {
          routes.FreeHoursResultsController.onPageLoad
        }
      }
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    new ExpectChildcareCostsForm(messagesApi).form.bindFromRequest().fold(
      errors => {
        Future(BadRequest(expectChildcareCosts(errors)))
      },
      success => {
        val hasExpectedChildcareCost: Boolean = success.get
        keystore.cacheEntryForSession(expectChildcareCostsKey, hasExpectedChildcareCost).flatMap { result =>
          getNextPage(hasExpectedChildcareCost).map { nextPage =>
            Redirect(nextPage)
          }
        } recover {
          case e: Exception =>
            Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
        }
      }
    )
  }

}