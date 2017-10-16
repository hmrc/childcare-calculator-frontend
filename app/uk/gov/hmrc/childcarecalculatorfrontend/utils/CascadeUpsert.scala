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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import javax.inject.Singleton

import play.api.libs.json._
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{SelfEmployedOrApprenticeOrNeitherEnum, YesNoUnsureEnum, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

@Singleton
class CascadeUpsert {

  val You: String = YouPartnerBothEnum.YOU.toString
  val Partner: String = YouPartnerBothEnum.PARTNER.toString
  val Both: String = YouPartnerBothEnum.BOTH.toString

  val funcMap: Map[String, (JsValue, CacheMap) => CacheMap] =
    Map(
      LocationId.toString -> ((v, cm) => storeLocation(v, cm)),
      DoYouLiveWithPartnerId.toString() -> ((v, cm) => storeDoYouLiveWithPartner(v, cm)),
      PaidEmploymentId.toString -> ((v,cm) => storePaidEmployment(v, cm)),
      WhoIsInPaidEmploymentId.toString -> ((v,cm) => storeWhoIsInPaidEmployment(v, cm)),
      AreYouInPaidWorkId.toString -> ((v,cm) => storeAreYouInPaidWork(v, cm)),
      HasYourTaxCodeBeenAdjustedId.toString -> ((v,cm) => storeHasYourTaxCodeBeenAdjusted(v, cm)),
      DoYouKnowYourAdjustedTaxCodeId.toString -> ((v,cm) => storeDoYouKnowYourAdjustedTaxCode(v, cm)),
      HasYourPartnersTaxCodeBeenAdjustedId.toString-> ((v,cm) => storeHasYourPartnersTaxCodeBeenAdjusted(v, cm)),
      DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> ((v,cm) => storeDoYouKnowYourPartnersAdjustedTaxCode(v, cm)),
      EitherGetsVouchersId.toString -> ((v,cm) => storeEitherGetsVoucher(v, cm)),
      DoYouOrYourPartnerGetAnyBenefitsId.toString -> ((v,cm) => storeYouOrYourPartnerGetAnyBenefits(v, cm)),
      YourMinimumEarningsId.toString -> ((v, cm) => storeMinimumEarnings(v, cm)),
      PartnerMinimumEarningsId.toString -> ((v, cm) => storePartnerMinimumEarnings(v, cm)),
      AreYouSelfEmployedOrApprenticeId.toString -> ((v, cm) => AreYouSelfEmployedOrApprentice(v, cm)),
      PartnerSelfEmployedOrApprenticeId.toString -> ((v, cm) => PartnerSelfEmployedOrApprentice(v, cm))
    )

  private def storeLocation(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsString(northernIreland)) {
      cacheMap copy (data = cacheMap.data - ChildAgedTwoId.toString)
    } else
      cacheMap

    store(LocationId.toString, value, mapToStore)
  }

  private def storeDoYouLiveWithPartner(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if(value == JsBoolean(false)){
      cacheMap copy (data = cacheMap.data - PaidEmploymentId.toString - WhoIsInPaidEmploymentId.toString - PartnerWorkHoursId.toString -
        HasYourPartnersTaxCodeBeenAdjustedId.toString - DoYouKnowYourPartnersAdjustedTaxCodeId.toString - WhatIsYourPartnersTaxCodeId.toString -
        EitherGetsVouchersId.toString - WhoGetsVouchersId.toString - PartnerChildcareVouchersId.toString - DoYouOrYourPartnerGetAnyBenefitsId.toString -
        WhoGetsBenefitsId.toString - YourPartnersAgeId.toString  - AreYouSelfEmployedOrApprenticeId.toString - PartnerSelfEmployedOrApprenticeId.toString)

    } else if(value == JsBoolean(true))
      cacheMap copy (data = cacheMap.data - AreYouInPaidWorkId.toString - DoYouGetAnyBenefitsId.toString)
    else cacheMap

    store(DoYouLiveWithPartnerId.toString, value, mapToStore)
  }

  private def storeAreYouInPaidWork(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if(value == JsBoolean(false)){
      cacheMap copy (data = cacheMap.data - ParentWorkHoursId.toString -
        HasYourTaxCodeBeenAdjustedId.toString - DoYouKnowYourAdjustedTaxCodeId.toString - WhatIsYourTaxCodeId.toString -
        YourChildcareVouchersId.toString - DoYouGetAnyBenefitsId.toString - YourAgeId.toString)

    } else cacheMap

    store(AreYouInPaidWorkId.toString, value, mapToStore)
  }

  private def storePaidEmployment(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if(value == JsBoolean(false)){
      cacheMap copy (data = cacheMap.data - WhoIsInPaidEmploymentId.toString - ParentWorkHoursId.toString - PartnerWorkHoursId.toString -
        HasYourTaxCodeBeenAdjustedId.toString - DoYouKnowYourAdjustedTaxCodeId.toString - WhatIsYourTaxCodeId.toString -
        HasYourPartnersTaxCodeBeenAdjustedId.toString - DoYouKnowYourPartnersAdjustedTaxCodeId.toString - WhatIsYourPartnersTaxCodeId.toString -
        EitherGetsVouchersId.toString - WhoGetsVouchersId.toString - YourChildcareVouchersId.toString - PartnerChildcareVouchersId.toString -
        DoYouOrYourPartnerGetAnyBenefitsId.toString - WhoGetsBenefitsId.toString - DoYouGetAnyBenefitsId.toString - YourAgeId.toString - YourPartnersAgeId.toString -
        AreYouSelfEmployedOrApprenticeId.toString - PartnerSelfEmployedOrApprenticeId.toString)

    } else cacheMap

    store(PaidEmploymentId.toString, value, mapToStore)
  }

  private def storeWhoIsInPaidEmployment(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore =
      value match {
        case JsString(You) =>
          cacheMap copy (data = cacheMap.data - PartnerWorkHoursId.toString - HasYourPartnersTaxCodeBeenAdjustedId.toString -
            DoYouKnowYourPartnersAdjustedTaxCodeId.toString - WhatIsYourPartnersTaxCodeId.toString - EitherGetsVouchersId.toString -
            WhoGetsVouchersId.toString - YourPartnersAgeId.toString - PartnerMinimumEarningsId.toString - PartnerSelfEmployedOrApprenticeId.toString)

        case JsString(Partner) =>
          cacheMap copy (data = cacheMap.data - ParentWorkHoursId.toString - HasYourTaxCodeBeenAdjustedId.toString -
            DoYouKnowYourAdjustedTaxCodeId.toString - WhatIsYourTaxCodeId.toString - EitherGetsVouchersId.toString - WhoGetsVouchersId.toString -
            YourAgeId.toString - YourMinimumEarningsId.toString - AreYouSelfEmployedOrApprenticeId.toString)

        case JsString(Both) => cacheMap copy (data = cacheMap.data - YourChildcareVouchersId.toString)

        case _ => cacheMap
      }

    store(WhoIsInPaidEmploymentId.toString, value, mapToStore)
  }

  private def storeHasYourTaxCodeBeenAdjusted(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if(value == JsBoolean(false)){
      cacheMap copy (data = cacheMap.data - DoYouKnowYourAdjustedTaxCodeId.toString - WhatIsYourTaxCodeId.toString)
    } else {
      cacheMap
    }
    store(HasYourTaxCodeBeenAdjustedId.toString, value, mapToStore)
  }

  private def storeDoYouKnowYourAdjustedTaxCode(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if(value == JsBoolean(false)){
      cacheMap copy (data = cacheMap.data - WhatIsYourTaxCodeId.toString)
    } else {
      cacheMap
    }
    store(DoYouKnowYourAdjustedTaxCodeId.toString, value, mapToStore)
  }

  private def storeDoYouKnowYourPartnersAdjustedTaxCode(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if(value == JsBoolean(false)){
      cacheMap copy (data = cacheMap.data - WhatIsYourPartnersTaxCodeId.toString)
    } else {
      cacheMap
    }
    store(DoYouKnowYourPartnersAdjustedTaxCodeId.toString, value, mapToStore)
  }

  private def storeHasYourPartnersTaxCodeBeenAdjusted(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if(value == JsBoolean(false)){
      cacheMap copy (data = cacheMap.data - DoYouKnowYourPartnersAdjustedTaxCodeId.toString - WhatIsYourPartnersTaxCodeId.toString)
    } else {
      cacheMap
    }
    store(HasYourPartnersTaxCodeBeenAdjustedId.toString, value, mapToStore)
  }

  private def storeEitherGetsVoucher(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val No = YesNoUnsureEnum.NO.toString

    val mapToStore = if(value == JsString(No)){
      cacheMap copy (data = cacheMap.data - WhoGetsVouchersId.toString)
    } else {
      cacheMap
    }
    store(EitherGetsVouchersId.toString, value, mapToStore)
  }

  private def storeYouOrYourPartnerGetAnyBenefits(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if(value == JsBoolean(false)){
      cacheMap copy (data = cacheMap.data - WhoGetsBenefitsId.toString)
    } else {
      cacheMap
    }
    store(DoYouOrYourPartnerGetAnyBenefitsId.toString, value, mapToStore)
  }

private def storeMinimumEarnings(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(true)){
      cacheMap copy (data = cacheMap.data - AreYouSelfEmployedOrApprenticeId.toString)
    } else if (value == JsBoolean(false))
      cacheMap copy (data = cacheMap.data - YourMaximumEarningsId.toString)
      else cacheMap

      store(YourMinimumEarningsId.toString, value, mapToStore)
  }

  private def storePartnerMinimumEarnings(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsBoolean(true)){
      cacheMap copy (data = cacheMap.data - PartnerSelfEmployedOrApprenticeId.toString)
    } else if (value == JsBoolean(false))
      cacheMap copy (data = cacheMap.data - PartnerMaximumEarningsId.toString)
    else cacheMap

    store(PartnerMinimumEarningsId.toString, value, mapToStore)
  }

  private def AreYouSelfEmployedOrApprentice(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsString(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString)
                        || (value == JsString(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString))){
      cacheMap copy (data = cacheMap.data - YourSelfEmployedId.toString)
    } else cacheMap

    store(AreYouSelfEmployedOrApprenticeId.toString, value, mapToStore)
  }

  private def PartnerSelfEmployedOrApprentice(value: JsValue, cacheMap: CacheMap): CacheMap = {
    val mapToStore = if (value == JsString(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString)
      || (value == JsString(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString))){
      cacheMap copy (data = cacheMap.data - PartnerSelfEmployedId.toString)
    } else cacheMap

    store(PartnerSelfEmployedOrApprenticeId.toString, value, mapToStore)
  }


  def apply[A](key: String, value: A, originalCacheMap: CacheMap)(implicit fmt: Format[A]): CacheMap =
    funcMap.get(key).fold(store(key, value, originalCacheMap)) { fn => fn(Json.toJson(value), originalCacheMap)}

  def addRepeatedValue[A](key: String, value: A, originalCacheMap: CacheMap)(implicit fmt: Format[A]): CacheMap = {
    val values = originalCacheMap.getEntry[Seq[A]](key).getOrElse(Seq()) :+ value
    originalCacheMap copy(data = originalCacheMap.data + (key -> Json.toJson(values)))
  }

  private def store[A](key:String, value: A, cacheMap: CacheMap)(implicit fmt: Format[A]) =
    cacheMap copy (data = cacheMap.data + (key -> Json.toJson(value)))

  private def clearIfFalse[A](key: String, value: A, keysToRemove: Set[String], cacheMap: CacheMap)(implicit fmt: Format[A]): CacheMap = {
    val mapToStore = value match {
      case JsBoolean(false) => cacheMap copy (data = cacheMap.data.filterKeys(s => !keysToRemove.contains(s)))
      case _ => cacheMap
    }
    store(key, value, mapToStore)
  }
}
