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

package uk.gov.hmrc.childcarecalculatorfrontend.models

import play.api.libs.json.{Format, JsString, Reads, Writes}
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildAgeGroup._

trait ChildAgeGroup {
  override def toString: String = inverseMappping(this)
}

case object NineTo23Months extends ChildAgeGroup
case object TwoYears       extends ChildAgeGroup
case object ThreeYears     extends ChildAgeGroup
case object FourYears      extends ChildAgeGroup
case object NoneOfThese    extends ChildAgeGroup

object ChildAgeGroup {
  val nineTo23Months = "nineTo23Months"
  val twoYears       = "twoYears"
  val threeYears     = "threeYears"
  val fourYears      = "fourYears"
  val noneOfThese    = "noneOfThese"

  val mapping: Map[String, ChildAgeGroup] = Map(
    nineTo23Months -> NineTo23Months,
    twoYears       -> TwoYears,
    threeYears     -> ThreeYears,
    fourYears      -> FourYears,
    noneOfThese    -> NoneOfThese
  )

  val inverseMappping: Map[ChildAgeGroup, String] = mapping.map(_.swap)

  private val reads: Reads[ChildAgeGroup] = Reads(json => json.validate[String].map(mapping))

  private val writes: Writes[ChildAgeGroup] = Writes(childAgeGroup => JsString(inverseMappping(childAgeGroup)))

  implicit val format: Format[ChildAgeGroup] = Format(reads, writes)
}
