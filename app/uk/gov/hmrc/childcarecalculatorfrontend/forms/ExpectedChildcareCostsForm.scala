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
import play.api.i18n.Messages
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildcarePayFrequency

object ExpectedChildcareCostsForm extends FormErrorHelper {

  def apply(frequency: ChildcarePayFrequency.Value, name: String)(implicit messages: Messages): Form[BigDecimal] =
    Form(
      "value" ->
        decimal(
          "expectedChildcareCosts.error.notCompleted",
          "expectedChildcareCosts.error.invalid",
          messages(s"childcarePayFrequency.$frequency").toLowerCase,
          name
        )
          .verifying(
            inRange[BigDecimal](
              1,
              9999.99,
              "expectedChildcareCosts.error.invalid",
              messages(s"childcarePayFrequency.$frequency").toLowerCase,
              name
            )
          )
    )

}
