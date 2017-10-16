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

import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{AreYouSelfEmployedOrApprenticeId, _}
import uk.gov.hmrc.childcarecalculatorfrontend.models.SelfEmployedOrApprenticeOrNeitherEnum
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class CascadeUpsertSpec extends SpecBase {

  "using the apply method for a key that has no special function" when {
    "the key doesn't already exists" must {
      "add the key to the cache map" in {
        val originalCacheMap = new CacheMap("id", Map())
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert("key", "value", originalCacheMap)
        result.data mustBe Map("key" -> JsString("value"))
      }
    }

    "data already exists for that key" must {
      "replace the value held against the key" in {
        val originalCacheMap = new CacheMap("id", Map("key" -> JsString("original value")))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert("key", "new value", originalCacheMap)
        result.data mustBe Map("key" -> JsString("new value"))
      }
    }

    "saving a location of northernIreland" must {
      "remove an existing childAgedTwo key and save the location" in {
        val originalCacheMap = new CacheMap("id", Map(ChildAgedTwoId.toString -> JsBoolean(true)))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(LocationId.toString, "northernIreland", originalCacheMap)
        result.data mustBe Map(LocationId.toString -> JsString("northernIreland"))
      }
    }

    "saving a location other than northernIreland" must {
      "save the location and leave an existing childAgedTwo key in place" in {
        val originalCacheMap = new CacheMap("id", Map(ChildAgedTwoId.toString -> JsBoolean(true)))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(LocationId.toString, "england", originalCacheMap)
        result.data mustBe Map(
          ChildAgedTwoId.toString -> JsBoolean(true),
          LocationId.toString -> JsString("england" +
            "")
        )
      }
    }

    "saving the doYouLiveWithPartner" must {
      "remove an existing paid employment, partners adjusted tax code and who is in paid employment, you or partner get benefits, " +
        "vouchers, partners age, parent and partner self employed or apprentice when doYouLiveWithPartner is No" in {
        val originalCacheMap = new CacheMap("id", Map(PaidEmploymentId.toString -> JsBoolean(true),
          WhoIsInPaidEmploymentId.toString -> JsString(you), PartnerWorkHoursId.toString -> JsString("12"),
          HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsBoolean(true), DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"), EitherGetsVouchersId.toString -> JsString("yes"),
          WhoGetsVouchersId.toString -> JsString("you"), PartnerChildcareVouchersId.toString -> JsString("yes"),
          DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(true), WhoGetsBenefitsId.toString -> JsString("you"),
          YourPartnersAgeId.toString -> JsString("under18"),
          AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
          PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(DoYouLiveWithPartnerId.toString, false, originalCacheMap)
        result.data mustBe Map(DoYouLiveWithPartnerId.toString -> JsBoolean(false))
      }

      "remove an existing paid employment, who is in paid employment when doYouLiveWithpartner is Yes" in {
        val originalCacheMap = new CacheMap("id", Map(AreYouInPaidWorkId.toString -> JsBoolean(true),
          DoYouGetAnyBenefitsId.toString -> JsBoolean(true)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(DoYouLiveWithPartnerId.toString, true, originalCacheMap)
        result.data mustBe Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true))
      }
    }

    "saving the areYouInPaidWork" must {
      "remove an existing parent work hours, parents adjusted tax code, your childcare vouchers, do you get benefits, " +
        "your age when are you in paid work is no" in {
        val originalCacheMap = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12"),
          HasYourTaxCodeBeenAdjustedId.toString -> JsBoolean(true), DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourTaxCodeId.toString -> JsString("1100L"), YourChildcareVouchersId.toString -> JsString("yes"),
          DoYouGetAnyBenefitsId.toString -> JsBoolean(false), YourAgeId.toString -> JsString("under18")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(AreYouInPaidWorkId.toString, false, originalCacheMap)
        result.data mustBe Map(AreYouInPaidWorkId.toString -> JsBoolean(false))
      }
    }

    "saving the are you your partner, or both of you in paid work" must {
      "remove an existing who's in paid work, parent work hours, partner work hours, parents adjusted tax code, partners adjusted tax code," +
        "either child care vouchers, who gets childcare vouchers, your childcare vouchers, partner childcare couchers, do you get benefits, " +
        "your age, partners age when paid employment is no" in {
        val originalCacheMap = new CacheMap("id", Map(WhoIsInPaidEmploymentId.toString -> JsString("both"), ParentWorkHoursId.toString -> JsString("12"),
          PartnerWorkHoursId.toString -> JsString("12"), HasYourTaxCodeBeenAdjustedId.toString -> JsBoolean(true), HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsBoolean(true),
          DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true), DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourTaxCodeId.toString -> JsString("1100L"), WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"), EitherGetsVouchersId.toString -> JsString("yes"),
          WhoGetsVouchersId.toString -> JsString("both"), YourChildcareVouchersId.toString -> JsString("yes"), PartnerChildcareVouchersId.toString -> JsString("yes"),
          DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(true), WhoGetsBenefitsId.toString -> JsString("you"),
          DoYouGetAnyBenefitsId.toString -> JsBoolean(false), YourAgeId.toString -> JsString("under18"), YourPartnersAgeId.toString -> JsString("under18"),
          AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString),
          PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PaidEmploymentId.toString, false, originalCacheMap)
        result.data mustBe Map(PaidEmploymentId.toString -> JsBoolean(false))
      }
    }

    "saving the whoIsInPaidEmployment" must {
      "remove an existing partner work hours, partners adjusted tax code and partner min earnings when whoIsInPaidEmployment is you" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerWorkHoursId.toString -> JsString("12"),
          HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsBoolean(true), DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L"), EitherGetsVouchersId.toString -> JsString("yes"),
          WhoGetsVouchersId.toString -> JsString("you"), YourPartnersAgeId.toString -> JsString("under18"), PartnerMinimumEarningsId.toString -> JsBoolean(true),
          PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, you, originalCacheMap)
        result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(you))
      }

      "remove an existing parent work hours, parent adjusted tax code and your min earnings when whoIsInPaidEmployment is partner" in {
        val originalCacheMap = new CacheMap("id", Map(ParentWorkHoursId.toString -> JsString("12"),
          HasYourTaxCodeBeenAdjustedId.toString -> JsBoolean(true), DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true),
          WhatIsYourTaxCodeId.toString -> JsString("1100L"), EitherGetsVouchersId.toString -> JsString("yes"),
          WhoGetsVouchersId.toString -> JsString("you"), YourAgeId.toString -> JsString("under18"), YourMinimumEarningsId.toString -> JsBoolean(true),
          AreYouSelfEmployedOrApprenticeId.toString -> JsString (SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString)))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, partner, originalCacheMap)
        result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(partner))
      }

      "remove parent childcare vouchers when whoIsInPaidEmployment is both" in {
        val originalCacheMap = new CacheMap("id", Map(YourChildcareVouchersId.toString -> JsString("yes")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(WhoIsInPaidEmploymentId.toString, both, originalCacheMap)
        result.data mustBe Map(WhoIsInPaidEmploymentId.toString -> JsString(both))
      }
    }

    "saving has your tax code been adjusted" must {
      "remove an existing do you know your adjusted tax code and your tax code when has your tax code been adjusted is no" in {
        val originalCacheMap = new CacheMap("id", Map(DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(true), WhatIsYourTaxCodeId.toString -> JsString("1100L")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(HasYourTaxCodeBeenAdjustedId.toString, false, originalCacheMap)
        result.data mustBe Map(HasYourTaxCodeBeenAdjustedId.toString -> JsBoolean(false))
      }
    }

    "saving do you know your adjusted tax code" must {
      "remove an existing your tax code when do you know adjusted tax code is no" in {
        val originalCacheMap = new CacheMap("id", Map(WhatIsYourTaxCodeId.toString -> JsString("1100L")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(DoYouKnowYourAdjustedTaxCodeId.toString, false, originalCacheMap)
        result.data mustBe Map(DoYouKnowYourAdjustedTaxCodeId.toString -> JsBoolean(false))
      }
    }

    "saving has your partner's tax code been adjusted" must {
      "remove an existing do you know your partner's adjusted tax code and your partner's tax code when has your partner's tax code been adjusted is no" in {
        val originalCacheMap = new CacheMap("id", Map(DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(true), WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(HasYourPartnersTaxCodeBeenAdjustedId.toString, false, originalCacheMap)
        result.data mustBe Map(HasYourPartnersTaxCodeBeenAdjustedId.toString -> JsBoolean(false))
      }
    }

    "saving do you know your partners adjusted tax code" must {
      "remove an existing your partners tax code when do you know your partners adjusted tax code is no" in {
        val originalCacheMap = new CacheMap("id", Map(WhatIsYourPartnersTaxCodeId.toString -> JsString("1100L")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(DoYouKnowYourPartnersAdjustedTaxCodeId.toString, false, originalCacheMap)
        result.data mustBe Map(DoYouKnowYourPartnersAdjustedTaxCodeId.toString -> JsBoolean(false))
      }
    }

    "saving the either childcare vouchers" must {
      "remove an existing who gets vouchers when either childcare vouchers is no" in {
        val originalCacheMap = new CacheMap("id", Map(WhoGetsVouchersId.toString -> JsString("you")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(EitherGetsVouchersId.toString, "no", originalCacheMap)
        result.data mustBe Map(EitherGetsVouchersId.toString -> JsString("no"))
      }
    }

    "saving the your or your partner benefits" must {
      "remove an existing who gets benefits when you or your partner benefits is no" in {
        val originalCacheMap = new CacheMap("id", Map(WhoGetsBenefitsId.toString -> JsString("you")))

        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(DoYouOrYourPartnerGetAnyBenefitsId.toString, false, originalCacheMap)
        result.data mustBe Map(DoYouOrYourPartnerGetAnyBenefitsId.toString -> JsBoolean(false))
      }
    }
  }

"saving the your minimumEarnings" must {
      "remove your maximum earnings when your minimum earnings is no" in {
        val originalCacheMap = new CacheMap("id", Map(YourMaximumEarningsId.toString -> JsBoolean(false)))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YourMinimumEarningsId.toString, false, originalCacheMap)
        result.data mustBe Map(YourMinimumEarningsId.toString -> JsBoolean(false))
      }

      "remove you self employed or apprentice and you self employed less than 12 months when minimum earnings is yes" in {
        val originalCacheMap = new CacheMap("id", Map(AreYouSelfEmployedOrApprenticeId.toString -> JsBoolean(true))) //TODO Add in self employed less than 12 months
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(YourMinimumEarningsId.toString, true, originalCacheMap)
        result.data mustBe Map(YourMinimumEarningsId.toString -> JsBoolean(true))
      }
    }

    "saving the your partners minimumEarnings" must {
      "remove partners maximum earnings when partners minimum earnings is no" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerMaximumEarningsId.toString -> JsBoolean(false)))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerMinimumEarningsId.toString, false, originalCacheMap)
        result.data mustBe Map(PartnerMinimumEarningsId.toString -> JsBoolean(false))
      }

      "remove your partners self employed or apprentice and partners self employed less than 12 months when partners minimum earnings is yes" in {
        val originalCacheMap = new CacheMap("id", Map(PartnerSelfEmployedOrApprenticeId.toString -> JsBoolean(true))) //TODO Add in self employed less than 12 months
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert(PartnerMinimumEarningsId.toString, true, originalCacheMap)
        result.data mustBe Map(PartnerMinimumEarningsId.toString -> JsBoolean(true))
      }
    }

  "saving are you self employed or apprentice" must {
    "remove your self employed selection when parent select apprentice" in {
      val originalCacheMap = new CacheMap("id", Map(YourSelfEmployedId.toString -> JsBoolean(false)))
      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(AreYouSelfEmployedOrApprenticeId.toString, SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString, originalCacheMap)
      result.data mustBe Map(AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString))
    }

    "remove your self employed selection when parent select neither" in {
      val originalCacheMap = new CacheMap("id", Map(YourSelfEmployedId.toString -> JsBoolean(false)))
      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(AreYouSelfEmployedOrApprenticeId.toString, SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString, originalCacheMap)
      result.data mustBe Map(AreYouSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString))
    }
  }

  "saving partner self employed or apprentice" must {
    "remove partner self employed selection when partner select apprentice" in {
      val originalCacheMap = new CacheMap("id", Map(PartnerSelfEmployedId.toString -> JsBoolean(false)))
      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(PartnerSelfEmployedOrApprenticeId.toString, SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString, originalCacheMap)
      result.data mustBe Map(PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString))
    }

    "remove partner self employed selection when partner select neither" in {
      val originalCacheMap = new CacheMap("id", Map(PartnerSelfEmployedId.toString -> JsBoolean(false)))
      val cascadeUpsert = new CascadeUpsert
      val result = cascadeUpsert(PartnerSelfEmployedOrApprenticeId.toString, SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString, originalCacheMap)
      result.data mustBe Map(PartnerSelfEmployedOrApprenticeId.toString -> JsString(SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString))
    }
  }

  "addRepeatedValue" when {
    "the key doesn't already exist" must {
      "add the key to the cache map and save the value in a sequence" in {
        val originalCacheMap = new CacheMap("id", Map())
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert.addRepeatedValue("key", "value", originalCacheMap)
        result.data mustBe Map("key" -> Json.toJson(Seq("value")))
      }
    }

    "the key already exists" must {
      "add the new value to the existing sequence" in {
        val originalCacheMap = new CacheMap("id", Map("key" -> Json.toJson(Seq("value"))))
        val cascadeUpsert = new CascadeUpsert
        val result = cascadeUpsert.addRepeatedValue("key", "new value", originalCacheMap)
        result.data mustBe Map("key" -> Json.toJson(Seq("value", "new value")))
      }
    }
  }
}
