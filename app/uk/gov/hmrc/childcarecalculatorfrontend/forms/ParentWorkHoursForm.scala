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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import javax.inject.{Inject, Singleton}

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

@Singleton
class ParentWorkHoursForm @Inject() (appConfig: FrontendAppConfig) extends FormErrorHelper {

  def parentWorkHoursFormatter(errorKeyBlank: String, errorKeyInvalid: String) = new Formatter[BigDecimal] {

    val minValue: Double = appConfig.minWorkingHours
    val maxValue: Double = appConfig.maxWorkingHours
    val decimalRegex: String = "0*[0-9]{1,2}(\\.[0-9])?".r.toString()

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], BigDecimal] = {
      data.get(key) match {

        case None => produceError(key, errorKeyBlank)

        case Some("") => produceError(key, errorKeyBlank)

        case Some(strValue) if strValue.matches(decimalRegex) =>
          val value = BigDecimal(strValue)

          if (validateInRange(value, minValue, maxValue)) {
            Right(value)
          } else {
            produceError(key, errorKeyBlank)
          }

        case _ =>
          produceError(key, errorKeyInvalid)
      }
    }

    def unbind(key: String, value: BigDecimal) = Map(key -> value.toString)
  }

  def apply(errorKeyBlank: String = parentWorkHoursBlankErrorKey, errorKeyInvalid: String = parentWorkHoursInvalidErrorKey): Form[BigDecimal] =
    Form("value" -> of(parentWorkHoursFormatter(errorKeyBlank, errorKeyInvalid)))
}
