/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import play.api.Logging
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes

object SessionExpiredRouter extends Logging {

  def route[A](area: String, message: String, answers: Option[UserAnswers] = None, uri : String = "N/A", session: String = "N/A") = {

    val isCacheMapAvailable = answers.fold("No")(c => if (c.cacheMap == null) "No" else "")

    logger.warn(s"ChildcareCalculatorSessionExpired - ${area} - ${uri} - ${message} - ${isCacheMapAvailable} cachemap available - ${session}")
    routes.SessionExpiredController.onPageLoad
  }
}
