/*
 * Copyright 2019 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class WhosHadBenefitsFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> WhosHadBenefitsForm.options.head.value
  )

  val form = WhosHadBenefitsForm()

  "WhosHadBenefits form" must {

    behave like questionForm[YouPartnerBothEnum.Value](YouPartnerBothEnum.YOU)

    behave like formWithOptionFieldError("value", whosHadBenefitsErrorKey, WhosHadBenefitsForm.options.map{x => x.value}:_*)
  }
}
