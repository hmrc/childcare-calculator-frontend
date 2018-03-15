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

import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.http.cache.client.CacheMap

class CacheMapClonerSpec extends SpecBase {
  "Cache map cloner" should {
    "mirror a cachemap property as boolean" in {
      val data = new CacheMap("id",Map("property1" -> JsBoolean(true)))

      val result = CacheMapCloner.cloneSection(data,Map("property1"->"property2"))

      result.getEntry[Boolean]("property2") mustBe result.getEntry[Boolean]("property1")
    }

    "mirror two boolean property" in {
      val data = new CacheMap("id", Map("property1" -> JsBoolean(true), "property2" -> JsBoolean(true)))

      val result = CacheMapCloner.cloneSection(data, Map("property1"->"property3", "property2"->"property4"))

      result.getEntry[Boolean]("property1") mustBe result.getEntry[Boolean]("property3")
      result.getEntry[Boolean]("property2") mustBe result.getEntry[Boolean]("property4")
    }

    "mirror three properties with different types" in {
      val data = new CacheMap("id", Map("property1" -> JsBoolean(true), "property2" -> JsBoolean(true), "property3" -> JsNumber(2), "property4" -> JsString("Test")))

      val result = CacheMapCloner.cloneSection(data, Map("property1"->"property5", "property2"->"property6", "property3" -> "property7", "property4" -> "property8"))

      result.getEntry[Boolean]("property1") mustBe result.getEntry[Boolean]("property5")
      result.getEntry[Boolean]("property2") mustBe result.getEntry[Boolean]("property6")
      result.getEntry[Int]("property3") mustBe result.getEntry[Int]("property7")
      result.getEntry[String]("property4") mustBe result.getEntry[String]("property8")
    }

    "be able to handle complex objects" in {
      val data = new CacheMap("id",Map("property1" -> Json.obj("0" -> Json.toJson(4), "1" -> JsBoolean(true))))

      val result = CacheMapCloner.cloneSection(data,Map("property1"->"property2"))

      result.getEntry[JsValue]("property1") mustBe result.getEntry[JsValue]("property2")
    }

    "be able to handle not existing data" in {
      val data = new CacheMap("id",Map("property1" -> JsBoolean(true)))

      val result = CacheMapCloner.cloneSection(data,Map("property2"->"property3"))

      result.getEntry[Boolean]("property2") mustBe result.getEntry[Boolean]("property3")
    }

    "be able to overwrite already existing data" in {
      val data = new CacheMap("id",Map("property1" -> JsBoolean(true), "property2" -> Json.obj("0" -> Json.toJson(4), "1" -> JsBoolean(true))))

      val result = CacheMapCloner.cloneSection(data,Map("property1"->"property2"))

      result.getEntry[Boolean]("property1") mustBe result.getEntry[Boolean]("property2")
    }

    "be able to clone a json object property name accordingly" in {
      val data = new CacheMap("id",Map("employmentIncomeCY" -> Json.obj("parentEmploymentIncomeCY" -> Json.toJson(4), "partnerEmploymentIncomeCY" -> JsBoolean(true))))

      val result = CacheMapCloner.cloneSection(data,Map("employmentIncomeCY"->"employmentIncomePY"))

      result.getEntry[JsValue]("employmentIncomePY").toString() must include("parentEmploymentIncomePY")
    }

    "be able to clone a json object property value accordingly" in {
      val data = new CacheMap("id",Map("employmentIncomeCY" -> Json.obj("parentEmploymentIncomeCY" -> Json.toJson(4), "partnerEmploymentIncomeCY" -> JsBoolean(true))))

      val result = CacheMapCloner.cloneSection(data,Map("employmentIncomeCY"->"employmentIncomePY"))

      result.getEntry[JsValue]("employmentIncomePY").toString() must include("4")
    }

    "be able to handle missing data when mapping a json object" in {
      val data = new CacheMap("id",Map("employmentIncomeCY" -> Json.obj("test" -> Json.toJson(4), "partnerEmploymentIncomeCY" -> JsBoolean(true))))

      val result = CacheMapCloner.cloneSection(data,Map("employmentIncomeCY"->"employmentIncomePY"))

      result.getEntry[JsValue]("employmentIncomePY").toString() must include("mapping not found")
    }

    "be able to handle custom mappings" in {
      val data = new CacheMap("id",Map("property1" -> JsBoolean(true)))

      val result = CacheMapCloner.cloneSection(data,Map("property1"->"property2"),Some(Map("property4" -> JsBoolean(true))))

      result.getEntry[Boolean]("property4").get mustBe true
    }

    "be able to clear cachemap for cloned routes" in {
      val data = new CacheMap("id",Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true),"employmentIncomeCY" -> Json.obj("parentEmploymentIncomeCY" -> Json.toJson(4), "partnerEmploymentIncomeCY" -> JsBoolean(true))))

      val result = CacheMapCloner.removeClonedDataForPreviousYearIncome(data)

      result.getEntry[JsValue]("employmentIncomePY") mustBe None
      result.getEntry[JsValue]("employmentIncomeCY").toString() must include("parentEmploymentIncomeCY")
    }

    "be able to identify if it is a single parent route" in {
      val data = new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(false), ParentEmploymentIncomeCYId.toString -> JsNumber(52)))

      val result = CacheMapCloner.cloneCYIncomeIntoPYIncome(data)

      result.getEntry[BigDecimal](ParentEmploymentIncomePYId.toString) mustBe Some(52)
    }

    "be able to identify if it is a both parent route" in {
      val data = new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true), BothAnyTheseBenefitsCYId.toString -> JsBoolean(true)))

      val result = CacheMapCloner.cloneCYIncomeIntoPYIncome(data)

      result.getEntry[Boolean](BothAnyTheseBenefitsPYId.toString) mustBe Some(true)
    }

    "On single journey, map YourPaidWorkPreviousYear" in {
      val data = new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(false), AreYouInPaidWorkId.toString -> JsBoolean(true)))

      val result = CacheMapCloner.cloneCYIncomeIntoPYIncome(data)

      result.getEntry[Boolean](ParentPaidWorkPYId.toString) mustBe Some(true)
    }

    "On both journey, map properties that dont correlate between CY -> PY" when {
      "When we say that both are working CY then both will be working in PY" in {
        val data = new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true), WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.BOTH.toString)))

        val result = CacheMapCloner.cloneCYIncomeIntoPYIncome(data)

        result.getEntry[Boolean](BothPaidWorkPYId.toString) mustBe Some(true)
      }

      "When we say that no one is working currently then no one will be working PY" in {
        val data = new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true), WhoIsInPaidEmploymentId.toString -> JsString(ChildcareConstants.neither)))

        val result = CacheMapCloner.cloneCYIncomeIntoPYIncome(data)

        result.getEntry[Boolean](BothPaidWorkPYId.toString) mustBe Some(false)
      }

      "When we say both are working then both were working PY" in {
        val data = new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true), WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.BOTH.toString)))

        val result = CacheMapCloner.cloneCYIncomeIntoPYIncome(data)

        result.getEntry[String](WhoWasInPaidWorkPYId.toString) mustBe Some(ChildcareConstants.both)
      }

      "When we say that parent is working but at some point partner has worked this year then both worked PY" in {
        val data = new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true),PartnerPaidWorkCYId.toString -> JsBoolean(true), WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.YOU.toString)))

        val result = CacheMapCloner.cloneCYIncomeIntoPYIncome(data)

        result.getEntry[String](WhoWasInPaidWorkPYId.toString) mustBe Some(ChildcareConstants.both)
      }

      "When we say that only parent is working the only parent worked PY" in {
        val data = new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true),PartnerPaidWorkCYId.toString -> JsBoolean(false), WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.YOU.toString)))

        val result = CacheMapCloner.cloneCYIncomeIntoPYIncome(data)

        result.getEntry[String](WhoWasInPaidWorkPYId.toString) mustBe Some(ChildcareConstants.you)
      }

      "When we say that partner is working but at some point parent worked this year then both worked PY" in {
        val data = new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true),ParentPaidWorkCYId.toString -> JsBoolean(true), WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.PARTNER.toString)))

        val result = CacheMapCloner.cloneCYIncomeIntoPYIncome(data)

        result.getEntry[String](WhoWasInPaidWorkPYId.toString) mustBe Some(ChildcareConstants.both)
      }

      "When we say that only partner is working then only partner worked PY" in {
        val data = new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true),ParentPaidWorkCYId.toString -> JsBoolean(false), WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.PARTNER.toString)))

        val result = CacheMapCloner.cloneCYIncomeIntoPYIncome(data)

        result.getEntry[String](WhoWasInPaidWorkPYId.toString) mustBe Some(ChildcareConstants.partner)
      }
    }
  }
}
