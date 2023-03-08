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
import play.api.data.Forms._
import play.api.data.{Form, FormError}

object ChildStartEducationForm extends FormErrorHelper {

  val requiredKey = "childStartEducation.error.blank"
  val invalidKey = "childStartEducation.error.invalid"
  val before16Key = "childStartEducation.error.before16"

  def apply(dateOfBirth: LocalDate): Form[LocalDate] = Form(
    single(
      "date" -> localDateMapping(
        "day" -> int(requiredKey, invalidKey),
        "month" -> int(requiredKey, invalidKey),
        "year" -> int(requiredKey, invalidKey)
      )
        .verifying(invalidKey, _.isBefore(LocalDate.now.plusDays(1)))
        .verifying(before16Key, _.isAfter(dateOfBirth.plusYears(16)))
        .replaceError(FormError("", "error.invalidDate"), FormError("", invalidKey))
        .replaceError(FormError("day", requiredKey), FormError("", requiredKey))
        .replaceError(FormError("month", requiredKey), FormError("", requiredKey))
        .replaceError(FormError("year", requiredKey), FormError("", requiredKey))
        .replaceError(FormError("day", invalidKey), FormError("", invalidKey))
        .replaceError(FormError("month", invalidKey), FormError("", invalidKey))
        .replaceError(FormError("year", invalidKey), FormError("", invalidKey))
    )
  )
}
