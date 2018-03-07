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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.http.cache.client.CacheMap

object CacheMapCloner {

  val singleParentCurrentYearToPreviousYear = Map(ParentEmploymentIncomeCYId.toString -> ParentEmploymentIncomePYId.toString,
    YouPaidPensionCYId.toString -> YouPaidPensionPYId.toString,
    HowMuchYouPayPensionId.toString -> HowMuchYouPayPensionPYId.toString,
    YouAnyTheseBenefitsIdCY.toString -> YouAnyTheseBenefitsPYId.toString,
    YouBenefitsIncomeCYId.toString -> YouBenefitsIncomePYId.toString,
    YourOtherIncomeThisYearId.toString -> YourOtherIncomeLYId.toString,
    YourOtherIncomeAmountCYId.toString -> YourOtherIncomeAmountPYId.toString)

  val bothIncomeCurrentYearToPreviousYear = Map(EmploymentIncomeCYId.toString -> EmploymentIncomePYId.toString,
    BothPaidPensionCYId.toString -> BothPaidPensionPYId.toString,
    WhoPaysIntoPensionId.toString -> WhoPaidIntoPensionPYId.toString,
    HowMuchBothPayPensionId.toString -> HowMuchBothPayPensionPYId.toString,
    BothAnyTheseBenefitsCYId.toString -> BothAnyTheseBenefitsPYId.toString,
    WhoGetsBenefitsId.toString -> WhosHadBenefitsPYId.toString,
    BenefitsIncomeCYId.toString -> BothBenefitsIncomePYId.toString,
    BothOtherIncomeThisYearId.toString -> BothOtherIncomeLYId.toString,
    WhoGetsOtherIncomeCYId.toString -> WhoOtherIncomePYId.toString,
    OtherIncomeAmountCYId.toString -> OtherIncomeAmountPYId.toString)

  def cloneSection(data: CacheMap, sectionToClone: Map[String, String]): CacheMap = {
    sectionToClone.foldLeft(data)((clonedData, sectionToClone) => {
      clonedData.data.get(sectionToClone._1) match {
        case Some(dataToClone) => clonedData.copy(data = clonedData.data + (sectionToClone._2 -> dataToClone))
        case _ => clonedData
      }
    })
  }
}
