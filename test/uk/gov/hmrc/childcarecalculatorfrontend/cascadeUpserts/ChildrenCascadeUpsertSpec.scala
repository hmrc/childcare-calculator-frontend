/*
 * Copyright 2022 HM Revenue & Customs
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
import play.api.libs.json.JodaWrites._
import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.DataGenerator.{ageExactly15Relative, ageOf19YearsAgo, ageOfOver16Relative}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, ChildcarePayFrequency, DisabilityBenefits}
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, DataGenerator, SpecBase}

class ChildrenCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {
  private val testDate: LocalDate = LocalDate.parse("2014-01-01")

  private val ageOf19: LocalDate            = ageOf19YearsAgo(testDate)
  private val ageOfOver16: LocalDate        = ageOfOver16Relative(testDate)
  private val ageOfExactly15: LocalDate     = ageExactly15Relative(testDate)

  private val childStartEducationDate: LocalDate = new LocalDate(2017, 2, 1)

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
        val result = cascadeUpsert(AboutYourChildId.toString, Map("0" -> Json.toJson(AboutYourChild("Foo", ageOfExactly15))), DataGenerator.sample)

        result.data.get(ChildApprovedEducationId.toString) mustBe None
        result.data.get(ChildStartEducationId.toString) mustBe None
      }
    }

    "Save childApprovedEducation data " must {
      "not remove anything if object is not present" in {
        val data = DataGenerator().deleteObject(ChildStartEducationId.toString)

        val result = cascadeUpsert(ChildApprovedEducationId.toString, Json.toJson(Map("0" -> false, "1" -> false)), data.sample)

        result.data.get(ChildStartEducationId.toString) mustBe None
      }
      "remove childEducationStartDate data when children with age above 19 and below 20 selects no for childApprovedEducation" in {
        val sampleData = DataGenerator().overWriteObject(AboutYourChildId.toString(), Json.obj("0" -> Json.toJson(AboutYourChild("Foo", ageOf19)), "1" -> Json.toJson(AboutYourChild("Bar", ageOf19)),
          "2" -> Json.toJson(AboutYourChild("Raz", ageOf19)), "3" -> Json.toJson(AboutYourChild("Baz", ageOfOver16)), "4" -> Json.toJson(AboutYourChild("Quux", ageOfExactly15))))
          .overWriteObject(ChildStartEducationId.toString, Json.obj("0" -> childStartEducationDate, "1" -> childStartEducationDate, "2" -> childStartEducationDate))

        val result = cascadeUpsert(ChildApprovedEducationId.toString, Json.toJson(Map("0" -> false, "1" -> false)), sampleData.sample)
        result.data.get(ChildStartEducationId.toString) mustBe Some(Json.obj("2" -> childStartEducationDate))
      }

      "remove childEducationStartDate data when children with age above 19 and below 20 and children between 16 to 18 selects no for childApprovedEducation" in {
        val sampleData = DataGenerator().overWriteObject(AboutYourChildId.toString(), Json.obj("0" -> Json.toJson(AboutYourChild("Foo", ageOf19)),
          "1" -> Json.toJson(AboutYourChild("Bar", ageOf19)), "2" -> Json.toJson(AboutYourChild("Raz", ageOf19)), "3" -> Json.toJson(AboutYourChild("Baz", ageOfOver16))))
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

      "Save whichChildrenDisability data " must {
        "not remove anything if there is no object" in {
          val data = DataGenerator().deleteObject(WhichDisabilityBenefitsId.toString)

          val result = cascadeUpsert(WhichChildrenDisabilityId.toString, Json.toJson(Seq(0, 2)), data.sample)

          result.data.get(WhichDisabilityBenefitsId.toString) mustBe None
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
}