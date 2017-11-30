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

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsNumber, JsString}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoNotYetEnum.YesNoNotYetEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoUnsureEnum.YesNoUnsureEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YesNoNotYetEnum, YesNoUnsureEnum}
import uk.gov.hmrc.http.cache.client.CacheMap

class SessionDataOverwriterSpec extends PlaySpec {

  "Session Data Ovewriter" must {
    "overwrite value" when {
      "the value exists in CacheMap and matches criteria" in {
        val data = new CacheMap("id", Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NOTYET.toString)))
        val result = SessionDataOverwrite.overwrite[YesNoNotYetEnum](data, ChildcareCostsId.toString, YesNoNotYetEnum.NOTYET, YesNoNotYetEnum.YES)
        result.data(ChildcareCostsId.toString).as[YesNoNotYetEnum] mustBe YesNoNotYetEnum.YES
      }
    }

    "not overwrite anything" when {
      "the value exists in CacheMap but does not match our criteria" in {
        val data = new CacheMap("id", Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString)))
        val result = SessionDataOverwrite.overwrite[YesNoNotYetEnum](data, ChildcareCostsId.toString, YesNoNotYetEnum.NOTYET, YesNoNotYetEnum.YES)
        result.data(ChildcareCostsId.toString).as[YesNoNotYetEnum] mustBe YesNoNotYetEnum.YES
      }

      "the value does not exist in our CacheMap" in {
        val data = new CacheMap("id", Map("test" -> JsString(YesNoNotYetEnum.NOTYET.toString)))
        val result = SessionDataOverwrite.overwrite[YesNoNotYetEnum](data, ChildcareCostsId.toString, YesNoNotYetEnum.NOTYET, YesNoNotYetEnum.YES)
        result.data.get(ChildcareCostsId.toString) mustBe None
      }

      "there is other values but not the one we need" in {
        val data = new CacheMap("id", Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NOTYET.toString), "test" -> JsNumber(5)))
        val result = SessionDataOverwrite.overwrite[YesNoNotYetEnum](data, ChildcareCostsId.toString, YesNoNotYetEnum.NOTYET, YesNoNotYetEnum.YES)
        result.data("test").as[Int] mustBe 5
      }
    }

    "do multiple overwrites" when {
      "we have two elements to overwrite" in {
        val data = new CacheMap("id", Map("test1" -> JsString(YesNoNotYetEnum.NOTYET.toString), "test2" -> JsString(YesNoUnsureEnum.NOTSURE.toString)))

        val update = SessionDataOverwrite.overwrite[YesNoNotYetEnum](data, "test1", YesNoNotYetEnum.NOTYET, YesNoNotYetEnum.YES)
        val update2 = SessionDataOverwrite.overwrite[YesNoUnsureEnum](update, "test2", YesNoUnsureEnum.NOTSURE, YesNoUnsureEnum.NO)

        update2.data("test1").as[YesNoNotYetEnum] mustBe YesNoNotYetEnum.YES
        update2.data("test2").as[YesNoUnsureEnum] mustBe YesNoUnsureEnum.NO
      }

      "we have all required business elements to overwrite" in {
        val data = new CacheMap("id", Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NOTYET.toString),
          ApprovedProviderId.toString -> JsString(YesNoUnsureEnum.NOTSURE.toString),
          DoYouKnowYourAdjustedTaxCodeId.toString -> JsString(YesNoUnsureEnum.NOTSURE.toString),
          DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsString(YesNoUnsureEnum.NOTSURE.toString),
          PartnerChildcareVouchersId.toString -> JsString(YesNoUnsureEnum.NOTSURE.toString),
          YourChildcareVouchersId.toString -> JsString(YesNoUnsureEnum.NOTSURE.toString),
          EitherGetsVouchersId.toString -> JsString(YesNoUnsureEnum.NOTSURE.toString)))

        val result = SessionDataOverwrite.applyRules(data)

        result.data(ChildcareCostsId.toString).as[YesNoNotYetEnum] mustBe YesNoNotYetEnum.YES
        result.data(ApprovedProviderId.toString).as[YesNoUnsureEnum] mustBe YesNoUnsureEnum.YES
        result.data(DoYouKnowYourAdjustedTaxCodeId.toString).as[YesNoUnsureEnum] mustBe YesNoUnsureEnum.NO
        result.data(DoYouKnowYourPartnersAdjustedTaxCodeId.toString).as[YesNoUnsureEnum] mustBe YesNoUnsureEnum.NO
        result.data(PartnerChildcareVouchersId.toString).as[YesNoUnsureEnum] mustBe YesNoUnsureEnum.YES
        result.data(YourChildcareVouchersId.toString).as[YesNoUnsureEnum] mustBe YesNoUnsureEnum.YES
        result.data(EitherGetsVouchersId.toString).as[YesNoUnsureEnum] mustBe YesNoUnsureEnum.YES
      }
    }
  }
}
