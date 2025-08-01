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

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildcarePayFrequency
import uk.gov.hmrc.childcarecalculatorfrontend.utils.InputOption

object ChildcarePayFrequencyForm extends FormErrorHelper {

  def apply(name: String): Form[ChildcarePayFrequency.Value] =
    Form(single("value" -> of(ChildcarePayFrequencyFormatter(name))))

  lazy val options: Seq[InputOption] = Seq(
    payFrequencyInputOption(ChildcarePayFrequency.WEEKLY_KEY, ChildcarePayFrequency.WEEKLY.toString),
    payFrequencyInputOption(ChildcarePayFrequency.MONTHLY_KEY, ChildcarePayFrequency.MONTHLY.toString)
  )

  private def payFrequencyInputOption(id: String, option: String): InputOption =
    new InputOption(
      id = id,
      value = option,
      messageKey = s"childcarePayFrequency.$option"
    )

  private def ChildcarePayFrequencyFormatter(name: String) = new Formatter[ChildcarePayFrequency.Value] {
    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(ChildcarePayFrequency.withName(s))
      case None                        => produceError(key, "childcarePayFrequency.error.notCompleted", name)
      case _                           => produceError(key, "error.unknown")
    }

    def unbind(key: String, value: ChildcarePayFrequency.Value) = Map(key -> value.toString)
  }

  private def optionIsValid(value: String) = options.exists(o => o.value == value)
}
