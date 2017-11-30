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

import play.api.libs.json.Format
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoNotYetEnum.YesNoNotYetEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YesNoNotYetEnum, YesNoUnsureEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoUnsureEnum.YesNoUnsureEnum
import uk.gov.hmrc.http.cache.client.CacheMap

object SessionDataOverwrite extends SubCascadeUpsert {
  def applyRules(userData: CacheMap) : CacheMap = {
    val childCareCostsIfNotYetThenYes = SessionDataOverwrite.overwrite[YesNoNotYetEnum](_:CacheMap, ChildcareCostsId.toString, YesNoNotYetEnum.NOTYET, YesNoNotYetEnum.YES)
    val approvedProviderIfNotSureThenYes = SessionDataOverwrite.overwrite[YesNoUnsureEnum](_:CacheMap, ApprovedProviderId.toString, YesNoUnsureEnum.NOTSURE, YesNoUnsureEnum.YES)
    val yourAdjustedTaxCodeIfNotSureThenNo = SessionDataOverwrite.overwrite[YesNoUnsureEnum](_:CacheMap, DoYouKnowYourAdjustedTaxCodeId.toString, YesNoUnsureEnum.NOTSURE, YesNoUnsureEnum.NO)
    val partnerAdjustedTaxCodeIfNotSureThenNo = SessionDataOverwrite.overwrite[YesNoUnsureEnum](_:CacheMap, DoYouKnowYourPartnersAdjustedTaxCodeId.toString, YesNoUnsureEnum.NOTSURE, YesNoUnsureEnum.NO)
    val partnerChildcarePartnerIfNotSureThenNo = SessionDataOverwrite.overwrite[YesNoUnsureEnum](_:CacheMap, PartnerChildcareVouchersId.toString, YesNoUnsureEnum.NOTSURE, YesNoUnsureEnum.YES)
    val yourChildcareVouchersIfNotSureThenNo = SessionDataOverwrite.overwrite[YesNoUnsureEnum](_:CacheMap, YourChildcareVouchersId.toString, YesNoUnsureEnum.NOTSURE, YesNoUnsureEnum.YES)
    val eitherChildcareVouchersIfNotSureThenNo = SessionDataOverwrite.overwrite[YesNoUnsureEnum](_:CacheMap,EitherGetsVouchersId.toString, YesNoUnsureEnum.NOTSURE, YesNoUnsureEnum.YES)

    (childCareCostsIfNotYetThenYes andThen  approvedProviderIfNotSureThenYes andThen
      yourAdjustedTaxCodeIfNotSureThenNo andThen partnerAdjustedTaxCodeIfNotSureThenNo andThen
      partnerChildcarePartnerIfNotSureThenNo andThen yourChildcareVouchersIfNotSureThenNo andThen eitherChildcareVouchersIfNotSureThenNo)(userData)
  }

  def overwrite[T: Format](userSelections: CacheMap, elementToOverwrite: String, valueToReplace: T, valueToReplaceWith: T): CacheMap = {
    userSelections.data.get(elementToOverwrite).fold(userSelections)(itemToOverwrite => {
      if (itemToOverwrite.as[T] == valueToReplace) store(elementToOverwrite, valueToReplaceWith, userSelections) else userSelections
    })
  }
}
