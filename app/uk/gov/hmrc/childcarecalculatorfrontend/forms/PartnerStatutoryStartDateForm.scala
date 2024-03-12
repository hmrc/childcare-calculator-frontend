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
import play.api.data.{Form, Forms}
import play.api.data.Forms.single
import play.api.i18n.Messages
import uk.gov.hmrc.childcarecalculatorfrontend.forms.formatters.DateFormatter
import uk.gov.hmrc.time.TaxYear

object PartnerStatutoryStartDateForm extends FormErrorHelper {

  private val dateKey = "partnerStatutoryStartDate"
  private val minDate = TaxYear.current.starts.minusYears(2).minusDays(1)
  private val maxDate = LocalDate.now.plusDays(1)

  def apply(statutoryType: String)(implicit messages: Messages): Form[LocalDate] = Form(
    single(
      dateKey -> Forms.of(DateFormatter(
        dateKey,
        optMinDate = Some(minDate),
        optMaxDate = Some(maxDate),
        args = Seq(statutoryType, TaxYear.current.starts.minusYears(2).getYear.toString)
      ))
    )
  )

}
