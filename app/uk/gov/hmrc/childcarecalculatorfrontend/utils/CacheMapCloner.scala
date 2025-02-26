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


  private val mappingError = "mapping not found"

  private val complexObjectsMapper: Map[String, Seq[String]] = Map(EmploymentIncomeCYId.toString -> Seq(ParentEmploymentIncomeCYId.toString, PartnerEmploymentIncomeCYId.toString),
    HowMuchBothPayPensionId.toString -> Seq(HowMuchYouPayPensionId.toString, HowMuchPartnerPayPensionId.toString),
    BenefitsIncomeCYId.toString -> Seq(ParentBenefitsIncomeId.toString, PartnerBenefitsIncomeId.toString),
    OtherIncomeAmountCYId.toString -> Seq(ParentOtherIncomeId.toString, PartnerOtherIncomeId.toString))

  private val jsonObjectsMapper: Map[String, String] = Map(
    ParentBenefitsIncomeId.toString -> ParentBenefitsIncomePYId.toString)


  private def removeClonedData(data: CacheMap, sectionToClone: Map[String, String]) = {
    sectionToClone.foldLeft(data)((clonedData, sectionToClear) => {
      clonedData.copy(data = clonedData.data - sectionToClear._2)
    })
  }
}
