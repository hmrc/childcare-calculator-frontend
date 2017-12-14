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

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

object PartnerBenefitsIncomePYForm extends FormErrorHelper {

  val errorKeyBlank = partnerBenefitsIncomePYRequiredErrorKey
  val errorKeyInvalid = partnerBenefitsIncomePYInvalidErrorKey

  def apply(): Form[BigDecimal] =
    Form(
      "value" ->
        decimal(errorKeyBlank, errorKeyInvalid)
          .verifying(inRange[BigDecimal](1, 9999.99, errorKeyInvalid)))
}
