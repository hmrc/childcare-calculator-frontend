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


import org.joda.time.LocalDate
import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, ChildcarePayFrequency, DisabilityBenefits}
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, DataGenerator, SpecBase}

class ChildrenCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {
  val over19 = LocalDate.now.minusYears(19).minusDays(1)
  val over16 = LocalDate.now.minusYears(16).minusDays(1)
  val exact15 = LocalDate.now.minusYears(15).plusMonths(1)
  val under16 = LocalDate.now

  val childStartEducationDate = new LocalDate(2017, 2, 1)

  lazy val disabilityBenefits: String = DisabilityBenefits.DISABILITY_BENEFITS.toString
  lazy val higherRateDisabilityBenefits: String = DisabilityBenefits.HIGHER_DISABILITY_BENEFITS.toString

  lazy val weekly: String = ChildcarePayFrequency.WEEKLY.toString
  lazy val monthly: String = ChildcarePayFrequency.MONTHLY.toString


  "Children Journey" when {
    "Save noOfChildren data " must {
      "remove relevant data in child journey when noOfChildren value is changed" in {
        val result = cascadeUpsert(NoOfChildrenId.toString, 4, DataGenerator.sample)

        result.data mustBe Map(NoOfChildrenId.toString -> JsNumber(4))
      }

      "remove relevant data in child journey when noOfChildren value is changed from single child" in {
        val result = cascadeUpsert(NoOfChildrenId.toString, 4, DataGenerator.sample)

        result.data mustBe Map(NoOfChildrenId.toString -> JsNumber(4))
      }
    }

    "Save aboutYourChild data " must {
      "remove child education data when there is no child age above 16" in {
        val result = cascadeUpsert(AboutYourChildId.toString, Map("0" -> Json.toJson(AboutYourChild("Foo", under16))), DataGenerator.sample)

        result.data.get(ChildApprovedEducationId.toString) mustBe None
        result.data.get(ChildStartEducationId.toString) mustBe None
      }
    }

    "Save childApprovedEducation data " must {
      "remove childEducationStartDate data when children with age above 19 and below 20 selects no for childApprovedEducation" in {
        val sampleData = DataGenerator().overWriteObject(AboutYourChildId.toString(),Json.obj("0" -> Json.toJson(AboutYourChild("Foo", over19)), "1" -> Json.toJson(AboutYourChild("Bar", over19)),
          "2" -> Json.toJson(AboutYourChild("Raz", over19)), "3" -> Json.toJson(AboutYourChild("Baz", over16)), "4" -> Json.toJson(AboutYourChild("Quux", under16))))
          .overWriteObject(ChildStartEducationId.toString, Json.obj("0" -> childStartEducationDate, "1" -> childStartEducationDate, "2" -> childStartEducationDate))

        val result = cascadeUpsert(ChildApprovedEducationId.toString, Json.toJson(Map("0" -> false, "1" -> false)), sampleData.sample)
        result.data.get(ChildStartEducationId.toString) mustBe Some(Json.obj("2" -> childStartEducationDate))
      }

      "remove childEducationStartDate data when children with age above 19 and below 20 and children between 16 to 18 selects no for childApprovedEducation" in {
        val sampleData = DataGenerator().overWriteObject(AboutYourChildId.toString(), Json.obj("0" -> Json.toJson(AboutYourChild("Foo", over19)),
          "1" -> Json.toJson(AboutYourChild("Bar", over19)), "2" -> Json.toJson(AboutYourChild("Raz", over19)), "3" -> Json.toJson(AboutYourChild("Baz", over16))))
          .overWriteObject(ChildApprovedEducationId.toString, Json.obj("0" -> true, "1" -> true, "2" -> true, "3" -> false))
          .overWriteObject(ChildStartEducationId.toString, Json.obj("0" -> childStartEducationDate, "1" -> childStartEducationDate, "2" -> childStartEducationDate))

        val result = cascadeUpsert(ChildApprovedEducationId.toString, Json.toJson(Map("1" -> false, "2" -> false, "3" -> true)), sampleData.sample)
        result.data.get(ChildStartEducationId.toString) mustBe Some(Json.obj("0" -> childStartEducationDate))
      }
    }

    "Save childrenDisabilityBenefits data " must {
      "remove whichChildrenDisability and whichDisabilityBenefits data when childrenDisabilityBenefits is false" in {
        val result = cascadeUpsert(ChildrenDisabilityBenefitsId.toString, false, DataGenerator.sample)

        result.data.get(WhichDisabilityBenefitsId.toString) mustBe None
        result.data.get(WhichChildrenDisabilityId.toString) mustBe None
      }

      "remove whichDisabilityBenefits data when childDisabilityBenefits is false" in {
        val result = cascadeUpsert(ChildDisabilityBenefitsId.toString, false, DataGenerator.sample)

        result.data.get(WhichDisabilityBenefitsId.toString) mustBe None
      }
    }

    "Save whichChildrenDisability data " must {
      "remove whichDisabilityBenefits data accordingly when childrenDisabilityBenefits is changed" in {
        val result = cascadeUpsert(WhichChildrenDisabilityId.toString, Json.toJson(Seq(0, 1)), DataGenerator.sample)

        result.data.get(WhichDisabilityBenefitsId.toString) mustBe Some(Json.obj("0" -> Seq(disabilityBenefits)))
      }


      "remove whichDisabilityBenefits data accordingly when childrenDisabilityBenefits is changed for 5 children " in {
        val data = DataGenerator().overWriteObject(WhichChildrenDisabilityId.toString, Json.toJson(Seq(0, 1, 2, 4)))
          .overWriteObject(WhichDisabilityBenefitsId.toString, Json.obj("0" -> Seq(disabilityBenefits), "1" -> Seq(higherRateDisabilityBenefits),
          "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits), "4" -> Seq(higherRateDisabilityBenefits)))

        val result = cascadeUpsert(WhichChildrenDisabilityId.toString, Json.toJson(Seq(0, 3)), data.sample)
        result.data.get(WhichDisabilityBenefitsId.toString) mustBe Some(Json.obj("0" -> Seq(disabilityBenefits)))
      }
    }

    "Save registeredBlind data " must {
      "remove whichChildrenBlind data when registeredBlind is false" in {
        val originalCacheMap = DataGenerator().overWriteObject(WhichChildrenBlindId.toString, Json.toJson(Seq(0, 2)))

        val result = cascadeUpsert(RegisteredBlindId.toString, false, originalCacheMap.sample)
        result.data.get(WhichChildrenBlindId.toString) mustBe None
      }
    }


    "Save whoHasChildcareCosts data " must {
      "remove childcarePayFrequency and expectedChildcareCosts data accordingly when whoHasChildcareCosts is changed " in {
        val originalCacheMap = DataGenerator().overWriteObject(WhoHasChildcareCostsId.toString, Json.toJson(Seq(0, 1)))
          .overWriteObject(ChildcarePayFrequencyId.toString, Json.obj("0" -> monthly, "1" -> weekly))
          .overWriteObject(ExpectedChildcareCostsId.toString, Json.obj("0" -> JsNumber(123), "1" -> JsNumber(224)))

        val result = cascadeUpsert(WhoHasChildcareCostsId.toString, Json.toJson(Seq(0, 2)), originalCacheMap.sample)
        result.data.get(ChildcarePayFrequencyId.toString) mustBe Some(Json.obj("0" -> monthly))
        result.data.get(ExpectedChildcareCostsId.toString) mustBe Some(Json.obj("0" -> JsNumber(123)))
      }

      "remove childcarePayFrequency and expectedChildcareCosts data accordingly when whoHasChildcareCosts is changed for 5 children " in {
        val originalCacheMap = DataGenerator().overWriteObject(WhoHasChildcareCostsId.toString, Json.toJson(Seq(0, 1, 3, 4)))
          .overWriteObject(ChildcarePayFrequencyId.toString, Json.obj("0" -> monthly, "1" -> weekly, "3" -> weekly, "4" -> weekly))
          .overWriteObject(ExpectedChildcareCostsId.toString, Json.obj("0" -> JsNumber(123), "1" -> JsNumber(224), "3" -> JsNumber(500), "4" -> JsNumber(340)))

        val result = cascadeUpsert(WhoHasChildcareCostsId.toString, Json.toJson(Seq(0, 4)), originalCacheMap.sample)
        result.data.get(ChildcarePayFrequencyId.toString) mustBe Some(Json.obj("0" -> monthly, "4" -> weekly))
        result.data.get(ExpectedChildcareCostsId.toString) mustBe Some(Json.obj("0" -> JsNumber(123), "4" -> JsNumber(340)))
      }
    }
  }
}
