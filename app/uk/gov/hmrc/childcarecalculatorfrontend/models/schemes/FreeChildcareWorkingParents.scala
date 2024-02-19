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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import javax.inject.Inject
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location.ENGLAND
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligibility, Eligible, NotDetermined, NotEligible}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig

class FreeChildcareWorkingParents @Inject() (tfc: TaxFreeChildcare, appConfig: FrontendAppConfig) extends Scheme {

  override def eligibility(answers: UserAnswers): Eligibility = {
    val hasChildrenInAgeGroups = {
      if(appConfig.allowFreeHoursFromNineMonths) {
        answers.isChildAgedNineTo23Months.getOrElse(false) || answers.isChildAgedTwo.getOrElse(false) || answers.isChildAgedThreeOrFour.getOrElse(false)
      }
      else answers.isChildAgedTwo.getOrElse(false) || answers.isChildAgedThreeOrFour.getOrElse(false)
    }
    val tfcEligibility = tfc.eligibility(answers)
    answers.location.map {
      case ENGLAND if hasChildrenInAgeGroups && tfcEligibility == Eligible =>
        Eligible
      case _ =>
        NotEligible
    }.getOrElse(NotDetermined)
  }
}
