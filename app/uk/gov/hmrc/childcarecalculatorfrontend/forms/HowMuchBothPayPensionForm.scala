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
import uk.gov.hmrc.childcarecalculatorfrontend.models.HowMuchBothPayPension

object HowMuchBothPayPensionForm extends FormErrorHelper {

  private val howMuchYouPayInvalidKey     = "howMuchYouPayPension.error.invalid"
  private val howMuchPartnerPayInvalidKey = "howMuchPartnerPayPension.error.invalid"

  def apply(): Form[HowMuchBothPayPension] = Form(
    mapping(
      "howMuchYouPayPension" ->
        decimal("howMuchYouPayPension.error.required", howMuchYouPayInvalidKey)
          .verifying(minimumValue[BigDecimal](1, howMuchYouPayInvalidKey))
          .verifying(maximumValue[BigDecimal](9999.99, howMuchYouPayInvalidKey)),
      "howMuchPartnerPayPension" ->
        decimal("howMuchPartnerPayPension.error.required", howMuchPartnerPayInvalidKey)
          .verifying(minimumValue[BigDecimal](1, howMuchPartnerPayInvalidKey))
          .verifying(maximumValue[BigDecimal](9999.99, howMuchPartnerPayInvalidKey))
    )(HowMuchBothPayPension.apply)(HowMuchBothPayPension.unapply)
  )

}
