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

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.utils.InputOption
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

object WhoIsInPaidEmploymentForm extends FormErrorHelper {

  def WhoIsInPaidEmploymentFormatter = new Formatter[String] {
    def bind(key: String, data: Map[String, String]) = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None => produceError(key, whoIsInPaidEmploymentErrorKey)
      case _ => produceError(key, unknownErrorKey)
    }

    def unbind(key: String, value: String) = Map(key -> value)
  }

  def apply(): Form[String] = 
    Form(single("value" -> of(WhoIsInPaidEmploymentFormatter)))

  def options = Seq(
      InputOption("whoIsInPaidEmployment", YouPartnerBothEnum.YOU.toString),
    InputOption("whoIsInPaidEmployment", YouPartnerBothEnum.PARTNER.toString),
    InputOption("whoIsInPaidEmployment", YouPartnerBothEnum.BOTH.toString),
    InputOption("whoIsInPaidEmployment", YouPartnerBothEnum.NEITHER.toString)

  )

  def optionIsValid(value: String) = options.exists(o => o.value == value)
}
