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

import java.time.LocalDate
import play.api.libs.json._

import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, ChildcarePayFrequency, DisabilityBenefits}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SubCascadeUpsert, CacheMap}

case class DataGenerator(sample: CacheMap) extends SubCascadeUpsert{
  def overWriteObject(objectName: String, properties: JsValue) : DataGenerator = {
    DataGenerator(sample.copy(data = sample.data + (objectName -> properties)))
  }

  def deleteObject(objectName: String) : DataGenerator = {
    DataGenerator(sample.copy(data = sample.data - objectName))
  }
}

object DataGenerator {
  val ageOf19YearsAgo: LocalDate => LocalDate = (date : LocalDate) =>
    date.minusYears(19).minusDays(1)
  val ageOf16WithBirthdayBefore31stAugust: LocalDate => LocalDate = (date : LocalDate) =>
    if (date.getMonthValue > 8) {
      LocalDate.parse(s"${date.minusYears(16).getYear}-07-31")
    } else {
      date.minusYears(16)
    }
  val ageOfOver16Relative: LocalDate => LocalDate = (date : LocalDate) =>
    if (date.getMonthValue <= 8) {
      date.minusYears(17)
    } else {
      date.minusYears(16)
    }
  val ageUnder16Relative: LocalDate => LocalDate = (date : LocalDate) =>
    date.minusYears(1)
  val ageExactly16Relative: LocalDate => LocalDate = (date : LocalDate) =>
    LocalDate.of(date.minusYears(16).getYear, 6, 1)
  val ageExactly15Relative: LocalDate => LocalDate = (date : LocalDate) =>
    LocalDate.of(date.minusYears(15).getYear, 6, 1)

  private val childStartEducationDate = LocalDate.of(2017, 2, 1)

  lazy val disabilityBenefits: String = DisabilityBenefits.DISABILITY_BENEFITS.toString
  lazy val higherRateDisabilityBenefits: String = DisabilityBenefits.HIGHER_DISABILITY_BENEFITS.toString

  lazy val weekly: String = ChildcarePayFrequency.WEEKLY.toString
  lazy val monthly: String = ChildcarePayFrequency.MONTHLY.toString

  private val sampleDate = LocalDate.parse("2019-01-01")

  val sample = new CacheMap("id", Map(
    NoOfChildrenId.toString -> JsNumber(5),
    AboutYourChildId.toString -> Json.obj(
      "0" -> Json.toJson(AboutYourChild("Foo", sampleDate)),
      "1" -> Json.toJson(AboutYourChild("Bar", sampleDate)),
      "2" -> Json.toJson(AboutYourChild("Quux", sampleDate)),
      "3" -> Json.toJson(AboutYourChild("Baz", sampleDate)),
      "4" -> Json.toJson(AboutYourChild("Raz", sampleDate))
    ),
    ChildApprovedEducationId.toString -> Json.obj(
      "0" -> true,
      "1" -> true
    ),
    ChildStartEducationId.toString -> Json.obj(
      "0" -> childStartEducationDate
    ),
    ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
    WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
    WhichDisabilityBenefitsId.toString -> Json.obj(
      "0" -> Seq(disabilityBenefits),
      "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
    ),
    RegisteredBlindId.toString -> JsBoolean(true),
    WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
    WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
    ChildcarePayFrequencyId.toString -> Json.obj(
      "0" -> monthly,
      "2" -> weekly
    ),
    ExpectedChildcareCostsId.toString -> Json.obj(
      "3" -> JsNumber(123),
      "4" -> JsNumber(224))
  ))

  def apply() :DataGenerator = DataGenerator(sample)
}