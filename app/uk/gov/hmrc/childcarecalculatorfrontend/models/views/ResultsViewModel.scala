/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.models.views

import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import play.api.libs.json._

case class ResultsViewModel(firstParagraph : String = "",
                            tc: Option[BigDecimal] = None,
                            tfc:Option[BigDecimal] = None,
                            esc:Option[BigDecimal] = None,
                            freeHours:Option[BigDecimal] = None,
                            location:Option[Location.Value] = None,
                            childAgedTwo: Boolean = false,
                            taxCreditsOrUC: Option[String] = None,
                            showTFCWarning: Boolean = false,
                            tfcWarningMessage: String = "") {

  def noOfEligibleSchemes: Int = List(tc, tfc, esc, freeHours).flatten.size

  def isEligibleForAllButVouchers: Boolean = tc.nonEmpty && tfc.nonEmpty && freeHours.nonEmpty && esc.isEmpty

  def isEligibleForAllButTc: Boolean = esc.nonEmpty && tfc.nonEmpty && freeHours.nonEmpty && tc.isEmpty

  def isEligibleForAllButTfc: Boolean = esc.nonEmpty && tc.nonEmpty && freeHours.nonEmpty && tfc.isEmpty

  def isEligibleForAllButFreeHours: Boolean = esc.nonEmpty && tc.nonEmpty  && tfc.nonEmpty && freeHours.isEmpty

  def isEligibleOnlyForFreeHoursAndTfc: Boolean = freeHours.nonEmpty && tfc.nonEmpty && esc.isEmpty && tc.isEmpty

  def isEligibleOnlyForFreeHoursAndTc: Boolean = freeHours.nonEmpty && tc.nonEmpty && esc.isEmpty && tfc.isEmpty

  def isEligibleOnlyForFreeHoursAndEsc: Boolean = freeHours.nonEmpty && esc.nonEmpty && tc.isEmpty && tfc.isEmpty

  def isEligibleOnlyForTCAndTfc: Boolean = tfc.nonEmpty && tc.nonEmpty && esc.isEmpty && freeHours.isEmpty

  def isEligibleOnlyForTCAndEsc: Boolean = esc.nonEmpty && tc.nonEmpty && tfc.isEmpty && freeHours.isEmpty

  def isEligibleOnlyForTfcAndEsc: Boolean = esc.nonEmpty && tfc.nonEmpty && tc.isEmpty && freeHours.isEmpty
}

object ResultsViewModel {
  implicit val format: OFormat[ResultsViewModel] = Json.format[ResultsViewModel]
}