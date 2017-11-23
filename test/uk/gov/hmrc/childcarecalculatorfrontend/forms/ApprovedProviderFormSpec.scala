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

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.FormBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class ApprovedProviderFormSpec extends FormBehaviours {

  val validData: Map[String, String] = Map(
    "value" -> ApprovedProviderForm.options.head.value
  )

  val form = ApprovedProviderForm()

  "ApprovedProvider form" must {
    behave like questionForm[String](ApprovedProviderForm.options.head.value)

    behave like formWithOptionFieldError("value", approvedProviderErrorKey, ApprovedProviderForm.options.map{x => x.value}:_*)
  }
}
