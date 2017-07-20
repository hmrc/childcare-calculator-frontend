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

import java.util.UUID

import play.api.mvc._
import uk.gov.hmrc.play.http.SessionKeys
import scala.concurrent.Future

trait SessionProvider {

  def withSession(f: => Request[AnyContent] => Future[Result]): Action[AnyContent] = {
    Action.async {
      implicit request: Request[AnyContent] =>
        getSessionId match {
          case None =>
            Future.successful(Results.Redirect(routes.ChildCareBaseController.onPageLoad()).withSession(generateSession()))
          case _ =>
            f(request)
        }
    }
  }

  def getSessionId()(implicit request: Request[AnyContent]): Option[String] = request.session.get(SessionKeys.sessionId)

  def generateSession()(implicit request: Request[AnyContent]): Session = {
    Session(request.session.data ++ Map(SessionKeys.sessionId -> s"session-${UUID.randomUUID}"))
  }

}
