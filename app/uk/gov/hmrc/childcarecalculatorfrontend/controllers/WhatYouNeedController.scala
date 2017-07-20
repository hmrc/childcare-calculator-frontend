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
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{AnyContent, Action}
import uk.gov.hmrc.play.frontend.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.views.html._
import scala.concurrent.Future

@Singleton
class WhatYouNeedController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with SessionProvider with FrontendController {

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    Future.successful(
      Ok(whatYouNeed())
    )
  }

}
