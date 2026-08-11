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

package uk.gov.hmrc.childcarecalculatorfrontend

import uk.gov.hmrc.childcarecalculatorfrontend.helpers.{CacheKeyOps, CacheMapOps}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{ChildcarePayFrequency, DisabilityBenefit}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

import java.time.LocalDate

object DataGenerator extends CacheMapOps with CacheKeyOps {
  val ageOf19YearsAgo: LocalDate => LocalDate = (date: LocalDate) => date.minusYears(19).minusDays(1)

  val ageOf16WithBirthdayBefore31stAugust: LocalDate => LocalDate = (date: LocalDate) =>
    if (date.getMonthValue > 8) {
      LocalDate.parse(s"${date.minusYears(16).getYear}-07-31")
    } else {
      date.minusYears(16)
    }

  val ageOfOver16Relative: LocalDate => LocalDate = (date: LocalDate) =>
    if (date.getMonthValue <= 8) {
      date.minusYears(17)
    } else {
      date.minusYears(16).minusDays(1)
    }

  val ageUnder16Relative: LocalDate => LocalDate = (date: LocalDate) => date.minusYears(1)

  val ageExactly16Relative: LocalDate => LocalDate = (date: LocalDate) =>
    LocalDate.of(date.minusYears(16).getYear, 6, 1)

  val ageExactly15Relative: LocalDate => LocalDate = (date: LocalDate) =>
    LocalDate.of(date.minusYears(15).getYear, 6, 1)

  private val sampleDate = LocalDate.parse("2019-01-01")

  val sample: CacheMap = CacheMap.of(
    NoOfChildrenId.withValue(5),
    AboutYourChildId.withValue(
      Map(
        0 -> AboutYourChild("Foo", sampleDate),
        1 -> AboutYourChild("Bar", sampleDate),
        2 -> AboutYourChild("Quux", sampleDate),
        3 -> AboutYourChild("Baz", sampleDate),
        4 -> AboutYourChild("Raz", sampleDate)
      )
    ),
    ChildrenDisabilityBenefitsId.withValue(true),
    WhichChildrenDisabilityId.withValue(Set(0, 2)),
    WhichDisabilityBenefitsId.withValue(
      Map(
        0 -> Set(DisabilityBenefit.DisabilityBenefits),
        2 -> Set(DisabilityBenefit.DisabilityBenefits, DisabilityBenefit.HigherDisabilityBenefits)
      )
    ),
    RegisteredBlindId.withValue(true),
    WhichChildrenBlindId.withValue(Set(2)),
    WhoHasChildcareCostsId.withValue(Set(0, 2)),
    ChildcarePayFrequencyId.withValue(
      Map(0 -> ChildcarePayFrequency.Monthly, 2 -> ChildcarePayFrequency.Weekly)
    ),
    ExpectedChildcareCostsId.withValue(Map(3 -> BigDecimal(123), 4 -> BigDecimal(224)))
  )

}
