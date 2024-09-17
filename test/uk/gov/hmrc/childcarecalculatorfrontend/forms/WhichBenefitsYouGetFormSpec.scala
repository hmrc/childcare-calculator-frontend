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

import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.CheckboxBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Location, WhichBenefitsEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class WhichBenefitsYouGetFormSpec extends CheckboxBehaviours[String] {

  val location: Location.Value = Location.ENGLAND
  val form = WhichBenefitsYouGetForm(location)

  override val validOptions: Set[String] = WhichBenefitsEnum.values.map(_.toString).filterNot(_ == "scottishCarersAllowance" )

  override val fieldName = "value"

  "WhichBenefitsYouGet form" must {
    behave like aCheckboxForm(invalid = "error.unknown")

    behave like aMandatoryCheckboxForm(whichBenefitsYouGetErrorKey)

  }
}
