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
import play.api.libs.json.{JsBoolean, JsNumber, JsString, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, ChildcarePayFrequency, DisabilityBenefits}
import uk.gov.hmrc.childcarecalculatorfrontend.{CascadeUpsertBase, SpecBase}
import uk.gov.hmrc.http.cache.client.CacheMap

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

        val originalCacheMap = new CacheMap("id", Map(
          NoOfChildrenId.toString -> JsNumber(5),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),
            "1" -> Json.toJson(AboutYourChild("Bar", over16)),
            "2" -> Json.toJson(AboutYourChild("Quux", exact15)),
            "3" -> Json.toJson(AboutYourChild("Baz", under16)),
            "4" -> Json.toJson(AboutYourChild("Raz", under16))),
          ChildApprovedEducationId.toString -> Json.obj(
            "0" -> true,
            "1" -> true
          ),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits),
            "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
          ),
          RegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "3" -> JsNumber(123),
            "4" -> JsNumber(224))
        ))

        val result = cascadeUpsert(NoOfChildrenId.toString, 4, originalCacheMap)

        result.data mustBe Map(NoOfChildrenId.toString -> JsNumber(4))

      }


      "remove relevant data in child journey when noOfChildren value is changed from single child" in {

        val originalCacheMap = new CacheMap("id", Map(
          NoOfChildrenId.toString -> JsNumber(1),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),

            ChildApprovedEducationId.toString -> Json.obj(
              "0" -> true
            ),
            ChildStartEducationId.toString -> Json.obj(
              "0" -> childStartEducationDate
            ),
            ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
            WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0)),
            WhichDisabilityBenefitsId.toString -> Json.obj(
              "0" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)),
            RegisteredBlindId.toString -> JsBoolean(true),
            WhichChildrenBlindId.toString -> Json.toJson(Seq(0)),
            WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
            ChildcarePayFrequencyId.toString -> Json.obj(
              "0" -> monthly
            ),
            ExpectedChildcareCostsId.toString -> Json.obj(
              "0" -> JsNumber(123))

          )))

        val result = cascadeUpsert(NoOfChildrenId.toString, 4, originalCacheMap)

        result.data mustBe Map(NoOfChildrenId.toString -> JsNumber(4))

      }
    }

    "Save aboutYourChild data " must {
      "remove child education data when there is no child age above 16" in {

        val aboutChildrenMap = Map(
          "0" -> Json.toJson(AboutYourChild("Foo", under16)),
          "1" -> Json.toJson(AboutYourChild("Bar", under16)),
          "2" -> Json.toJson(AboutYourChild("Quux", exact15)),
          "3" -> Json.toJson(AboutYourChild("Baz", under16)),
          "4" -> Json.toJson(AboutYourChild("Raz", under16)))

        val originalCacheMap = new CacheMap("id", Map(

          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),
            "1" -> Json.toJson(AboutYourChild("Bar", over19)),
            "2" -> Json.toJson(AboutYourChild("Quux", exact15)),
            "3" -> Json.toJson(AboutYourChild("Baz", under16)),
            "4" -> Json.toJson(AboutYourChild("Raz", under16))),

          ChildApprovedEducationId.toString -> Json.obj(
            "0" -> true,
            "1" -> true
          ),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits),
            "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
          ),
          RegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "3" -> JsNumber(123),
            "4" -> JsNumber(224))
        ))


        val result = cascadeUpsert(AboutYourChildId.toString, aboutChildrenMap, originalCacheMap)

        result.data mustBe Map(AboutYourChildId.toString -> Json.toJson(aboutChildrenMap), ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits),
            "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
          ),
          RegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "3" -> JsNumber(123),
            "4" -> JsNumber(224))
        )

      }


      "remove the child education data when the child above 16 are changed" in {

        val aboutChildrenMap = Map(
          "0" -> Json.toJson(AboutYourChild("Foo", under16)),
          "1" -> Json.toJson(AboutYourChild("Bar", under16)),
          "2" -> Json.toJson(AboutYourChild("Quux", exact15)),
          "3" -> Json.toJson(AboutYourChild("Baz", over19)),
          "4" -> Json.toJson(AboutYourChild("Raz", over19)))

        val originalCacheMap = new CacheMap("id", Map(

          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),
            "1" -> Json.toJson(AboutYourChild("Bar", over19)),
            "2" -> Json.toJson(AboutYourChild("Quux", exact15)),
            "3" -> Json.toJson(AboutYourChild("Baz", under16)),
            "4" -> Json.toJson(AboutYourChild("Raz", under16))),

          ChildApprovedEducationId.toString -> Json.obj(
            "0" -> true,
            "1" -> true
          ),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits),
            "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
          ),
          RegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "3" -> JsNumber(123),
            "4" -> JsNumber(224))
        ))


        val result = cascadeUpsert(AboutYourChildId.toString, aboutChildrenMap, originalCacheMap)

        result.data mustBe Map(AboutYourChildId.toString -> Json.toJson(aboutChildrenMap), ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits),
            "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
          ),
          RegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "3" -> JsNumber(123),
            "4" -> JsNumber(224))
        )

      }
    }

    "Save childApprovedEducation data " must {
      "remove childEducationStartDate data when children with age above 19 and below 20 selects no for childApprovedEducation" in {

        val originalCacheMap = new CacheMap("id", Map(
          NoOfChildrenId.toString -> JsNumber(3),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),
            "1" -> Json.toJson(AboutYourChild("Bar", over16)),
            "2" -> Json.toJson(AboutYourChild("Quux", under16))),

          ChildApprovedEducationId.toString -> Json.obj("0" -> true, "1" -> true),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits),
            "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
          ),
          RegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "0" -> JsNumber(123),
            "2" -> JsNumber(224))
        ))

        val result = cascadeUpsert(ChildApprovedEducationId.toString, Json.toJson(Map("0" -> false, "1" -> true)), originalCacheMap)

        result.data mustBe Map(
          NoOfChildrenId.toString -> JsNumber(3),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),
            "1" -> Json.toJson(AboutYourChild("Bar", over16)),
            "2" -> Json.toJson(AboutYourChild("Quux", under16))),

          ChildApprovedEducationId.toString -> Json.obj("0" -> false, "1" -> true),

          ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),

          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),

          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits),
            "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
          ),
          RegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "0" -> JsNumber(123),
            "2" -> JsNumber(224)))
      }
    }


    "Save childrenDisabilityBenefits data " must {
      "remove whichChildrenDisability and whichDisabilityBenefits data when childrenDisabilityBenefits is no" in {

        val originalCacheMap = new CacheMap("id", Map(
          NoOfChildrenId.toString -> JsNumber(3),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),
            "1" -> Json.toJson(AboutYourChild("Bar", over16)),
            "2" -> Json.toJson(AboutYourChild("Quux", under16))),

          ChildApprovedEducationId.toString -> Json.obj("0" -> true, "1" -> true),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits),
            "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
          ),
          RegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "0" -> JsNumber(123),
            "2" -> JsNumber(224))
        ))

        val result = cascadeUpsert(ChildrenDisabilityBenefitsId.toString, false, originalCacheMap)

        result.data mustBe Map(
          NoOfChildrenId.toString -> JsNumber(3),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),
            "1" -> Json.toJson(AboutYourChild("Bar", over16)),
            "2" -> Json.toJson(AboutYourChild("Quux", under16))),

          ChildApprovedEducationId.toString -> Json.obj("0" -> true, "1" -> true),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
          RegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "0" -> JsNumber(123),
            "2" -> JsNumber(224)))
      }
    }

    "Save childDisabilityBenefits data " must {
      "remove whichDisabilityBenefits data when childDisabilityBenefits is no" in {

        val originalCacheMap = new CacheMap("id", Map(
          NoOfChildrenId.toString -> JsNumber(1),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19))),

          ChildApprovedEducationId.toString -> Json.obj("0" -> true),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits)),
          ChildRegisteredBlindId.toString -> JsBoolean(true),

          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "0" -> JsNumber(123))))

        val result = cascadeUpsert(ChildDisabilityBenefitsId.toString, false, originalCacheMap)

        result.data mustBe Map(
          NoOfChildrenId.toString -> JsNumber(1),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19))),

          ChildApprovedEducationId.toString -> Json.obj("0" -> true),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildDisabilityBenefitsId.toString -> JsBoolean(false),
          ChildRegisteredBlindId.toString -> JsBoolean(true),

          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "0" -> JsNumber(123)))
      }
    }


    "Save whichChildrenDisability data " must {
      "remove whichDisabilityBenefits data accordingly when childrenDisabilityBenefits is changed" in {

        val originalCacheMap = new CacheMap("id", Map(
          NoOfChildrenId.toString -> JsNumber(3),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),
            "1" -> Json.toJson(AboutYourChild("Bar", over16)),
            "2" -> Json.toJson(AboutYourChild("Quux", under16))),

          ChildApprovedEducationId.toString -> Json.obj("0" -> true, "1" -> true),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits),
            "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
          ),
          RegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "0" -> JsNumber(123),
            "2" -> JsNumber(224))
        ))

        val result = cascadeUpsert(WhichChildrenDisabilityId.toString, Json.toJson(Seq(0, 1)), originalCacheMap)

        result.data mustBe Map(
          NoOfChildrenId.toString -> JsNumber(3),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),
            "1" -> Json.toJson(AboutYourChild("Bar", over16)),
            "2" -> Json.toJson(AboutYourChild("Quux", under16))),

          ChildApprovedEducationId.toString -> Json.obj("0" -> true, "1" -> true),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 1)),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits)
          ),
          RegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "0" -> JsNumber(123),
            "2" -> JsNumber(224))
        )
      }


    "remove whichDisabilityBenefits data accordingly when childrenDisabilityBenefits is changed for 5 children " in {

      val originalCacheMap = new CacheMap("id", Map(
        NoOfChildrenId.toString -> JsNumber(3),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", over19)),
          "1" -> Json.toJson(AboutYourChild("Bar", over16)),
          "2" -> Json.toJson(AboutYourChild("Quux", exact15)),
          "3" -> Json.toJson(AboutYourChild("Baz", under16)),
          "4" -> Json.toJson(AboutYourChild("Raz", under16))),
        ChildApprovedEducationId.toString -> Json.obj("0" -> true, "1" -> true),
        ChildStartEducationId.toString -> Json.obj(
          "0" -> childStartEducationDate
        ),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 1, 2, 4)),
        WhichDisabilityBenefitsId.toString -> Json.obj(
          "0" -> Seq(disabilityBenefits),
          "1" -> Seq(higherRateDisabilityBenefits),
          "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits),
          "4" -> Seq(higherRateDisabilityBenefits)
        ),
        RegisteredBlindId.toString -> JsBoolean(true),
        WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
        WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
        ChildcarePayFrequencyId.toString -> Json.obj(
          "0" -> monthly,
          "2" -> weekly
        ),
        ExpectedChildcareCostsId.toString -> Json.obj(
          "0" -> JsNumber(123),
          "2" -> JsNumber(224))
      ))

      val result = cascadeUpsert(WhichChildrenDisabilityId.toString, Json.toJson(Seq(0,3)), originalCacheMap)

      result.data mustBe Map(
        NoOfChildrenId.toString -> JsNumber(3),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", over19)),
          "1" -> Json.toJson(AboutYourChild("Bar", over16)),
          "2" -> Json.toJson(AboutYourChild("Quux", exact15)),
          "3" -> Json.toJson(AboutYourChild("Baz", under16)),
          "4" -> Json.toJson(AboutYourChild("Raz", under16))),
        ChildApprovedEducationId.toString -> Json.obj("0" -> true, "1" -> true),
        ChildStartEducationId.toString -> Json.obj(
          "0" -> childStartEducationDate
        ),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0,3)),
        WhichDisabilityBenefitsId.toString -> Json.obj(
          "0" -> Seq(disabilityBenefits)
        ),
        RegisteredBlindId.toString -> JsBoolean(true),
        WhichChildrenBlindId.toString -> Json.toJson(Seq(2)),
        WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
        ChildcarePayFrequencyId.toString -> Json.obj(
          "0" -> monthly,
          "2" -> weekly
        ),
        ExpectedChildcareCostsId.toString -> Json.obj(
          "0" -> JsNumber(123),
          "2" -> JsNumber(224))
      )
    }
  }

  "Save registeredBlind data " must {
      "remove whichChildrenBlind data when registeredBlind is no" in {

        val originalCacheMap = new CacheMap("id", Map(
          NoOfChildrenId.toString -> JsNumber(3),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),
            "1" -> Json.toJson(AboutYourChild("Bar", over16)),
            "2" -> Json.toJson(AboutYourChild("Quux", under16))),

          ChildApprovedEducationId.toString -> Json.obj("0" -> true, "1" -> true),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits),
            "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
          ),
          RegisteredBlindId.toString -> JsBoolean(true),
          WhichChildrenBlindId.toString -> Json.toJson(Seq(0,2)),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "0" -> JsNumber(123),
            "2" -> JsNumber(224))
        ))

        val result = cascadeUpsert(RegisteredBlindId.toString, false, originalCacheMap)

        result.data mustBe Map(
          NoOfChildrenId.toString -> JsNumber(3),
          AboutYourChildId.toString -> Json.obj(
            "0" -> Json.toJson(AboutYourChild("Foo", over19)),
            "1" -> Json.toJson(AboutYourChild("Bar", over16)),
            "2" -> Json.toJson(AboutYourChild("Quux", under16))),

          ChildApprovedEducationId.toString -> Json.obj("0" -> true, "1" -> true),
          ChildStartEducationId.toString -> Json.obj(
            "0" -> childStartEducationDate
          ),
          ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
          WhichDisabilityBenefitsId.toString -> Json.obj(
            "0" -> Seq(disabilityBenefits),
            "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
          ),
          RegisteredBlindId.toString -> JsBoolean(false),
          WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 2)),
          ChildcarePayFrequencyId.toString -> Json.obj(
            "0" -> monthly,
            "2" -> weekly
          ),
          ExpectedChildcareCostsId.toString -> Json.obj(
            "0" -> JsNumber(123),
            "2" -> JsNumber(224))
        )
      }
    }


   "Save whoHasChildcareCosts data " must {
       "remove childcarePayFrequency  and expectedChildcareCosts data accordingly when whoHasChildcareCosts is changed " in {

         val originalCacheMap = new CacheMap("id", Map(
           NoOfChildrenId.toString -> JsNumber(3),
           AboutYourChildId.toString -> Json.obj(
             "0" -> Json.toJson(AboutYourChild("Foo", over19)),
             "1" -> Json.toJson(AboutYourChild("Bar", over16)),
             "2" -> Json.toJson(AboutYourChild("Quux", exact15)),
             "3" -> Json.toJson(AboutYourChild("Baz", under16)),
             "4" -> Json.toJson(AboutYourChild("Raz", under16))),
           ChildApprovedEducationId.toString -> Json.obj("0" -> true, "1" -> true),
           ChildStartEducationId.toString -> Json.obj(
             "0" -> childStartEducationDate
           ),
           ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
           WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
           WhichDisabilityBenefitsId.toString -> Json.obj(
             "0" -> Seq(disabilityBenefits),
             "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
           ),
           RegisteredBlindId.toString -> JsBoolean(true),
           WhichChildrenBlindId.toString -> Json.toJson(Seq(0,1)),
           WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0,1,3,4)),
           ChildcarePayFrequencyId.toString -> Json.obj(
             "0" -> monthly,
             "1" -> weekly,
             "3" -> weekly,
             "4" -> weekly
           ),
           ExpectedChildcareCostsId.toString -> Json.obj(
             "0" -> JsNumber(123),
             "1" -> JsNumber(224),
             "3" -> JsNumber(500),
             "4" -> JsNumber(340))
         ))

         val result = cascadeUpsert(WhoHasChildcareCostsId.toString, Json.toJson(Seq(0, 1, 2, 3)), originalCacheMap)

         result.data mustBe Map(
           NoOfChildrenId.toString -> JsNumber(3),
           AboutYourChildId.toString -> Json.obj(
             "0" -> Json.toJson(AboutYourChild("Foo", over19)),
             "1" -> Json.toJson(AboutYourChild("Bar", over16)),
             "2" -> Json.toJson(AboutYourChild("Quux", exact15)),
             "3" -> Json.toJson(AboutYourChild("Baz", under16)),
             "4" -> Json.toJson(AboutYourChild("Raz", under16))),
           ChildApprovedEducationId.toString -> Json.obj("0" -> true, "1" -> true),
           ChildStartEducationId.toString -> Json.obj(
             "0" -> childStartEducationDate
           ),
           ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
           WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2)),
           WhichDisabilityBenefitsId.toString -> Json.obj(
             "0" -> Seq(disabilityBenefits),
             "2" -> Seq(disabilityBenefits, higherRateDisabilityBenefits)
           ),
           RegisteredBlindId.toString -> JsBoolean(true),
           WhichChildrenBlindId.toString -> Json.toJson(Seq(0,1)),
           WhoHasChildcareCostsId.toString -> Json.toJson(Seq(0, 1,2,3)),
           ChildcarePayFrequencyId.toString -> Json.obj(
             "0" -> monthly,
             "1" -> weekly,
             "3" -> weekly
           ),
           ExpectedChildcareCostsId.toString -> Json.obj(
             "0" -> JsNumber(123),
             "1" -> JsNumber(224),
             "3" -> JsNumber(500),
             "4" -> JsNumber(340))
         )
       }
     }

  }

}
