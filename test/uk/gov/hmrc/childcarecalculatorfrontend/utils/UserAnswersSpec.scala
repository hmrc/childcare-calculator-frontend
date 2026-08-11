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
import uk.gov.hmrc.childcarecalculatorfrontend.DataGenerator.*
import uk.gov.hmrc.childcarecalculatorfrontend.helpers.CacheKeyOps
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{
  Location,
  YesNoNotSure,
  YesNoNotYet,
  YouPartnerBothNeitherNotSure
}

import java.time.LocalDate

class UserAnswersSpec extends PlaySpec with OptionValues with CacheKeyOps {

  private val testDate: LocalDate           = LocalDate.of(2026, 7, 27)
  private val ageOf19: LocalDate            = ageOf19YearsAgo(testDate)
  private val ageOf16Before31Aug: LocalDate = ageOf16WithBirthdayBefore31stAugust(testDate)
  private val ageOf16Over: LocalDate        = ageOfOver16Relative(testDate)
  private val ageOfUnder16: LocalDate       = ageUnder16Relative(testDate)
  private val ageOfExactly16: LocalDate     = ageExactly16Relative(testDate)

  def userAnswers(map: CacheMap = CacheMap.empty): UserAnswers =
    new UserAnswers(map) {
      override def now: LocalDate = testDate
    }

  def vouchersHelper(map: CacheMap = CacheMap.empty, checkVouchersBoth: Option[Boolean] = None): UserAnswers =
    new UserAnswers(map) {
      override def now: LocalDate = testDate

      override def checkVouchersForBoth: Option[Boolean] = checkVouchersBoth
    }

  private val quux = "Quux"
  private val foo  = "Foo"
  private val bar  = "Bar"

  ".childrenOver16" must {

    "return no children over 16" in {
      val answers: CacheMap = CacheMap.of(
        AboutYourChildId.withValue(
          Map(0 -> AboutYourChild(foo, ageOfUnder16), 1 -> AboutYourChild("Baz", ageOfUnder16))
        )
      )

      val result = userAnswers(answers).childrenOver16
      result.get.size mustBe 0
    }

    "return any children who are over 16" in {

      val answers: CacheMap = CacheMap.of(
        AboutYourChildId.withValue(
          Map(
            0 -> AboutYourChild(foo, ageOf16Over),
            1 -> AboutYourChild(bar, ageOfUnder16),
            2 -> AboutYourChild(quux, ageOfUnder16),
            3 -> AboutYourChild("Baz", ageOf16Over),
            4 -> AboutYourChild("Josh", ageOf19)
          )
        )
      )
      val result = userAnswers(answers).childrenOver16
      print(result)
      result.value must contain(0 -> AboutYourChild(foo, ageOf16Over))
      result.value must contain(3 -> AboutYourChild("Baz", ageOf16Over))
      result.value must contain(4 -> AboutYourChild("Josh", ageOf19))
    }

    "return `None` when there are no children defined" in {
      val answers: CacheMap = CacheMap.of()
      userAnswers(answers).childrenOver16 mustNot be(defined)
    }
  }

  "extract16YearsOldWithBirthdayBefore31stAugust" must {
    "return the number of children of 16 years and dob before 31st August" in {
      val answers: CacheMap = CacheMap.of(
        AboutYourChildId.withValue(
          Map(
            0 -> AboutYourChild(foo, ageOfExactly16),
            1 -> AboutYourChild(bar, ageOfExactly16),
            2 -> AboutYourChild(quux, ageOf19)
          )
        )
      )

      val parametersMap = Map(
        0 -> AboutYourChild(foo, ageOfExactly16),
        1 -> AboutYourChild(bar, ageOfExactly16),
        2 -> AboutYourChild(quux, ageOf19)
      )

      val result = userAnswers(answers).extract16YearOldsWithBirthdayBefore31stAugust(Some(parametersMap))

      result.value must contain(0 -> AboutYourChild(foo, ageOfExactly16))
      result.value must contain(1 -> AboutYourChild(bar, ageOfExactly16))
    }
  }

  "is16ThisYearAndDateOfBirthIsAfter31stAugust" must {
    "not return any children who are over 16 but Birthday is before 31st of August" in {
      val answers: CacheMap = CacheMap.of(
        AboutYourChildId.withValue(Map(0 -> AboutYourChild(foo, ageOf16Before31Aug)))
      )
      val result = userAnswers(answers).childrenOver16
      result.get.size mustBe 0
    }
  }

  "childrenIdsForAgeBelow16" must {
    "return the seq of child ids who are less than 16 years old and exactly 16 whose dob is before 31st of august " in {

      val answers: CacheMap = CacheMap.of(
        AboutYourChildId.withValue(
          Map(
            0 -> AboutYourChild(foo, ageOf16Over),
            1 -> AboutYourChild(bar, ageOfUnder16),
            2 -> AboutYourChild(quux, ageOfUnder16),
            3 -> AboutYourChild("Baz", ageOf16Before31Aug)
          )
        )
      )

      val result: Seq[Int] = userAnswers(answers).childrenIdsForAgeExactly16
      result mustEqual Seq(3)
    }

    "return the empty sequence when children Map has None" in {
      val answers: CacheMap = CacheMap.of()
      val result: Seq[Int]  = userAnswers(answers).childrenIdsForAgeExactly16
      result mustEqual Seq()
    }
  }

  "hasChildEligibleForTfc" must {
    "return false if 1 child that is over 11 and not disabled" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(1),
          AboutYourChildId.withValue(
            Map(0 -> AboutYourChild(foo, ageOfExactly16))
          ),
          ChildrenDisabilityBenefitsId.withValue(false),
          RegisteredBlindId.withValue(false)
        )
      )

      answers.hasChildEligibleForTfc mustEqual false
    }

    "return false if multiple children over 11 and not disabled" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(3),
          AboutYourChildId.withValue(
            Map(
              0 -> AboutYourChild(foo, ageOfExactly16),
              1 -> AboutYourChild(bar, ageOf19),
              2 -> AboutYourChild(quux, ageOf16Over)
            )
          ),
          ChildrenDisabilityBenefitsId.withValue(false),
          RegisteredBlindId.withValue(false)
        )
      )

      answers.hasChildEligibleForTfc mustEqual false
    }

    "return true if there is a disabled child aged 16" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(3),
          AboutYourChildId.withValue(
            Map(
              0 -> AboutYourChild(foo, ageOfExactly16),
              1 -> AboutYourChild(bar, ageOf19),
              2 -> AboutYourChild(quux, ageOf16Over)
            )
          ),
          WhichChildrenDisabilityId.withValue(Set(0)),
          WhichChildrenBlindId.withValue(Set(0))
        )
      )

      answers.hasChildEligibleForTfc mustEqual true
    }

    "return true when number of children is 1 and the child is disabled and 16" in {
      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(1),
        AboutYourChildId.withValue(
          Map(0 -> AboutYourChild(foo, ageOfUnder16))
        ),
        ChildrenDisabilityBenefitsId.withValue(true),
        RegisteredBlindId.withValue(false)
      )

      val result: Boolean = userAnswers(answers).hasChildEligibleForTfc
      result mustEqual true
    }

    "return false when number of children is 1 and the child is 16 and not disabled" in {
      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(1),
        AboutYourChildId.withValue(
          Map(0 -> AboutYourChild(foo, ageOfExactly16))
        ),
        ChildrenDisabilityBenefitsId.withValue(false),
        RegisteredBlindId.withValue(false)
      )

      val result: Boolean = userAnswers(answers).hasChildEligibleForTfc
      result mustEqual false
    }

    "return false when the children aged exactly 16 and birthday before 31st of August are disabled" in {
      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(4),
        AboutYourChildId.withValue(
          Map(
            0 -> AboutYourChild(foo, ageOf16Before31Aug),
            1 -> AboutYourChild(bar, ageOfUnder16),
            2 -> AboutYourChild(quux, ageOfUnder16),
            3 -> AboutYourChild("Baz", ageOf16Before31Aug)
          )
        ),
        WhichChildrenDisabilityId.withValue(Set(0, 2, 3))
      )

      val result: Boolean = userAnswers(answers).hasChildEligibleForTfc
      result mustEqual true
    }

    "return true when the children aged exactly 16 and birthday before 31st of August are blind" in {
      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(4),
        AboutYourChildId.withValue(
          Map(0 -> AboutYourChild(foo, ageOf16Before31Aug), 1 -> AboutYourChild("Baz", ageOf16Before31Aug))
        ),
        WhichChildrenBlindId.withValue(Set(0, 2, 3))
      )

      val result: Boolean = userAnswers(answers).hasChildEligibleForTfc
      result mustEqual true
    }

    "return true when there are children under 11" in {
      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(4),
        AboutYourChildId.withValue(
          Map(
            0 -> AboutYourChild(foo, ageOf16Before31Aug),
            1 -> AboutYourChild(bar, ageOfUnder16),
            2 -> AboutYourChild(quux, ageOfUnder16),
            3 -> AboutYourChild("Baz", ageOf16Before31Aug)
          )
        ),
        WhichChildrenDisabilityId.withValue(Set(1, 2)),
        WhichChildrenBlindId.withValue(Set(2))
      )

      val result: Boolean = userAnswers(answers).hasChildEligibleForTfc
      result mustEqual true
    }

    "return false when there are 16 year olds that are not disabled" in {
      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(4),
        AboutYourChildId.withValue(
          Map(0 -> AboutYourChild(foo, ageOf16Before31Aug), 1 -> AboutYourChild("Baz", ageOf16Before31Aug))
        )
      )

      val result: Boolean = userAnswers(answers).hasChildEligibleForTfc
      result mustEqual false
    }
  }

  "childrenIdsForAgeExactly16AndDisabled" must {
    "returns list with children exactly 16 years with dob before august and blind" in {

      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(4),
        AboutYourChildId.withValue(
          Map(
            0 -> AboutYourChild(foo, ageOf16Before31Aug),
            1 -> AboutYourChild(bar, ageOfUnder16),
            2 -> AboutYourChild(quux, ageOfUnder16),
            3 -> AboutYourChild("Baz", ageOf16Before31Aug)
          )
        ),
        WhichChildrenDisabilityId.withValue(Set(1, 2)),
        WhichChildrenBlindId.withValue(Set(0, 2, 1, 3))
      )

      val result: List[Int] = userAnswers(answers).childrenIdsForAgeExactly16AndDisabled
      result mustEqual Seq(0, 3)
    }

    "returns list with children exactly 16 years with dob before august and disable " in {

      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(4),
        AboutYourChildId.withValue(
          Map(
            0 -> AboutYourChild(foo, ageOf16Before31Aug),
            1 -> AboutYourChild(bar, ageOfUnder16),
            2 -> AboutYourChild(quux, ageOf16Before31Aug),
            3 -> AboutYourChild("Baz", ageOfUnder16)
          )
        ),
        WhichChildrenDisabilityId.withValue(Set(0, 2, 3)),
        WhichChildrenBlindId.withValue(Set(1, 3))
      )

      val result: List[Int] = userAnswers(answers).childrenIdsForAgeExactly16AndDisabled
      result mustEqual Seq(0, 2)
    }

    "returns empty list with children exactly 16 years with dob before august and not disable " in {
      val ageOfUnder16 = testDate.minusYears(1)

      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(4),
        AboutYourChildId.withValue(
          Map(
            0 -> AboutYourChild(foo, ageOf16Before31Aug),
            1 -> AboutYourChild(bar, ageOfUnder16),
            2 -> AboutYourChild(quux, ageOf16Before31Aug),
            3 -> AboutYourChild("Baz", ageOfUnder16)
          )
        )
      )

      val result: List[Int] = userAnswers(answers).childrenIdsForAgeExactly16AndDisabled
      result mustEqual Seq()
    }

    "returns list with single child exactly 16 years with dob before august and disabled" in {

      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(1),
        AboutYourChildId.withValue(Map(0 -> AboutYourChild(foo, ageOf16Before31Aug))),
        ChildrenDisabilityBenefitsId.withValue(true),
        RegisteredBlindId.withValue(false)
      )

      val result: List[Int] = userAnswers(answers).childrenIdsForAgeExactly16AndDisabled
      result mustEqual Seq(0)
    }

    "returns list with single child exactly 16 years with dob before august and blind" in {

      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(1),
        AboutYourChildId.withValue(Map(0 -> AboutYourChild(foo, ageOf16Before31Aug))),
        ChildrenDisabilityBenefitsId.withValue(false),
        RegisteredBlindId.withValue(true)
      )

      val result: List[Int] = userAnswers(answers).childrenIdsForAgeExactly16AndDisabled
      result mustEqual Seq(0)
    }

    "returns empty list for single child exactly 16 years with dob before august and not blind or disabled" in {

      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(1),
        AboutYourChildId.withValue(Map(0 -> AboutYourChild(foo, ageOf16Before31Aug))),
        ChildrenDisabilityBenefitsId.withValue(false),
        RegisteredBlindId.withValue(false)
      )

      val result: List[Int] = userAnswers(answers).childrenIdsForAgeExactly16AndDisabled
      result mustEqual Seq()
    }

  }

  "childrenBelow16AndExactly16Disabled" when {
    "return the list of children who are under 16 and exactly 16 with DOB before 31st of august and disable or blind" in {

      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(4),
        AboutYourChildId.withValue(
          Map(
            0 -> AboutYourChild(foo, ageOf16Before31Aug),
            1 -> AboutYourChild(bar, ageOfUnder16),
            2 -> AboutYourChild(quux, ageOf16Before31Aug),
            3 -> AboutYourChild("Baz", ageOfUnder16)
          )
        ),
        WhichChildrenDisabilityId.withValue(Set(0, 3))
      )

      val result: List[Int] = userAnswers(answers).childrenBelow16AndExactly16Disabled
      result mustEqual Seq(0, 1, 3)
    }

    "return empty list when children who are under 16 and exactly 16 with DOB before 31st of august and disable or blind" in {

      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(4),
        AboutYourChildId.withValue(
          Map(
            0 -> AboutYourChild(foo, ageOf16Before31Aug),
            1 -> AboutYourChild(bar, ageOf16Over),
            2 -> AboutYourChild(quux, ageOf16Before31Aug),
            3 -> AboutYourChild("Baz", ageOf16Over)
          )
        ),
        WhichChildrenBlindId.withValue(Set(1, 3))
      )

      val result: List[Int] = userAnswers(answers).childrenBelow16AndExactly16Disabled
      result mustEqual Seq()
    }
  }

  "childrenBelow16" must {
    "returns list of children id's whose age is less than 16" in {
      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(4),
        AboutYourChildId.withValue(
          Map(
            0 -> AboutYourChild(foo, ageOf19),
            1 -> AboutYourChild(bar, ageOfUnder16),
            2 -> AboutYourChild(quux, ageOf16Before31Aug),
            3 -> AboutYourChild("Baz", ageOfUnder16)
          )
        ),
        WhichChildrenDisabilityId.withValue(Set(0, 3))
      )

      val result: List[Int] = userAnswers(answers).childrenBelow16
      result mustEqual Seq(1, 3)
    }

    "returns empty list   when children are over or exactly 16" in {
      val answers: CacheMap = CacheMap.of(
        NoOfChildrenId.withValue(4),
        AboutYourChildId.withValue(
          Map(
            0 -> AboutYourChild(foo, ageOf19),
            1 -> AboutYourChild(bar, ageOf19),
            2 -> AboutYourChild(quux, ageOf16Before31Aug),
            3 -> AboutYourChild("Baz", ageOf16Before31Aug)
          )
        ),
        WhichChildrenDisabilityId.withValue(Set(0, 3))
      )

      val result: List[Int] = userAnswers(answers).childrenBelow16
      result mustEqual Seq()
    }
  }

  "childrenWithDisabilityBenefits" must {

    "return `Some` if `whichChildrenDisability` is defined" in {
      val answers = userAnswers(
        CacheMap.of(
          WhichChildrenDisabilityId.withValue(Set(0, 2))
        )
      )
      answers.childrenWithDisabilityBenefits.value mustEqual Set(0, 2)
    }

    "return `Some` if there is a single child with disability benefits" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(1),
          ChildrenDisabilityBenefitsId.withValue(true)
        )
      )
      answers.childrenWithDisabilityBenefits.value mustEqual Set(0)
    }

    "return `Some(Set())` if there is a single child without disability benefits" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(1),
          ChildrenDisabilityBenefitsId.withValue(false)
        )
      )
      answers.childrenWithDisabilityBenefits.value must be(empty)
    }

    "return `Some(Set())` if there are multiple children without disability benefits" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(2),
          ChildrenDisabilityBenefitsId.withValue(false)
        )
      )
      answers.childrenWithDisabilityBenefits.value must be(empty)
    }

    "return `None` if `noOfChildren` and `whichChildrenDisability` are both undefined" in {
      val answers = userAnswers(
        CacheMap.of(
          ChildrenDisabilityBenefitsId.withValue(true)
        )
      )
      answers.childrenWithDisabilityBenefits mustNot be(defined)
    }

    "return `None` if there is a single child and `childrenDisabilityBenefits` is undefined" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(1)
        )
      )
      answers.childrenWithDisabilityBenefits mustNot be(defined)
    }
  }

  "childrenWithCosts" must {

    "return `Some` if there are multiple children and `whoHasChildcareCosts` is defined" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(2),
          WhoHasChildcareCostsId.withValue(Set(0))
        )
      )
      answers.childrenWithCosts.value mustEqual Set(0)
    }

    "return `Some` if there is a single child and the `childcareCosts` is `yes`" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(1),
          ChildcareCostsId.withValue(YesNoNotYet.Yes)
        )
      )
      answers.childrenWithCosts.value mustEqual Set(0)
    }

    "return `Some` if there is a single child and the `childcareCosts` is `not yet`" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(1),
          ChildcareCostsId.withValue(YesNoNotYet.NotYet)
        )
      )
      answers.childrenWithCosts.value mustEqual Set(0)
    }

    "return `Some(Set())` if there is a single child and `childcareCosts` is `no`" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(1),
          ChildcareCostsId.withValue(YesNoNotYet.No)
        )
      )
      answers.childrenWithCosts.value mustEqual Set.empty
    }

    "return `None` if there is a single child and `childcareCosts` is undefined" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(1)
        )
      )
      answers.childrenWithCosts mustNot be(defined)
    }

    "return `None` if there are multiple children and `whoHasChildcareCosts` is undefined" in {
      val answers = userAnswers(
        CacheMap.of(
          NoOfChildrenId.withValue(2)
        )
      )
      answers.childrenWithCosts mustNot be(defined)
    }
  }

  "hasApprovedCosts" must {

    val yesNoNotYetPositive: Seq[YesNoNotYet]   = Seq(YesNoNotYet.Yes, YesNoNotYet.NotYet)
    val yesNoNotSurePositive: Seq[YesNoNotSure] = Seq(YesNoNotSure.Yes, YesNoNotSure.NotSure)

    for {
      costs    <- yesNoNotYetPositive
      provider <- yesNoNotSurePositive
    }
      s"return `true` if user has costs: $costs, and approved costs: $provider" in {
        val answers = userAnswers(
          CacheMap.of(
            ChildcareCostsId.withValue(costs),
            ApprovedProviderId.withValue(provider)
          )
        )
        answers.hasApprovedCosts.value mustEqual true
      }

    "return `false` if a user has no costs" in {
      val answers = userAnswers(
        CacheMap.of(
          ChildcareCostsId.withValue(YesNoNotYet.No)
        )
      )
      answers.hasApprovedCosts.value mustEqual false
    }

    yesNoNotYetPositive.foreach { costs =>
      s"return `false` if a user has costs: $costs, but they aren't approved" in
        userAnswers(
          CacheMap.of(
            ChildcareCostsId.withValue(costs),
            ApprovedProviderId.withValue(YesNoNotSure.No)
          )
        )
    }

    "return `None` if a user has costs but `approvedProvider` is undefined" in {
      val answers = userAnswers(
        CacheMap.of(
          ChildcareCostsId.withValue(YesNoNotYet.Yes)
        )
      )
      answers.hasApprovedCosts mustNot be(defined)
    }

    "return `None` if a user `childcareCosts` is undefined" in {
      val answers = userAnswers(
        CacheMap.of(
          ApprovedProviderId.withValue(YesNoNotSure.Yes)
        )
      )
      answers.hasApprovedCosts mustNot be(defined)
    }
  }

  "checkVouchersForBoth" must {
    "return false when whoWorks is 'neither'" in {
      val answers = userAnswers(CacheMap.of(WhoGetsVouchersId.withValue(YouPartnerBothNeitherNotSure.Neither)))
      answers.checkVouchersForBoth mustBe Some(false)
    }

    "return None when whoWorks is 'None'" in {
      val answers = userAnswers(CacheMap.of())
      answers.checkVouchersForBoth mustBe None
    }

    "return true when whoWorks is 'you'" in {
      val answers = userAnswers(CacheMap.of(WhoGetsVouchersId.withValue(YouPartnerBothNeitherNotSure.You)))
      answers.checkVouchersForBoth mustBe Some(true)
    }

    "return true when whoWorks is 'partner'" in {
      val answers = userAnswers(CacheMap.of(WhoGetsVouchersId.withValue(YouPartnerBothNeitherNotSure.Partner)))
      answers.checkVouchersForBoth mustBe Some(true)
    }
  }

  "hasVouchers" must {
    "return true" when {
      "'you' receive vouchers" in {
        val answers = userAnswers(CacheMap.of(YourChildcareVouchersId.withValue(true)))
        answers.hasVouchers mustEqual true
      }

      "'partner' receives vouchers" in {
        val answers = userAnswers(CacheMap.of(PartnerChildcareVouchersId.withValue(true)))
        answers.hasVouchers mustEqual true
      }

      "both work but 'you' receive vouchers" in {
        val answers = vouchersHelper(CacheMap.of(), checkVouchersBoth = Some(true))
        answers.hasVouchers mustEqual true
      }

      "both work but the 'partner' receive vouchers" in {
        val answers = vouchersHelper(CacheMap.of(), checkVouchersBoth = Some(true))
        answers.hasVouchers mustEqual true
      }

      "both work but 'both' receive vouchers" in {
        val answers = vouchersHelper(CacheMap.of(), checkVouchersBoth = Some(true))
        answers.hasVouchers mustEqual true
      }
    }

    "return false" when {
      "'you' don't receive vouchers" in {
        val answers = userAnswers(CacheMap.of(YourChildcareVouchersId.withValue(false)))
        answers.hasVouchers mustEqual false
      }

      "'partner' doesn't receive vouchers" in {
        val answers = userAnswers(CacheMap.of(PartnerChildcareVouchersId.withValue(false)))
        answers.hasVouchers mustEqual false
      }

      "both work but neither receive vouchers" in {
        val answers = vouchersHelper(CacheMap.of(), checkVouchersBoth = Some(false))
        answers.hasVouchers mustEqual false
      }
    }
  }

  "max30HoursEnglandContent" must {
    "return Some(true) when the location is England and hasVouchers is true" in {
      val answers = userAnswers(
        CacheMap.of(
          LocationId.withValue(Location.England),
          PartnerChildcareVouchersId.withValue(true)
        )
      )

      answers.max30HoursEnglandContent mustBe Some(true)
    }

    "return Some(false) when the location is England and hasVouchers is false" in {
      val answers = userAnswers(
        CacheMap.of(
          LocationId.withValue(Location.England),
          PartnerChildcareVouchersId.withValue(false)
        )
      )

      answers.max30HoursEnglandContent mustBe Some(false)
    }

    "return None when the location is not England" in {
      val answers = userAnswers(
        CacheMap.of(
          LocationId.withValue(Location.Scotland),
          PartnerChildcareVouchersId.withValue(true)
        )
      )

      answers.max30HoursEnglandContent mustBe None
    }
  }

}
