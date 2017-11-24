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

package uk.gov.hmrc.childcarecalculatorfrontend

import org.joda.time.LocalDate
import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, ChildcarePayFrequency, DisabilityBenefits}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.SubCascadeUpsert
import uk.gov.hmrc.http.cache.client.CacheMap

case class DataGenerator(sample: CacheMap) extends SubCascadeUpsert{
  def overWriteObject(objectName: String, properties: JsValue) : DataGenerator = {
    DataGenerator(sample.copy(data = sample.data + (objectName -> properties)))
  }
}

object DataGenerator {
  val over19 = LocalDate.now.minusYears(19).minusDays(1)
  val over16 = LocalDate.now.minusYears(16).minusDays(1)
  val exact15 = LocalDate.now.minusYears(15).plusMonths(1)
  val under16 = LocalDate.now
  val childStartEducationDate = new LocalDate(2017, 2, 1)

  lazy val disabilityBenefits: String = DisabilityBenefits.DISABILITY_BENEFITS.toString
  lazy val higherRateDisabilityBenefits: String = DisabilityBenefits.HIGHER_DISABILITY_BENEFITS.toString

  lazy val weekly: String = ChildcarePayFrequency.WEEKLY.toString
  lazy val monthly: String = ChildcarePayFrequency.MONTHLY.toString

  val sample = new CacheMap("id", Map(
    NoOfChildrenId.toString -> JsNumber(5),
    AboutYourChildId.toString -> Json.obj(
      "0" -> Json.toJson(AboutYourChild("Foo", over19)),
      "1" -> Json.toJson(AboutYourChild("Bar", over16)),
      "2" -> Json.toJson(AboutYourChild("Quux", exact15)),
      "3" -> Json.toJson(AboutYourChild("Baz", under16)),
      "4" -> Json.toJson(AboutYourChild("Raz", under16))),
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