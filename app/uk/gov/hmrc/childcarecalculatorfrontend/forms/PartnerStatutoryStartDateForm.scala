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

import java.time.LocalDate
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import uk.gov.hmrc.time.TaxYear

object PartnerStatutoryStartDateForm extends FormErrorHelper {

  val requiredKey = "partnerStatutoryStartDate.error.required"
  val invalidKey = "partnerStatutoryStartDate.error.invalid"

  def apply(statutoryType: String): Form[LocalDate] = Form(
    single(
      "date" -> localDateMapping(
        "day" -> int(requiredKey, invalidKey, statutoryType),
        "month" -> int(requiredKey, invalidKey, statutoryType),
        "year" -> int(requiredKey, invalidKey, statutoryType)
      )
        .verifying(before(LocalDate.now.plusDays(1), "partnerStatutoryStartDate.error.past", statutoryType))
        .verifying(after(
          TaxYear.current.starts.minusYears(2).minusDays(1),
          "partnerStatutoryStartDate.error.past-over-2-years",
          statutoryType,
          TaxYear.current.currentYear.toString)
        )
        .replaceError(FormError("", "error.invalidDate", statutoryType), FormError("", invalidKey, Seq(statutoryType)))
        .replaceError(FormError("day", requiredKey, statutoryType), FormError("", requiredKey, Seq(statutoryType)))
        .replaceError(FormError("month", requiredKey, statutoryType), FormError("", requiredKey, Seq(statutoryType)))
        .replaceError(FormError("year", requiredKey, statutoryType), FormError("", requiredKey, Seq(statutoryType)))
        .replaceError(FormError("day", invalidKey, statutoryType), FormError("", invalidKey, Seq(statutoryType)))
        .replaceError(FormError("month", invalidKey, statutoryType), FormError("", invalidKey, Seq(statutoryType)))
        .replaceError(FormError("year", invalidKey, statutoryType), FormError("", invalidKey, Seq(statutoryType)))
    )
  )
}
