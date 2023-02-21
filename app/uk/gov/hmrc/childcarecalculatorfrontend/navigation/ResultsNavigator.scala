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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.Identifier
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

trait ResultsNavigator extends SubNavigator {

  protected def schemes: Schemes
  protected def resultLocation: Call

  protected def resultsMap: PartialFunction[Identifier, UserAnswers => Call] = Map.empty

  override def nextPage(id: Identifier, mode: Mode): Option[UserAnswers => Call] = {
    resultsMap.lift(id).map {
      route =>
        (userAnswers: UserAnswers) =>
          if (schemes.allSchemesDetermined(userAnswers)) {
            resultLocation
          } else {
            route(userAnswers)
          }
    } orElse super.nextPage(id, mode)
  }
}
