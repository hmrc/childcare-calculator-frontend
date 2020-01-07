/*
 * Copyright 2020 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter

trait IncomeFormatter extends FormErrorHelper {

  val minValue: Double
  val maxValue: Double
  override val decimalRegex = """\d+(\.\d{1,2})?""".r.toString()

  val errorKeyBlank: String
  val errorKeyInvalid: String

  protected def formatter(errorKeyBlank: String, errorKeyInvalid: String) = new Formatter[BigDecimal] {

    def bind(key: String, data: Map[String, String]) = {
      data.get(key) match {
        case None => produceError(key, errorKeyBlank)
        case Some("") => produceError(key, errorKeyBlank)
        case Some(strValue) if strValue.matches(decimalRegex) =>
          val value = BigDecimal(strValue)
          if (value < minValue) {
            produceError(key, errorKeyInvalid)
          } else {
            Right(value)
          }
        case _ => produceError(key, errorKeyInvalid)
      }
    }

    def unbind(key: String, value: BigDecimal) = Map(key -> value.toString)
  }

  def apply(errorKeyBlank: String = errorKeyBlank, errorKeyInvalid: String = errorKeyInvalid): Form[BigDecimal] =
    Form(single("value" -> of(formatter(errorKeyBlank, errorKeyInvalid))))
}
