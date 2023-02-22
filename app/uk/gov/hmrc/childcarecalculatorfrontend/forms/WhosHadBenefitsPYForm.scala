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
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.InputOption

object WhosHadBenefitsPYForm extends FormErrorHelper {

  def apply(): Form[YouPartnerBothEnum.Value] =
    Form(single("value" -> of(WhosHadBenefitsPYFormatter)))

  def options: Seq[InputOption] = Seq(whosHadBenefitsPYInputOption("value", YouPartnerBothEnum.YOU.toString),
    whosHadBenefitsPYInputOption("value-2", YouPartnerBothEnum.PARTNER.toString),
    whosHadBenefitsPYInputOption("value-3", YouPartnerBothEnum.BOTH.toString))

  private def whosHadBenefitsPYInputOption(id: String, option: String): InputOption = {
    new InputOption(
      id = id,
      value = option,
      messageKey = s"whosHadBenefitsPY.$option"
    )
  }

  private def WhosHadBenefitsPYFormatter = new Formatter[YouPartnerBothEnum.Value] {
    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(YouPartnerBothEnum.withName(s))
      case None => produceError(key, whosHadBenefitsPYErrorKey)
      case _ => produceError(key, unknownErrorKey)
    }

    def unbind(key: String, value: YouPartnerBothEnum.Value) = Map(key -> value.toString)
  }

  private def optionIsValid(value: String) = options.exists(o => o.value == value)
}
