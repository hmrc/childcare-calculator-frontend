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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildAgedTwoForm
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childAgedTwo
import uk.gov.hmrc.play.http.HeaderCarrier

@Singleton
class ChildAgedTwoController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def getBackUrl(summary: Boolean)(implicit hc: HeaderCarrier): Call = {
    if(summary) {
      routes.FreeHoursResultsController.onPageLoad()
    } else {
      routes.LocationController.onPageLoad()
    }
  }

  def onPageLoad(summary: Boolean = false): Action[AnyContent] = withSession { implicit request =>
    {
      for {
        res <- keystore.fetchEntryForSession[Boolean](childAgedTwoKey)
        location <- keystore.fetchEntryForSession[String](locationKey)
      } yield {
        Ok(
          childAgedTwo(
            new ChildAgedTwoForm(messagesApi).form.fill(res),
            getBackUrl(summary),
            location.getOrElse("england")
          )
        )
      }
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from ChildAgedTwoController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    new ChildAgedTwoForm(messagesApi).form.bindFromRequest().fold(
      errors => {
        for (location <- keystore.fetchEntryForSession[String](locationKey)) yield {
          BadRequest(childAgedTwo(errors, getBackUrl(false),location.getOrElse("england")))
        }
      },
      success => {
        keystore.cacheEntryForSession(childAgedTwoKey, success.get).map {
          result =>
            Redirect(routes.ChildAgedThreeOrFourController.onPageLoad(false))
        } recover {
          case ex: Exception =>
            Logger.warn(s"Exception from ChildAgedTwoController.onSubmit: ${ex.getMessage}")
            Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
        }
      }
    )
  }
}
