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

import play.api.data.Forms.{of, single}
import play.api.data.format.Formatter
import play.api.data.{Form, FormError}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoGetsBenefitsForm.produceError
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothNeitherEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{doYouOrPartnerGetBenefitsErrorKey, unknownErrorKey}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.InputOption

object DoYouOrPartnerGetBenefitsForm extends FormErrorHelper {

  private val doYouOrPartnerGetBenefitsFormatter: Formatter[String] = new Formatter[String] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None => produceError(key, doYouOrPartnerGetBenefitsErrorKey)
      case _ => produceError(key, unknownErrorKey)
    }

    def unbind(key: String, value: String): Map[String, String] = Map(key -> value)
  }

  def apply(): Form[String] =
    Form(single("value" -> of(doYouOrPartnerGetBenefitsFormatter)))

  def options: Seq[InputOption] = Seq(
    doYouOrPartnerGetBenefitsInputOption(YouPartnerBothNeitherEnum.YOU.toString, "value"),
    doYouOrPartnerGetBenefitsInputOption(YouPartnerBothNeitherEnum.PARTNER.toString, "value-2"),
    doYouOrPartnerGetBenefitsInputOption(YouPartnerBothNeitherEnum.BOTH.toString, "value-3"),
    doYouOrPartnerGetBenefitsInputOption(YouPartnerBothNeitherEnum.NEITHER.toString, "value-4")
  )

  private def doYouOrPartnerGetBenefitsInputOption(option: String, id: String) =
    new InputOption(
      id = id,
      value = option,
      messageKey = s"doYouOrPartnerGetBenefits.$option"
    )

  private def optionIsValid(value: String): Boolean = options.exists(o => o.value == value)
}
