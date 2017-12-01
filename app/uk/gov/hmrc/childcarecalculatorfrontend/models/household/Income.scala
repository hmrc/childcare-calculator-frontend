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

package uk.gov.hmrc.childcarecalculatorfrontend.models.household

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

case class Income(employmentIncome: Option[BigDecimal] = None,
                   pension: Option[BigDecimal] = None,
                   otherIncome: Option[BigDecimal] = None,
                   benefits: Option[BigDecimal] = None,
                   statutoryIncome: Option[StatutoryIncome] = None,
                   taxCode: Option[String] = None)

object Income {

  def apply(answers: UserAnswers,
            statsIncome: Option[StatutoryIncome]): Income = {

    Income(statutoryIncome = statsIncome)
  }

  implicit val formatIncome: OFormat[Income] = Json.format[Income]
}
