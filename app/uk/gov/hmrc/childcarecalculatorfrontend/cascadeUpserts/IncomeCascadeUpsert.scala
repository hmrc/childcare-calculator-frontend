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

package uk.gov.hmrc.childcarecalculatorfrontend.cascadeUpserts

import javax.inject.Inject

import play.api.libs.json.{JsBoolean, JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.SubCascadeUpsert
import uk.gov.hmrc.http.cache.client.CacheMap

class IncomeCascadeUpsert @Inject()() extends SubCascadeUpsert {

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap]  =
    Map(
      YourOtherIncomeThisYearId.toString -> ((v, cm) => storeYourOtherIncomeThisYear(v, cm)),
      PartnerAnyOtherIncomeThisYearId.toString -> ((v, cm) => storePartnerAnyOtherIncomeThisYear(v, cm)),
      BothOtherIncomeThisYearId.toString -> ((v, cm) => storeBothOtherIncomeThisYear(v, cm)),
      WhoGetsOtherIncomeCYId.toString -> ((v, cm) => storeWhoGetsOtherIncomeCY(v, cm)),
      YourOtherIncomeLYId.toString -> ((v, cm) => storeYourOtherIncomePY(v, cm)),
      PartnerAnyOtherIncomeLYId.toString -> ((v, cm) => storePartnerAnyOtherIncomePY(v, cm)),
      BothOtherIncomeLYId.toString -> ((v, cm) => storeBothOtherIncomePY(v, cm)),
      WhoOtherIncomePYId.toString -> ((v, cm) => storeWhoOtherIncomePY(v, cm)),
      BothPaidWorkPYId.toString -> ((v, cm) => storeBothPaidWorkPY(v, cm)),
      WhoWasInPaidWorkPYId.toString -> ((v, cm) => storeWhoWasInPaidWork(v, cm))
    )

  private def storeYourOtherIncomeThisYear(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore= value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - YourOtherIncomeAmountCYId.toString)
      case _ => cacheMap
    }

    store(YourOtherIncomeThisYearId.toString, value, mapToStore)
  }

  private def storePartnerAnyOtherIncomeThisYear(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore= value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - PartnerOtherIncomeAmountCYId.toString)
      case _ => cacheMap
    }

    store(PartnerAnyOtherIncomeThisYearId.toString, value, mapToStore)
  }

  private def storeBothOtherIncomeThisYear(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore= value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - WhoGetsOtherIncomeCYId.toString - YourOtherIncomeAmountCYId.toString
        - PartnerOtherIncomeAmountCYId.toString - OtherIncomeAmountCYId.toString)
      case _ => cacheMap
    }

    store(BothOtherIncomeThisYearId.toString, value, mapToStore)
  }

  private def storeWhoGetsOtherIncomeCY(value: JsValue, cacheMap: CacheMap): CacheMap ={
    val mapToStore = value match {
      case JsString(`you`) => cacheMap copy (data = cacheMap.data  - PartnerOtherIncomeAmountCYId.toString -
        OtherIncomeAmountCYId.toString)
      case JsString(`partner`) => cacheMap copy (data = cacheMap.data  - YourOtherIncomeAmountCYId.toString -
        OtherIncomeAmountCYId.toString)
      case JsString(`both`) => cacheMap copy (data = cacheMap.data  - YourOtherIncomeAmountCYId.toString -
        PartnerOtherIncomeAmountCYId.toString)
      case _ => cacheMap
    }

    store(WhoGetsOtherIncomeCYId.toString, value, mapToStore)
  }


  private def storeYourOtherIncomePY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore= value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - YourOtherIncomeAmountPYId.toString)
      case _ => cacheMap
    }

    store(YourOtherIncomeLYId.toString, value, mapToStore)
  }

  private def storePartnerAnyOtherIncomePY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore= value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - PartnerOtherIncomeAmountPYId.toString)
      case _ => cacheMap
    }

    store(PartnerAnyOtherIncomeLYId.toString, value, mapToStore)
  }

  private def storeBothOtherIncomePY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore= value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - YourOtherIncomeAmountPYId.toString - PartnerOtherIncomeAmountPYId.toString
        - OtherIncomeAmountPYId.toString  - WhoOtherIncomePYId.toString)
      case _ => cacheMap
    }

    store(BothOtherIncomeLYId.toString, value, mapToStore)
  }

  private def storeWhoOtherIncomePY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsString(`you`) => cacheMap copy (data = cacheMap.data  - PartnerOtherIncomeAmountPYId.toString -
        OtherIncomeAmountPYId.toString)
      case JsString(`partner`) => cacheMap copy (data = cacheMap.data  - YourOtherIncomeAmountPYId.toString -
        OtherIncomeAmountPYId.toString)
      case JsString(`both`) => cacheMap copy (data = cacheMap.data  - YourOtherIncomeAmountPYId.toString -
        PartnerOtherIncomeAmountPYId.toString)
      case _ => cacheMap
    }

    store(WhoOtherIncomePYId.toString, value, mapToStore)
  }

  private def storeBothPaidWorkPY(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data - WhoWasInPaidWorkPYId.toString -
        EmploymentIncomePYId.toString - ParentEmploymentIncomePYId.toString - PartnerEmploymentIncomePYId.toString -
        YouPaidPensionPYId.toString - PartnerPaidPensionPYId.toString - BothPaidPensionPYId.toString -
        WhoPaidIntoPensionPYId.toString - HowMuchYouPayPensionPYId.toString - HowMuchPartnerPayPensionPYId.toString -
        HowMuchBothPayPensionPYId.toString)

      case _ => cacheMap
    }

    store(BothPaidWorkPYId.toString, value, mapToStore)
  }

  private def storeWhoWasInPaidWork(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = value match {
      case JsString(`you`) => cacheMap copy (data = cacheMap.data - PartnerEmploymentIncomePYId.toString -
        PartnerPaidPensionPYId.toString  - HowMuchPartnerPayPensionPYId.toString - EmploymentIncomePYId.toString -
        WhoPaidIntoPensionPYId.toString -  BothPaidPensionPYId.toString - HowMuchBothPayPensionPYId.toString)

      case JsString(`partner`) => cacheMap copy (data = cacheMap.data - ParentEmploymentIncomePYId.toString -
        YouPaidPensionPYId.toString - HowMuchYouPayPensionPYId.toString - EmploymentIncomePYId.toString -
        WhoPaidIntoPensionPYId.toString - BothPaidPensionPYId.toString - HowMuchBothPayPensionPYId.toString)

      case JsString(`both`) => cacheMap copy (data = cacheMap.data  - ParentEmploymentIncomePYId.toString -
        YouPaidPensionPYId.toString - HowMuchYouPayPensionPYId.toString  - PartnerEmploymentIncomePYId.toString -
        PartnerPaidPensionPYId.toString  - HowMuchPartnerPayPensionPYId.toString)

      case _ => cacheMap
    }

    store(WhoWasInPaidWorkPYId.toString, value, mapToStore)
  }

}
