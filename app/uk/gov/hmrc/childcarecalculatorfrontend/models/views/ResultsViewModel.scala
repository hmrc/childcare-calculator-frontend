/*
 * Copyright 2021 HM Revenue & Customs
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
                            location:Location.Value,
                            childAgedTwo: Boolean = false,
                            childAgedThreeOrFour: Boolean = false,
                            taxCreditsOrUC: Option[String] = None,
                            showTFCWarning: Boolean = false,
                            tfcWarningMessage: String = "",
                            tcSchemeInEligibilityMsg: String = "",
                            hasChildcareCosts: Boolean,
                            hasCostsWithApprovedProvider: Boolean,
                            isAnyoneInPaidEmployment: Boolean,
                            livesWithPartner: Boolean) {

  def noOfEligibleSchemes(hideTC: Boolean): Int = {
    val listOfSchemes = if (hideTC) List(tfc, esc, freeHours) else List(tc, tfc, esc, freeHours)

    listOfSchemes.flatten.size
  }
  def isEligibleForAllButVouchers: Boolean = tc.nonEmpty && tfc.nonEmpty && freeHours.nonEmpty && esc.isEmpty
  def isEligibleForAllButTc: Boolean = esc.nonEmpty && tfc.nonEmpty && freeHours.nonEmpty && tc.isEmpty
  def isEligibleForAllButFreeHours: Boolean = esc.nonEmpty && tc.nonEmpty  && tfc.nonEmpty && freeHours.isEmpty
  def isEligibleForAllButTfc: Boolean = esc.nonEmpty && tc.nonEmpty && freeHours.nonEmpty && tfc.isEmpty
  def isEligibleOnlyForFreeHoursAndTfc: Boolean = freeHours.nonEmpty && tfc.nonEmpty && esc.isEmpty && tc.isEmpty
  def isEligibleOnlyForFreeHoursAndTc: Boolean = freeHours.nonEmpty && tc.nonEmpty && esc.isEmpty && tfc.isEmpty
  def isEligibleOnlyForFreeHoursAndEsc: Boolean = freeHours.nonEmpty && esc.nonEmpty && tc.isEmpty && tfc.isEmpty
  def isEligibleOnlyForTCAndTfc: Boolean = tfc.nonEmpty && tc.nonEmpty && freeHours.isEmpty && esc.isEmpty
  def isEligibleOnlyForTCAndEsc: Boolean = esc.nonEmpty && tc.nonEmpty && tfc.isEmpty && freeHours.isEmpty
  def isEligibleOnlyForTfcAndEsc: Boolean = esc.nonEmpty && tfc.nonEmpty && freeHours.isEmpty && tc.isEmpty
  def isEligibleOnlyToMinimumFreeHours = esc.isEmpty && tfc.isEmpty && tc.isEmpty && (freeHours.contains(15) || freeHours.contains(10) || freeHours == Some(16) || freeHours == Some(12.5))
  def isEligibleToMaximumFreeHours =  freeHours.contains(30)
  def isEligibleToAllSchemes(hideTC: Boolean) = noOfEligibleSchemes(hideTC) == (if (hideTC) 3 else 4)
  def showTwoYearOldInfo(hideTC: Boolean) = {
    if (childAgedTwo) {
      location match {
        case Location.NORTHERN_IRELAND => false
        case Location.WALES => false
        case _ => {
          if (noOfEligibleSchemes(hideTC) == 0) {
            if (childAgedThreeOrFour) false else true
          }
          else {
            true
          }
        }
      }
    }
    else {
      false
    }
  }
}

object ResultsViewModel {
  implicit val format: OFormat[ResultsViewModel] = Json.format[ResultsViewModel]
}