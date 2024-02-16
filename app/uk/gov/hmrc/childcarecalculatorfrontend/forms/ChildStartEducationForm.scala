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
import play.api.data.Form
import play.api.data.Forms.{of, single}
import play.api.i18n.Messages
import uk.gov.hmrc.childcarecalculatorfrontend.forms.formatters.DateFormatter

object ChildStartEducationForm extends FormErrorHelper {

  private val dateKey = "childStartEducation"
  private val sixteen = 16
  private val maxDate = LocalDate.now.plusDays(1)
  private def minDate(date: LocalDate): LocalDate = date.plusYears(sixteen)

  def apply(dob: LocalDate, name: String)(implicit messages: Messages): Form[LocalDate] = Form(
    single(
      dateKey -> of(DateFormatter(
        dateKey,
        optMinDate = Some(minDate(dob)),
        optMaxDate = Some(maxDate),
        args = Seq(name)
      ))
    )
  )

}
