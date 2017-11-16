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

import org.joda.time.LocalDate
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter

object PartnerStatutoryStartDateForm extends FormErrorHelper {

  def apply(): Form[LocalDate] = Form(
    single(
      "date" -> localDateMapping(
        "day" -> number,
        "month" -> number,
        "year" -> number
      )
        .verifying("partnerStatutoryStartDate.error.invalid", _.isBefore(LocalDate.now.plusDays(1)))
        .replaceError("error.invalidDate", "partnerStatutoryStartDate.error.invalid")
        .replaceError(FormError("day", "error.required"), FormError("", "partnerStatutoryStartDate.error"))
        .replaceError(FormError("month", "error.required"), FormError("", "partnerStatutoryStartDate.error"))
        .replaceError(FormError("year", "error.required"), FormError("", "partnerStatutoryStartDate.error"))
        .replaceError(FormError("day", "error.number"), FormError("", "partnerStatutoryStartDate.error"))
        .replaceError(FormError("month", "error.number"), FormError("", "partnerStatutoryStartDate.error"))
        .replaceError(FormError("year", "error.number"), FormError("", "partnerStatutoryStartDate.error"))
    )
  )
}
