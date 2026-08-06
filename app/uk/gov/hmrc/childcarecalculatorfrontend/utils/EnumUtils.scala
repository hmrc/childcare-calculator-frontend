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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import play.api.Logging
import play.api.libs.json.*

import scala.language.implicitConversions

object EnumUtils extends Logging {

  def enumReads[E <: Enumeration](enumObject: E): Reads[enumObject.Value] = {
    case JsString(s) =>
      try
        JsSuccess(enumObject.withName(s))
      catch {
        case _: NoSuchElementException =>
          logger.warn(
            s"EnumUtils.enumReads - Enumeration expected of type: '${enumObject.getClass}', but it does not appear to contain the value: '$s'"
          )
          JsError(
            s"Enumeration expected of type: '${enumObject.getClass}', but it does not appear to contain the value: '$s'"
          )
      }
    case _ =>
      logger.warn("EnumUtils.enumReads - String value expected")
      JsError("String value expected")
  }

  implicit def enumFormat[E <: Enumeration](enumObject: E): Format[enumObject.Value] =
    Format(enumReads(enumObject), enumWrites(enumObject))

  implicit def enumWrites[E <: Enumeration](enumObject: E): Writes[enumObject.Value] = v => JsString(v.toString)

}
