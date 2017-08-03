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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildAgedThreeOrFourForm
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childAgedThreeOrFour
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

@Singleton
class ChildAgedThreeOrFourController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService

  private def getBackUrl()(implicit hc: HeaderCarrier): Future[Call] = {
    keystore.fetchEntryForSession[Boolean](childAgedTwoKey).map { childAgedTwo =>
      if(childAgedTwo.isDefined) {
        routes.ChildAgedTwoController.onPageLoad()
      }
      else {
        routes.LocationController.onPageLoad()
      }
    }
  }

  private def getLocation()(implicit hc: HeaderCarrier): Future[Option[String]] = {
    keystore.fetchEntryForSession[String](locationKey)
  }

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    {
      for {
        res <- keystore.fetchEntryForSession[Boolean](childAgedThreeOrFourKey)
        backUrl <- getBackUrl
        location <- getLocation
      } yield {

        Ok(
          childAgedThreeOrFour(
            new ChildAgedThreeOrFourForm(messagesApi).form.fill(res),
            backUrl,
            location.getOrElse("England")
          )
        )
      }
    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from ChildAgedThreeOrFourController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }

  def onSubmit: Action[AnyContent] = withSession { implicit request =>
    new ChildAgedThreeOrFourForm(messagesApi).form.bindFromRequest().fold(
      errors => {
        (for {
          backUrl <- getBackUrl
          location <- getLocation
        } yield {
          BadRequest(
            childAgedThreeOrFour(
              errors,
              backUrl,
              location.getOrElse("England")
            )
          )
        }).recover {
          case ex: Exception =>
            Logger.warn(s"Exception from ChildAgedThreeOrFourController.onSubmit.getBackUrl: ${ex.getMessage}")
            Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
        }
      },
      success => {
        keystore.cacheEntryForSession[Boolean](childAgedThreeOrFourKey, success.get).map {
          result =>
            Redirect(routes.ExpectChildcareCostsController.onPageLoad())
        } recover {
          case ex: Exception =>
            Logger.warn(s"Exception from ChildAgedThreeOrFourController.onSubmit: ${ex.getMessage}")
            Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
        }
      }
    )
  }
}
