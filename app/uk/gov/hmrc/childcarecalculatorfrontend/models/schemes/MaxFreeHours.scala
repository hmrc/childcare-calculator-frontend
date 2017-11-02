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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import javax.inject.Inject

import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class MaxFreeHours @Inject() (freeHours: FreeHours) extends Scheme {

  override def eligibility(answers: UserAnswers): Eligibility = {
    if (freeHours.eligibility(answers) == NotEligible) {
      NotEligible
    } else {
      (for {
        location <- answers.location
      } yield if (location.contains(LocationEnum.ENGLAND.toString)) {
        NotDetermined
      } else {
        NotEligible
      }).getOrElse(NotDetermined)
    }
  }
}
