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

package uk.gov.hmrc.childcarecalculatorfrontend.models

import play.api.libs.json.{Reads, Writes, Format}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.EnumUtils

object LocationEnum extends Enumeration {
  type LocationEnum = Value
  val ENGLAND = Value("england")
  val SCOTLAND = Value("scotland")
  val WALES = Value("wales")
  val NORTHERNIRELAND = Value("northernIreland")

  val enumReads: Reads[LocationEnum] = EnumUtils.enumReads(LocationEnum)

  val enumWrites: Writes[LocationEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[LocationEnum] = EnumUtils.enumFormat(LocationEnum)
}

object YouPartnerBothEnum extends Enumeration {
  type YouPartnerBothEnum = Value

  val YOU = Value("you")
  val PARTNER = Value("partner")
  val BOTH = Value("both")

  val enumReads: Reads[YouPartnerBothEnum] = EnumUtils.enumReads(YouPartnerBothEnum)
  val enumWrites: Writes[YouPartnerBothEnum] = EnumUtils.enumWrites

  implicit def enumFormats: Format[YouPartnerBothEnum] = EnumUtils.enumFormat(YouPartnerBothEnum)
}

