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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildcarePayFrequency

object ExpectedChildcareCostsForm extends FormErrorHelper {

  private def expectedChildcareCostsFormatter(frequency: ChildcarePayFrequency.Value) = new Formatter[BigDecimal] {

    val decimalRegex = """\d+(\.\d{1,2})?"""

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], BigDecimal] = {
      data.get(key) match {
        case None => produceError(key, "expectedChildcareCosts.error", frequency)
        case Some("") => produceError(key, "expectedChildcareCosts.error", frequency)
        case Some(s) if s.matches(decimalRegex) => Right(BigDecimal(s))
        case _ => produceError(key, "expectedChildcareCosts.invalid")
      }
    }

    def unbind(key: String, value: BigDecimal) = Map(key -> value.toString)
  }

  def apply(frequency: ChildcarePayFrequency.Value): Form[BigDecimal] =
    Form(
      "value" -> of(expectedChildcareCostsFormatter(frequency))
        .verifying("expectedChildcareCosts.invalid", _ >= 1.0)
        .verifying("expectedChildcareCosts.invalid", _ <= 999.99)
    )
}
