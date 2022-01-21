/*
 * Copyright 2022 HM Revenue & Customs
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

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.StatutoryPayTypeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._


class PartnerStatutoryPayTypeFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> StatutoryPayTypeEnum.MATERNITY.toString
  )

  val form = PartnerStatutoryPayTypeForm()

  "PartnerStatutoryPayType form" must {
    behave like questionForm[StatutoryPayTypeEnum.Value](StatutoryPayTypeEnum.MATERNITY)

    behave like formWithOptionFieldError("value", partnerStatutoryPayTypeErrorKey, PartnerStatutoryPayTypeForm.options.map{x => x.value}:_*)
  }
}
