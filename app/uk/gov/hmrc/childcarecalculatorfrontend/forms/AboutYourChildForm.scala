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

  def apply(): Form[AboutYourChild] = Form(
    mapping(
      "name" ->
        string("aboutYourChild.error.name")
          .verifying(maxLength(35, "aboutYourChild.error.maxLength")),
      "dob" ->
        localDateMapping(
          "day" -> number,
          "month" -> number,
          "year" -> number
        )
          .verifying("aboutYourChild.error.past", _.isAfter(LocalDate.now.minusYears(20)))
          .verifying("aboutYourChild.error.future", _.isBefore(LocalDate.now.plusYears(1)))
          .replaceError("error.invalidDate", "aboutYourChild.error.dob")
          .replaceError(FormError("day", "error.required"), FormError("", "aboutYourChild.error.dob"))
          .replaceError(FormError("month", "error.required"), FormError("", "aboutYourChild.error.dob"))
          .replaceError(FormError("year", "error.required"), FormError("", "aboutYourChild.error.dob"))
          .replaceError(FormError("day", "error.number"), FormError("", "aboutYourChild.error.dob"))
          .replaceError(FormError("month", "error.number"), FormError("", "aboutYourChild.error.dob"))
          .replaceError(FormError("year", "error.number"), FormError("", "aboutYourChild.error.dob"))
    )(AboutYourChild.apply)(AboutYourChild.unapply)
  )
}
