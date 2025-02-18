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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import play.api.libs.json.{JsBoolean, JsString, JsValue, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._

object CacheMapCloner {
  def cloneSection(userAnswers: CacheMap, sectionToClone: Map[String, String], customSections: Option[Map[String, JsValue]] = None): CacheMap = {
    val cacheMapWithClearedData = removeClonedData(userAnswers, sectionToClone)
    val clonedCacheMap = sectionToClone.foldLeft(cacheMapWithClearedData)((clonedData, sectionToClone) => {
      clonedData.data.get(sectionToClone._1) match {
        case Some(dataToClone) => clonedData.copy(data = clonedData.data + (sectionToClone._2 -> {
          complexObjectsMapper.get(sectionToClone._1) match {
            case Some(data) => {
              data.foldLeft(Json.obj())((clonedResult, property) => {
                clonedResult + (jsonObjectsMapper.get(property).getOrElse(mappingError) -> (dataToClone \ property).getOrElse(Json.toJson(mappingError)))
              })
            }
            case _ => dataToClone
          }
        }))
        case _ => clonedData
      }
    })

    customSections.fold(clonedCacheMap)(customSections => {
      customSections.foldLeft(clonedCacheMap)((clonedCacheMap, section) => {
        clonedCacheMap.copy(data = clonedCacheMap.data + section)
      })
    })
  }

  def removeClonedDataForPreviousYearIncome(data: CacheMap) = {
    data.getEntry[Boolean](DoYouLiveWithPartnerId.toString) match {
      case Some(livesWithPartner) => {
        if (livesWithPartner) {
         val mapWithNoClonedData = removeClonedData(data,bothIncomeCurrentYearToPreviousYear)
          mapWithNoClonedData.copy(data = mapWithNoClonedData.data - BothPaidWorkPYId.toString - WhoWasInPaidWorkPYId.toString)
        }
        else{
          removeClonedData(data,singleParentCurrentYearToPreviousYear)
        }
      }

      case _ => data
    }
  }

  private val mappingError = "mapping not found"

  private val singleParentCurrentYearToPreviousYear = Map(AreYouInPaidWorkId.toString -> ParentPaidWorkPYId.toString,
    ParentEmploymentIncomeCYId.toString -> ParentEmploymentIncomePYId.toString,
    YouAnyTheseBenefitsIdCY.toString -> YouAnyTheseBenefitsPYId.toString,
    YourOtherIncomeThisYearId.toString -> YourOtherIncomeLYId.toString)

  private val bothIncomeCurrentYearToPreviousYear = Map(ParentEmploymentIncomeCYId.toString -> ParentEmploymentIncomePYId.toString,
    PartnerEmploymentIncomeCYId.toString -> PartnerEmploymentIncomePYId.toString,
    EmploymentIncomeCYId.toString -> EmploymentIncomePYId.toString)

  private val complexObjectsMapper: Map[String, Seq[String]] = Map(EmploymentIncomeCYId.toString -> Seq(ParentEmploymentIncomeCYId.toString, PartnerEmploymentIncomeCYId.toString),
    EmploymentIncomePYId.toString -> Seq(ParentEmploymentIncomePYId.toString, PartnerEmploymentIncomePYId.toString),
    HowMuchBothPayPensionId.toString -> Seq(HowMuchYouPayPensionId.toString, HowMuchPartnerPayPensionId.toString),
    BenefitsIncomeCYId.toString -> Seq(ParentBenefitsIncomeId.toString, PartnerBenefitsIncomeId.toString),
    OtherIncomeAmountCYId.toString -> Seq(ParentOtherIncomeId.toString, PartnerOtherIncomeId.toString))

  private val jsonObjectsMapper: Map[String, String] = Map(ParentEmploymentIncomeCYId.toString -> ParentEmploymentIncomePYId.toString,
    PartnerEmploymentIncomeCYId.toString -> PartnerEmploymentIncomePYId.toString,
    ParentBenefitsIncomeId.toString -> ParentBenefitsIncomePYId.toString)

  def cloneCYIncomeIntoPYIncome(userAnswers: CacheMap) = {
    userAnswers.getEntry[Boolean](DoYouLiveWithPartnerId.toString) match {
      case Some(livesWithPartner) => {
        if (livesWithPartner) {
          val anyoneInPaidEmployment = userAnswers.getEntry[String](WhoIsInPaidEmploymentId.toString).fold(false)(c => c != ChildcareConstants.neither)
          val whoInPaidEmployment = userAnswers.getEntry[String](WhoIsInPaidEmploymentId.toString) match {
            case Some(ChildcareConstants.You) => {
              checkIfWorkedAtAnyPointThisYear(userAnswers,PartnerPaidWorkCYId.toString,ChildcareConstants.You)
            }
            case Some(ChildcareConstants.Partner) => {
              checkIfWorkedAtAnyPointThisYear(userAnswers,ParentPaidWorkCYId.toString,ChildcareConstants.Partner)
            }
            case Some(ChildcareConstants.Both) => ChildcareConstants.Both
            case _ => ChildcareConstants.neither
          }
          cloneSection(userAnswers, bothIncomeCurrentYearToPreviousYear, Some(Map(BothPaidWorkPYId.toString -> JsBoolean(anyoneInPaidEmployment),WhoWasInPaidWorkPYId.toString -> JsString(whoInPaidEmployment))))
        }
        else {
          cloneSection(userAnswers, singleParentCurrentYearToPreviousYear)
        }
      }
      case _ => userAnswers
    }
  }

  private def checkIfWorkedAtAnyPointThisYear(userAnswers: CacheMap, memberWorkingAtSomePointCurrentYear: String, memberWorkingAllYear: String) = {
    val workedThisYear = userAnswers.getEntry[Boolean](memberWorkingAtSomePointCurrentYear).fold(false)(c => c)
    if (workedThisYear) {
      ChildcareConstants.Both
    }
    else {
      memberWorkingAllYear
    }
  }

  private def removeClonedData(data: CacheMap, sectionToClone: Map[String, String]) = {
    sectionToClone.foldLeft(data)((clonedData, sectionToClear) => {
      clonedData.copy(data = clonedData.data - sectionToClear._2)
    })
  }
}
