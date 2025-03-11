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

import org.scalatest.OptionValues
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._
import uk.gov.hmrc.childcarecalculatorfrontend.DataGenerator._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefits.{CarersAllowance, IncapacityBenefit, SevereDisablementAllowance}
import uk.gov.hmrc.childcarecalculatorfrontend.models._

import java.time.LocalDate

class UserAnswersSpec extends PlaySpec with OptionValues {

  private val testDate: LocalDate = LocalDate.now
  private val ageOf19: LocalDate = ageOf19YearsAgo(testDate)
  private val ageOf16Before31Aug: LocalDate = ageOf16WithBirthdayBefore31stAugust(testDate)
  private val ageOf16Over: LocalDate = ageOfOver16Relative(testDate)
  private val ageOfUnder16: LocalDate = ageUnder16Relative(testDate)
  private val ageOfExactly16: LocalDate = ageExactly16Relative(testDate)

  def cacheMap(answers: (String, JsValue)*): CacheMap =
    CacheMap("", Map(answers: _*))

  def helper(map: CacheMap = cacheMap()): UserAnswers =
    new UserAnswers(map) {
      override def now: LocalDate = testDate
    }

  def vouchersHelper(map: CacheMap = cacheMap(), checkVouchersBoth: Option[Boolean] = None): UserAnswers =
    new UserAnswers(map) {
      override def now: LocalDate = testDate

      override def checkVouchersForBoth: Option[Boolean] = checkVouchersBoth
    }

  "return partner when user lives with partner and the answer to whoIsInPaidEmployment returns 'partner'" in {
    val answers: CacheMap = cacheMap(
      WhoIsInPaidEmploymentId.toString -> JsString("partner"),
      DoYouLiveWithPartnerId.toString -> JsBoolean(true)
    )
    helper(answers).isYouPartnerOrBoth(Some("partner")) mustEqual "partner"
  }

  "return both when user lives with partner and the answer to whoIsInPaidEmployment returns 'both'" in {
    val answers: CacheMap = cacheMap(
      WhoIsInPaidEmploymentId.toString -> JsString("both"),
      DoYouLiveWithPartnerId.toString -> JsBoolean(true)
    )
    helper(answers).isYouPartnerOrBoth(Some("both")) mustEqual "both"
  }

  "return you when the answer to whoIsInPaidEmployment returns 'you'" in {
    val answers: CacheMap = cacheMap(
      WhoIsInPaidEmploymentId.toString -> JsString("you"),
      DoYouLiveWithPartnerId.toString -> JsBoolean(true)
    )
    helper(answers).isYouPartnerOrBoth(Some("you")) mustEqual "you"
  }

  "return you when user does not live with partner" in {
    val answers: CacheMap = cacheMap(
      DoYouLiveWithPartnerId.toString -> JsBoolean(false)
    )
    helper(answers).isYouPartnerOrBoth(Some("you")) mustEqual "you"
  }

  private val quux = "Quux"
  private val foo = "Foo"
  private val bar = "Bar"

  ".childrenOver16" must {

    "return no children over 16" in {
      val answers: CacheMap = cacheMap(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(foo, ageOfUnder16)),
          "1" -> Json.toJson(AboutYourChild("Baz", ageOfUnder16))
        )
      )

      val result = helper(answers).childrenOver16
      result.get.size mustBe 0
    }

    "return any children who are over 16" in {

      val answers: CacheMap = cacheMap(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Over)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOfUnder16)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOfUnder16)),
          "3" -> Json.toJson(AboutYourChild("Baz", ageOf16Over)),
          "4" -> Json.toJson(AboutYourChild("Josh", ageOf19))
        )

      )
      println(answers)
      val result = helper(answers).childrenOver16
      print(result)
      result.value must contain(0 -> AboutYourChild(
        foo, ageOf16Over))
      result.value must contain(3 -> AboutYourChild("Baz", ageOf16Over))
      result.value must contain(4 -> AboutYourChild("Josh", ageOf19))
    }

    "return `None` when there are no children defined" in {
      val answers: CacheMap = cacheMap()
      helper(answers).childrenOver16 mustNot be(defined)
    }
  }

  "extract16YearsOldWithBirthdayBefore31stAugust" must {
    "return the number of children of 16 years and dob before 31st August" in {
      val answers: CacheMap = cacheMap(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOfExactly16)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOfExactly16)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOf19))
        )
      )

      val parametersMap = Map(
        0 -> AboutYourChild(
          foo, ageOfExactly16),
        1 -> AboutYourChild(bar, ageOfExactly16),
        2 -> AboutYourChild(quux, ageOf19))


      val result = helper(answers).extract16YearOldsWithBirthdayBefore31stAugust(Some(parametersMap))

      result.value must contain(0 -> AboutYourChild(
        foo, ageOfExactly16))
      result.value must contain(1 -> AboutYourChild(bar, ageOfExactly16))
    }
  }

  "is16ThisYearAndDateOfBirthIsAfter31stAugust" must {
    "not return any children who are over 16 but Birthday is before 31st of August" in {
      val answers: CacheMap = cacheMap(
        AboutYourChildId.toString -> Json.obj("0" -> Json.toJson(AboutYourChild(
          foo, ageOf16Before31Aug)))
      )
      val result = helper(answers).childrenOver16
      result.get.size mustBe 0
    }
  }

  "childrenIdsForAgeBelow16" must {
    "return the seq of child ids who are less than 16 years old and exactly 16 whose dob is before 31st of august " in {

      val answers: CacheMap = cacheMap(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Over)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOfUnder16)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOfUnder16)),
          "3" -> Json.toJson(AboutYourChild("Baz", ageOf16Before31Aug))
        )
      )

      val result: Seq[Int] = helper(answers).childrenIdsForAgeExactly16
      result mustEqual Seq(3)
    }

    "return the empty sequence when children Map has None" in {
      val answers: CacheMap = cacheMap()
      val result: Seq[Int] = helper(answers).childrenIdsForAgeExactly16
      result mustEqual Seq()
    }
  }

  "hasChildEligibleForTfc" must {
    "return false if 1 child that is over 11 and not disabled" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(foo, ageOfExactly16))
        ),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
        RegisteredBlindId.toString -> JsBoolean(false)
      ))

      answers.hasChildEligibleForTfc mustEqual false
    }

    "return false if multiple children over 11 and not disabled" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(3),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(foo, ageOfExactly16)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOf19)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOf16Over))
        ),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
        RegisteredBlindId.toString -> JsBoolean(false)
      ))

      answers.hasChildEligibleForTfc mustEqual false
    }

    "return true if there is a disabled child aged 16" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(3),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(foo, ageOfExactly16)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOf19)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOf16Over))
        ),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0)),
        WhichChildrenBlindId.toString -> Json.toJson(Seq(0))
      ))

      answers.hasChildEligibleForTfc mustEqual true
    }

    "return true when number of children is 1 and the child is disabled and 16" in {
      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(foo, ageOfUnder16))
        ),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
        RegisteredBlindId.toString -> JsBoolean(false))

      val result: Boolean = helper(answers).hasChildEligibleForTfc
      result mustEqual true
    }

    "return false when number of children is 1 and the child is 16 and not disabled" in {
      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(foo, ageOfExactly16))
        ),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
        RegisteredBlindId.toString -> JsBoolean(false))

      val result: Boolean = helper(answers).hasChildEligibleForTfc
      result mustEqual false
    }

    "return false when the children aged exactly 16 and birthday before 31st of August are disabled" in {
      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Before31Aug)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOfUnder16)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOfUnder16)),
          "3" -> Json.toJson(AboutYourChild("Baz", ageOf16Before31Aug))),

        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2, 3)))

      val result: Boolean = helper(answers).hasChildEligibleForTfc
      result mustEqual true
    }

    "return true when the children aged exactly 16 and birthday before 31st of August are blind" in {
      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Before31Aug)),
          "1" -> Json.toJson(AboutYourChild("Baz", ageOf16Before31Aug))),
        WhichChildrenBlindId.toString -> Json.toJson(Seq(0, 2, 3)))

      val result: Boolean = helper(answers).hasChildEligibleForTfc
      result mustEqual true
    }

    "return true when there are children under 11" in {
      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Before31Aug)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOfUnder16)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOfUnder16)),
          "3" -> Json.toJson(AboutYourChild("Baz", ageOf16Before31Aug))),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(1, 2)),
        WhichChildrenBlindId.toString -> Json.toJson(Seq(2)))

      val result: Boolean = helper(answers).hasChildEligibleForTfc
      result mustEqual true
    }

    "return false when there are 16 year olds that are not disabled" in {
      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Before31Aug)),
          "1" -> Json.toJson(AboutYourChild("Baz", ageOf16Before31Aug)))
      )

      val result: Boolean = helper(answers).hasChildEligibleForTfc
      result mustEqual false
    }
  }


  "childrenIdsForAgeExactly16AndDisabled" must {
    "returns list with children exactly 16 years with dob before august and blind" in {

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Before31Aug)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOfUnder16)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOfUnder16)),
          "3" -> Json.toJson(AboutYourChild("Baz", ageOf16Before31Aug))),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(1, 2)),
        WhichChildrenBlindId.toString -> Json.toJson(Seq(0, 2, 1, 3)))

      val result: List[Int] = helper(answers).childrenIdsForAgeExactly16AndDisabled
      result mustEqual Seq(0, 3)
    }

    "returns list with children exactly 16 years with dob before august and disable " in {

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Before31Aug)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOfUnder16)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOf16Before31Aug)),
          "3" -> Json.toJson(AboutYourChild("Baz", ageOfUnder16))),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2, 3)),
        WhichChildrenBlindId.toString -> Json.toJson(Seq(1, 3)))

      val result: List[Int] = helper(answers).childrenIdsForAgeExactly16AndDisabled
      result mustEqual Seq(0, 2)
    }

    "returns empty list with children exactly 16 years with dob before august and not disable " in {
      val ageOfUnder16 = testDate.minusYears(1)

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Before31Aug)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOfUnder16)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOf16Before31Aug)),
          "3" -> Json.toJson(AboutYourChild("Baz", ageOfUnder16))))

      val result: List[Int] = helper(answers).childrenIdsForAgeExactly16AndDisabled
      result mustEqual Seq()
    }

    "returns list with single child exactly 16 years with dob before august and disabled" in {

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Before31Aug))),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
        RegisteredBlindId.toString -> JsBoolean(false))

      val result: List[Int] = helper(answers).childrenIdsForAgeExactly16AndDisabled
      result mustEqual Seq(0)
    }

    "returns list with single child exactly 16 years with dob before august and blind" in {

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Before31Aug))),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
        RegisteredBlindId.toString -> JsBoolean(true))

      val result: List[Int] = helper(answers).childrenIdsForAgeExactly16AndDisabled
      result mustEqual Seq(0)
    }

    "returns empty list for single child exactly 16 years with dob before august and not blind or disabled" in {

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Before31Aug))),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
        RegisteredBlindId.toString -> JsBoolean(false))

      val result: List[Int] = helper(answers).childrenIdsForAgeExactly16AndDisabled
      result mustEqual Seq()
    }

  }

  "childrenBelow16AndExactly16Disabled" when {
    "return the list of children who are under 16 and exactly 16 with DOB before 31st of august and disable or blind" in {

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Before31Aug)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOfUnder16)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOf16Before31Aug)),
          "3" -> Json.toJson(AboutYourChild("Baz", ageOfUnder16))),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 3)))

      val result: List[Int] = helper(answers).childrenBelow16AndExactly16Disabled
      result mustEqual Seq(0, 1, 3)
    }

    "return empty list when children who are under 16 and exactly 16 with DOB before 31st of august and disable or blind" in {

      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf16Before31Aug)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOf16Over)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOf16Before31Aug)),
          "3" -> Json.toJson(AboutYourChild("Baz", ageOf16Over))),
        WhichChildrenBlindId.toString -> Json.toJson(Seq(1, 3)))

      val result: List[Int] = helper(answers).childrenBelow16AndExactly16Disabled
      result mustEqual Seq()
    }
  }

  "childrenBelow16" must {
    "returns list of children id's whose age is less than 16" in {
      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(
            foo, ageOf19)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOfUnder16)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOf16Before31Aug)),
          "3" -> Json.toJson(AboutYourChild("Baz", ageOfUnder16))),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 3)))

      val result: List[Int] = helper(answers).childrenBelow16
      result mustEqual Seq(1, 3)
    }

    "returns empty list   when chidren are over or exactly 16" in {
      val answers: CacheMap = cacheMap(
        NoOfChildrenId.toString -> JsNumber(4),
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild(foo, ageOf19)),
          "1" -> Json.toJson(AboutYourChild(bar, ageOf19)),
          "2" -> Json.toJson(AboutYourChild(quux, ageOf16Before31Aug)),
          "3" -> Json.toJson(AboutYourChild("Baz", ageOf16Before31Aug))),
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 3)))

      val result: List[Int] = helper(answers).childrenBelow16
      result mustEqual Seq()
    }
  }

  "childrenWithDisabilityBenefits" must {

    "return `Some` if `whichChildrenDisability` is defined" in {
      val answers = helper(cacheMap(
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2))
      ))
      answers.childrenWithDisabilityBenefits.value mustEqual Set(0, 2)
    }

    "return `Some` if there is a single child with disability benefits" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true)
      ))
      answers.childrenWithDisabilityBenefits.value mustEqual Set(0)
    }

    "return `Some(Set())` if there is a single child without disability benefits" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false)
      ))
      answers.childrenWithDisabilityBenefits.value must be(empty)
    }

    "return `Some(Set())` if there are multiple children without disability benefits" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(2),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false)
      ))
      answers.childrenWithDisabilityBenefits.value must be(empty)
    }

    "return `None` if `noOfChildren` and `whichChildrenDisability` are both undefined" in {
      val answers = helper(cacheMap(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true)
      ))
      answers.childrenWithDisabilityBenefits mustNot be(defined)
    }

    "return `None` if there is a single child and `childrenDisabilityBenefits` is undefined" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1)
      ))
      answers.childrenWithDisabilityBenefits mustNot be(defined)
    }
  }

  "childrenWithCosts" must {

    "return `Some` if there are multiple children and `whoHasChildcareCosts` is defined" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(2),
        WhoHasChildcareCostsId.toString -> Json.toJson(Seq(JsNumber(0)))
      ))
      answers.childrenWithCosts.value mustEqual Set(0)
    }

    "return `Some` if there is a single child and the `childcareCosts` is `yes`" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildcareCostsId.toString -> JsString("yes")
      ))
      answers.childrenWithCosts.value mustEqual Set(0)
    }

    "return `Some` if there is a single child and the `childcareCosts` is `not yet`" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildcareCostsId.toString -> JsString("notYet")
      ))
      answers.childrenWithCosts.value mustEqual Set(0)
    }

    "return `Some(Set())` if there is a single child and `childcareCosts` is `no`" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildcareCostsId.toString -> JsString("no")
      ))
      answers.childrenWithCosts.value mustEqual Set.empty
    }

    "return `None` if there is a single child and `childcareCosts` is undefined" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(1)
      ))
      answers.childrenWithCosts mustNot be(defined)
    }

    "return `None` if there are multiple children and `whoHasChildcareCosts` is undefined" in {
      val answers = helper(cacheMap(
        NoOfChildrenId.toString -> JsNumber(2)
      ))
      answers.childrenWithCosts mustNot be(defined)
    }
  }

  "hasApprovedCosts" must {

    import uk.gov.hmrc.childcarecalculatorfrontend.models.{YesNoNotYetEnum, YesNoUnsureEnum}

    val yesNoNotYetPositive: Seq[String] = Seq(YesNoNotYetEnum.YES.toString, YesNoNotYetEnum.NOTYET.toString)
    val yesNoUnsurePositive: Seq[String] = Seq(YesNoUnsureEnum.YES.toString, YesNoUnsureEnum.NOTSURE.toString)

    for (costs <- yesNoNotYetPositive; provider <- yesNoUnsurePositive) {
      s"return `true` if user has costs: $costs, and approved costs: $provider" in {
        val answers = helper(cacheMap(
          ChildcareCostsId.toString -> JsString(costs),
          ApprovedProviderId.toString -> JsString(provider)
        ))
        answers.hasApprovedCosts.value mustEqual true
      }
    }

    "return `false` if a user has no costs" in {
      val answers = helper(cacheMap(
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NO.toString)
      ))
      answers.hasApprovedCosts.value mustEqual false
    }

    yesNoNotYetPositive.foreach {
      costs =>
        s"return `false` if a user has costs: $costs, but they aren't approved" in {
          helper(cacheMap(
            ChildcareCostsId.toString -> JsString(costs),
            ApprovedProviderId.toString -> JsString(YesNoUnsureEnum.NO.toString)
          ))
        }
    }

    "return `None` if a user has costs but `approvedProvider` is undefined" in {
      val answers = helper(cacheMap(
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString)
      ))
      answers.hasApprovedCosts mustNot be(defined)
    }

    "return `None` if a user `childcareCosts` is undefined" in {
      val answers = helper(cacheMap(
        ApprovedProviderId.toString -> JsString(YesNoUnsureEnum.YES.toString)
      ))
      answers.hasApprovedCosts mustNot be(defined)
    }
  }

  "checkVouchersForBoth" must {
    "return false when whoWorks is 'neither'" in {
      val answers = helper(cacheMap(WhoGetsVouchersId.toString -> JsString("neither")))
      answers.checkVouchersForBoth mustEqual Some(false)
    }

    "return None when whoWorks is 'None'" in {
      val answers = helper(cacheMap())
      answers.checkVouchersForBoth mustEqual None
    }

    "return true when whoWorks is 'you'" in {
      val answers = helper(cacheMap(WhoGetsVouchersId.toString -> JsString("you")))
      answers.checkVouchersForBoth mustEqual Some(true)
    }

    "return true when whoWorks is 'partner'" in {
      val answers = helper(cacheMap(WhoGetsVouchersId.toString -> JsString("partner")))
      answers.checkVouchersForBoth mustEqual Some(true)
    }
  }

  "hasVouchers" must {
    "return true" when {
      "'you' receive vouchers" in {
        val answers = helper(cacheMap(YourChildcareVouchersId.toString -> JsBoolean(true)))
        answers.hasVouchers mustEqual true
      }

      "'partner' receives vouchers" in {
        val answers = helper(cacheMap(PartnerChildcareVouchersId.toString -> JsBoolean(true)))
        answers.hasVouchers mustEqual true
      }

      "both work but 'you' receive vouchers" in {
        val answers = vouchersHelper(cacheMap(), checkVouchersBoth = Some(true))
        answers.hasVouchers mustEqual true
      }

      "both work but the 'partner' receive vouchers" in {
        val answers = vouchersHelper(cacheMap(), checkVouchersBoth = Some(true))
        answers.hasVouchers mustEqual true
      }

      "both work but 'both' receive vouchers" in {
        val answers = vouchersHelper(cacheMap(), checkVouchersBoth = Some(true))
        answers.hasVouchers mustEqual true
      }
    }

    "return false" when {
      "'you' don't receive vouchers" in {
        val answers = helper(cacheMap(YourChildcareVouchersId.toString -> JsBoolean(false)))
        answers.hasVouchers mustEqual false
      }

      "'partner' doesn't receive vouchers" in {
        val answers = helper(cacheMap(PartnerChildcareVouchersId.toString -> JsBoolean(false)))
        answers.hasVouchers mustEqual false
      }

      "both work but neither receive vouchers" in {
        val answers = vouchersHelper(cacheMap(), checkVouchersBoth = Some(false))
        answers.hasVouchers mustEqual false
      }
    }
  }

  "max30HoursEnglandContent" must {
    "return Some(true) when the location is England and hasVouchers is true" in {
      val answers = helper(cacheMap(
        LocationId.toString -> JsString("england"),
        PartnerChildcareVouchersId.toString -> JsBoolean(true)
      ))

      answers.max30HoursEnglandContent mustEqual Some(true)
    }

    "return Some(false) when the location is England and hasVouchers is false" in {
      val answers = helper(cacheMap(
        LocationId.toString -> JsString("england"),
        PartnerChildcareVouchersId.toString -> JsBoolean(false)
      ))

      answers.max30HoursEnglandContent mustEqual Some(false)
    }

    "return None when the location is not England" in {
      val answers = helper(cacheMap(
        LocationId.toString -> JsString("scotland"),
        PartnerChildcareVouchersId.toString -> JsBoolean(true)
      ))

      answers.max30HoursEnglandContent mustEqual None
    }
  }

  "isOnSevereDisabilityPremium" must {

    "return true" when {

      "'you' get SevereDisablement benefits" in {
        val answers = helper(cacheMap(
          DoYouGetAnyBenefitsId.toString -> JsArray(Seq(JsString(SevereDisablementAllowance.toString)))
        ))

        answers.isOnSevereDisabilityPremium mustEqual true
      }

      "'partner' gets SevereDisablement benefits" in {
        val answers = helper(cacheMap(
          DoesYourPartnerGetAnyBenefitsId.toString -> JsArray(Seq(JsString(SevereDisablementAllowance.toString)))
        ))

        answers.isOnSevereDisabilityPremium mustEqual true
      }

      "'both' get SevereDisablement benefits" in {
        val answers = helper(cacheMap(
          DoYouGetAnyBenefitsId.toString -> JsArray(Seq(JsString(SevereDisablementAllowance.toString))),
          DoesYourPartnerGetAnyBenefitsId.toString -> JsArray(Seq(JsString(SevereDisablementAllowance.toString)))
        ))

        answers.isOnSevereDisabilityPremium mustEqual true
      }
    }

    "return false" when {

      "'you' don't get SevereDisablement benefits" in {
        val answers = helper(cacheMap(
          DoYouGetAnyBenefitsId.toString -> JsArray(Seq(JsString(CarersAllowance.toString)))
        ))

        answers.isOnSevereDisabilityPremium mustEqual false
      }

      "'partner' doesn't get SevereDisablement benefits" in {
        val answers = helper(cacheMap(
          DoesYourPartnerGetAnyBenefitsId.toString -> JsArray(Seq(JsString(IncapacityBenefit.toString)))
        ))

        answers.isOnSevereDisabilityPremium mustEqual false
      }

      "'both' don't get SevereDisablement benefits" in {
        val answers = helper(cacheMap(
          DoYouGetAnyBenefitsId.toString -> JsArray(Seq(JsString(CarersAllowance.toString))),
          DoesYourPartnerGetAnyBenefitsId.toString -> JsArray(Seq(JsString(IncapacityBenefit.toString)))
        ))

        answers.isOnSevereDisabilityPremium mustEqual false
      }
    }

  }
// TODO what is the use of these test case
  "isAlreadyReceivingUniversalCredits" must {
    "return true" when {
      "Someone has universal credit " in {
        val answers = helper(cacheMap(UniversalCreditId.toString -> JsBoolean(true)))
        answers.isAlreadyReceivingUniversalCredit mustEqual true
      }
    }
    "return false" when {
      "Someone does not have universal credit " in {
        val answers = helper(cacheMap(UniversalCreditId.toString -> JsBoolean(false)))
        answers.isAlreadyReceivingUniversalCredit mustEqual false
      }
    }
  }

}
