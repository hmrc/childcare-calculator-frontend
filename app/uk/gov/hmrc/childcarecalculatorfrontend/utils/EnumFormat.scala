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

trait EnumFormat[E] extends Logging {

  protected def values: Array[E]

  private lazy val lookupMap: Map[String, E] =
    (
      values.map(value => value.toString -> value) ++
        values
          .flatMap {
            // Also use the enum value's name if toString is overridden
            case value: Product => Some(value.productPrefix -> value)
            case _              => None
          }
    ).toMap

  def withName(string: String): Option[E] = lookupMap.get(string)

  given reads: Reads[E] = {
    case JsString(s) =>
      withName(s) match {
        case Some(value) => JsSuccess(value)
        case None =>
          logger.warn(
            s"EnumUtils.enumReads - Enumeration expected of type: '$getClass', but it does not appear to contain the value: '$s'"
          )
          JsError(
            s"Enumeration expected of type: '$getClass', but it does not appear to contain the value: '$s'"
          )
      }
    case other =>
      logger.warn(
        s"EnumUtils.enumReads - String value expected for type '$getClass', got value of type ${other.getClass}"
      )
      JsError("String value expected")
  }

  given writes: Writes[E] = value => JsString(value.toString)

  given Format[E] = Format(reads, writes)

  given EnumFormat[E] = this

}
