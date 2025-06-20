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

package uk.gov.hmrc.childcarecalculatorfrontend.models.integration

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.childcarecalculatorfrontend.models.AgeEnum.AgeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentStatusEnum.EmploymentStatusEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefits
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoUnsureEnum.YesNoUnsureEnum

case class Income(
    employmentIncome: Option[BigDecimal] = None,
    pension: Option[BigDecimal] = None,
    otherIncome: Option[BigDecimal] = None,
    benefits: Option[BigDecimal] = None,
    taxCode: Option[String] = None
)

object Income {
  implicit val formatIncome: OFormat[Income] = Json.format[Income]
}

case class MinimumEarnings(
    amount: BigDecimal = 0.00,
    employmentStatus: Option[EmploymentStatusEnum] = None,
    selfEmployedIn12Months: Option[Boolean] = None
)

object MinimumEarnings {
  implicit val formatMinimumEarnings: OFormat[MinimumEarnings] = Json.format[MinimumEarnings]
}

case class Claimant(
    ageRange: Option[AgeEnum] = None,
    benefits: Set[ParentsBenefits] = Set.empty,
    lastYearlyIncome: Option[Income] = None,
    currentYearlyIncome: Option[Income] = None,
    hours: Option[BigDecimal] = None,
    minimumEarnings: Option[MinimumEarnings] = None,
    escVouchers: Option[YesNoUnsureEnum] = None,
    maximumEarnings: Option[Boolean] = None
)

object Claimant {
  implicit val formatClaimant: OFormat[Claimant] = Json.format[Claimant]
}
