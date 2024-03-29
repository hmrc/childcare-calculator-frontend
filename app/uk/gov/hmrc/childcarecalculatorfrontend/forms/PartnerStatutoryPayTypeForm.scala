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
import uk.gov.hmrc.childcarecalculatorfrontend.models.StatutoryPayTypeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.unknownErrorKey
import uk.gov.hmrc.childcarecalculatorfrontend.utils.InputOption

object PartnerStatutoryPayTypeForm extends FormErrorHelper {

  val errorKey = "partnerStatutoryPayType.error.notCompleted"

  def apply(): Form[StatutoryPayTypeEnum.Value] =
    Form(single("value" -> of(StatutoryPayTypeFormatter)))

  def options = Seq(
    partnerStatutoryPayTypeInputOption("value", StatutoryPayTypeEnum.MATERNITY.toString),
    partnerStatutoryPayTypeInputOption("value-2", StatutoryPayTypeEnum.PATERNITY.toString),
    partnerStatutoryPayTypeInputOption("value-3", StatutoryPayTypeEnum.ADOPTION.toString),
    partnerStatutoryPayTypeInputOption("value-4", StatutoryPayTypeEnum.SHARED_PARENTAL.toString)
  )

  private def partnerStatutoryPayTypeInputOption(id: String, option: String): InputOption = {
    new InputOption(
      id = id,
      value = option,
      messageKey = s"statutoryPayType.$option"
    )
  }

  private def StatutoryPayTypeFormatter = new Formatter[StatutoryPayTypeEnum.Value] {
    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(StatutoryPayTypeEnum.withName(s))
      case None => produceError(key, errorKey)
      case _ => produceError(key, unknownErrorKey)
    }

    def unbind(key: String, value: StatutoryPayTypeEnum.Value) = Map(key -> value.toString)
  }

  private def optionIsValid(value: String) = options.exists(o => o.value == value)
}
