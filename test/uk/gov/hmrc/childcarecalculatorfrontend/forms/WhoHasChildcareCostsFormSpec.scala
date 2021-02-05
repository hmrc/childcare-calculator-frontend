/*
 * Copyright 2021 HM Revenue & Customs
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

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.CheckboxBehaviours

class WhoHasChildcareCostsFormSpec extends CheckboxBehaviours[Int] {

  override val validOptions: Set[Int] = Set(0, 1)
  override val invalidValue: String = "5"
  override val fieldName = "value"

  val form = WhoHasChildcareCostsForm(0, 1)

  "WhoHasChildcareCosts form" must {

    behave like aCheckboxForm(invalid = "error.unknown")

    behave like aMandatoryCheckboxForm("whoHasChildcareCosts.error.notCompleted")
  }
}
