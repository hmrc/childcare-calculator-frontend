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
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild

object AboutYourChildForm extends Mappings {

  val requiredKey = "aboutYourChild.error.dob.blank"
  val invalidKey = "aboutYourChild.error.dob.invalid"

  def apply(): Form[AboutYourChild] = Form(
    mapping(
      "name" ->
        string("aboutYourChild.error.name")
          .verifying(maxLength(35, "aboutYourChild.error.maxLength")),
      "dob" ->
        localDateMapping(
          "day" -> int(requiredKey, invalidKey),
          "month" -> int(requiredKey, invalidKey),
          "year" -> int(requiredKey, invalidKey)
        )
          .verifying("aboutYourChild.error.past", _.isAfter(LocalDate.now.minusYears(20)))
          .verifying("aboutYourChild.error.future", _.isBefore(LocalDate.now.plusYears(1)))
          .replaceError(FormError("", "error.invalidDate"), FormError("", invalidKey))
          .replaceError(FormError("day", requiredKey), FormError("", requiredKey))
          .replaceError(FormError("month", requiredKey), FormError("", requiredKey))
          .replaceError(FormError("year", requiredKey), FormError("", requiredKey))
          .replaceError(FormError("day", invalidKey), FormError("", invalidKey))
          .replaceError(FormError("month", invalidKey), FormError("", invalidKey))
          .replaceError(FormError("year", invalidKey), FormError("", invalidKey))
    )(AboutYourChild.apply)(AboutYourChild.unapply)
  )
}
