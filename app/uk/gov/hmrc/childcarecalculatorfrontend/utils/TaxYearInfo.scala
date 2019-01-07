/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import javax.inject.Inject
import org.joda.time.LocalDate
import uk.gov.hmrc.time.TaxYear

class TaxYearInfo @Inject()() {

  private lazy val currentTaxYear: TaxYear = TaxYear.current

  lazy val currentTaxYearStart: String = currentTaxYear.starts.getYear.toString
  lazy val currentTaxYearEndDate: LocalDate = currentTaxYear.finishes
  lazy val currentTaxYearEnd: String = currentTaxYearEndDate.getYear.toString

  private lazy val previousTaxYear: TaxYear = currentTaxYear.previous

  lazy val previousTaxYearStart: String = previousTaxYear.starts.getYear.toString
  lazy val previousTaxYearEndDate: LocalDate = previousTaxYear.finishes
  lazy val previousTaxYearEnd: String = previousTaxYearEndDate.getYear.toString
}
