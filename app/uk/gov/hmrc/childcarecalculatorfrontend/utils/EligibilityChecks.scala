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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligibility, Eligible, NotDetermined, NotEligible}

trait EligibilityChecks {
  self: UserAnswers =>

  def isEligibleForFreeHours: Eligibility = {

    if (childAgedThreeOrFour.getOrElse(false)) {
      Eligible
    } else if (!location.contains("northernIreland") && childAgedTwo.getOrElse(false)) {
      Eligible
    } else if(hasAnsweredChildAge2Or3Or4 && hasNoChildren) {
      NotEligible
    } else if (location.contains("northernIreland") && !childAgedThreeOrFour.getOrElse(false)) {
      NotEligible
    } else {
      NotDetermined
    }
  }

  private def hasAnsweredChildAge2Or3Or4: Boolean =
    childAgedThreeOrFour.isDefined || childAgedTwo.isDefined

  private def hasNoChildren: Boolean =
    childAgedThreeOrFour.contains(false) && childAgedTwo.contains(false)

  def isEligibleForMaxFreeHours: Eligibility = {
    if (isEligibleForFreeHours == Eligible &&
      location.contains("england") &&
      childAgedThreeOrFour.contains(true)) {
        Eligible
    } else {
      NotEligible
    }
  }
}
