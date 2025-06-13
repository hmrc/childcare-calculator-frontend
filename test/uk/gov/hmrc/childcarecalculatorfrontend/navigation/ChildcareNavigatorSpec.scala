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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import org.mockito.Mockito._
import org.scalatest.OptionValues
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsBoolean, JsNumber, JsValue, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.DataGenerator.{
  ageExactly15Relative,
  ageOf16WithBirthdayBefore31stAugust,
  ageOf19YearsAgo,
  ageOfOver16Relative
}
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, UserAnswers, Utils}

import java.time.LocalDate

class ChildcareNavigatorSpec extends SpecBase with OptionValues with MockitoSugar {

  private val testDate: LocalDate = LocalDate.parse("2014-01-01")

  val navigator: ChildcareNavigator = new ChildcareNavigator(new Utils()) {
    override def now: LocalDate = testDate
  }

  private val ageOf19: LocalDate            = ageOf19YearsAgo(testDate)
  private val ageOfOver16: LocalDate        = ageOfOver16Relative(testDate)
  private val ageOf16Before31Aug: LocalDate = ageOf16WithBirthdayBefore31stAugust(testDate)
  private val ageOfExactly15: LocalDate     = ageExactly15Relative(testDate)
  private lazy val dob: LocalDate           = testDate.minusYears(1)

  private def userAnswers(data: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", data.toMap))

  private def userAnswersOverride(data: (String, JsValue)*)(newDate: LocalDate): UserAnswers =
    new UserAnswers(CacheMap("", data.toMap)) {
      override def now = newDate
    }

  private def aboutYourChildren(children: (String, LocalDate)*): (String, JsValue) =
    AboutYourChildId.toString -> Json.toJson(children.zipWithIndex.map { case ((name, dateOfBirth), i) =>
      i.toString -> Json.toJson(AboutYourChild(name, dateOfBirth))
    }.toMap)

  "Number of children" must {
    "redirect to `About your child`" in {
      val result = navigator.nextPage(NoOfChildrenId).value(mock[UserAnswers])
      result mustEqual routes.AboutYourChildController.onPageLoad(0)
    }
  }

  private val foo = "Foo"
  private val bar = "Bar"

  "About your child" must {

    "this isn't the last child" when {
      "redirect to `About your child` for the next index" in {
        val answers: UserAnswers = userAnswersOverride(
          NoOfChildrenId.toString -> JsNumber(2),
          aboutYourChildren(
            foo -> dob,
            bar -> dob
          )
        )(testDate)
        val result = navigator.nextPage(AboutYourChildId(0)).value(answers)
        result mustEqual routes.AboutYourChildController.onPageLoad(1)
      }
    }

    "this is the last child" when {

      "redirect to `Do any children get disability benefits` when child is over 16" in {
        val answers: UserAnswers = userAnswersOverride(
          NoOfChildrenId.toString -> JsNumber(2),
          aboutYourChildren(
            foo -> ageOfOver16,
            bar -> dob
          )
        )(testDate)
        val result = navigator.nextPage(AboutYourChildId(1)).value(answers)
        result mustEqual routes.ChildrenDisabilityBenefitsController.onPageLoad()
      }

      "redirect to `Do any children get disability benefits` for all the child below 18" in {
        val answers: UserAnswers = userAnswersOverride(
          NoOfChildrenId.toString -> JsNumber(2),
          aboutYourChildren(
            foo -> ageOfExactly15,
            bar -> ageOfExactly15
          )
        )(testDate)
        val result = navigator.nextPage(AboutYourChildId(1)).value(answers)
        result mustEqual routes.ChildrenDisabilityBenefitsController.onPageLoad()
      }
    }

    "redirect to `SessionExpired` when no answer for `noOfChildren` is given" in {
      val answers: UserAnswers = userAnswers(
        aboutYourChildren(
          foo   -> dob,
          "bar" -> dob
        )
      )
      val result = navigator.nextPage(AboutYourChildId(0)).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad
    }
  }

  "Do any of your children get disability benefits" must {

    "redirect to `Any children blind` when the user answers `No` when the user has 1 child" in {
      val answers: UserAnswers = userAnswers(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
        NoOfChildrenId.toString               -> JsNumber(1)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId).value(answers)
      result mustEqual routes.RegisteredBlindController.onPageLoad()
    }

    "redirect to `Any children blind` when the user answers `No` when the user has more than 1 child" in {
      val answers: UserAnswers = userAnswers(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
        NoOfChildrenId.toString               -> JsNumber(2)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId).value(answers)
      result mustEqual routes.RegisteredBlindController.onPageLoad()
    }

    "redirect to `Which disability benefits` when the user answers `Yes` and has 1 child" in {
      val answers: UserAnswers = userAnswers(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
        NoOfChildrenId.toString               -> JsNumber(1)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId).value(answers)
      result mustEqual routes.WhichDisabilityBenefitsController.onPageLoad(0)
    }

    "redirect to `Which of your children get disability benefits` when the user answers `Yes` and has more than 1 child" in {
      val answers: UserAnswers = userAnswers(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
        NoOfChildrenId.toString               -> JsNumber(2)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId).value(answers)
      result mustEqual routes.WhichChildrenDisabilityController.onPageLoad()
    }

    "redirect to `SessionExpired` when the user has no answer for `Do any of your children get disability benefits`" in {
      val answers: UserAnswers = userAnswers(
        NoOfChildrenId.toString -> JsNumber(1)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad
    }

    "redirect to `SessionExpired` when the user has no answer for `Number of children`" in {
      val answers: UserAnswers = userAnswers(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad
    }
  }

  "Which of your children get disability benefits" must {

    Seq(0, 2).foreach { id =>
      s"redirect to `WhichDisabilityBenefits` for the first appropriate child, for id: $id" in {
        val answers: UserAnswers = userAnswers(
          WhichChildrenDisabilityId.toString -> Json.toJson(Seq(id))
        )
        val result = navigator.nextPage(WhichChildrenDisabilityId).value(answers)
        result mustEqual routes.WhichDisabilityBenefitsController.onPageLoad(id)
      }
    }

    "redirect to `SessionExpired` when there is no answer for `WhichChildrenDisability`" in {
      val result = navigator.nextPage(WhichChildrenDisabilityId).value(userAnswers())
      result mustEqual routes.SessionExpiredController.onPageLoad
    }
  }

  "Which disability benefits" must {

    "redirect to `Which disability benefits` for the next applicable child, if this is not the last child" in {
      val answers: UserAnswers = userAnswers(
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2))
      )
      val result = navigator.nextPage(WhichDisabilityBenefitsId(0)).value(answers)
      result mustEqual routes.WhichDisabilityBenefitsController.onPageLoad(2)
    }

    "redirect to `Any children blind` when this is the last applicable child" in {
      val answers: UserAnswers = userAnswers(
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2))
      )
      val result = navigator.nextPage(WhichDisabilityBenefitsId(2)).value(answers)
      result mustEqual routes.RegisteredBlindController.onPageLoad()
    }

    "redirect to `Any children blind` when this is the only child" in {
      val answers: UserAnswers = userAnswers(
        NoOfChildrenId.toString               -> JsNumber(1),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true)
      )
      val result = navigator.nextPage(WhichDisabilityBenefitsId(0)).value(answers)
      result mustEqual routes.RegisteredBlindController.onPageLoad()
    }

    "redirect to `SessionExpired` when there is no answer for `WhichChildrenDisability`" in {
      val result = navigator.nextPage(WhichDisabilityBenefitsId(0)).value(userAnswers())
      result mustEqual routes.SessionExpiredController.onPageLoad
    }
  }

  "Are any of your children registered blind" must {
    "user has a single child" when {

      "redirect to `How often do you expect to pay for childcare` when the user answers `Yes` and child age below 16" in {
        val answers: UserAnswers = spy(userAnswers())

        when(answers.noOfChildren).thenReturn(Some(1))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.childDisabilityBenefits).thenReturn(Some(false))
        when(answers.registeredBlind).thenReturn(Some(true))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.now).thenReturn(testDate)

        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", ageOf16Before31Aug))))

        val result = navigator.nextPage(RegisteredBlindId).value(answers)
        result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(0)
      }

      "redirect to `How often do you expect to pay for childcare` when the user answers `No` and child age below 16" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.parse("2003-01-01")))))
        when(answers.noOfChildren).thenReturn(Some(1))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.childrenDisabilityBenefits).thenReturn(Some(true))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.now).thenReturn(testDate)

        val result = navigator.nextPage(RegisteredBlindId).value(answers)
        result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(0)
      }

      "redirect to `How often do you expect to pay for childcare` when the user answers `No, disable child age 16 and dob before august" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.parse("2003-01-01")))))
        when(answers.noOfChildren).thenReturn(Some(1))
        when(answers.childrenDisabilityBenefits).thenReturn(Some(true))

        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.now).thenReturn(testDate)

        val result = navigator.nextPage(RegisteredBlindId).value(answers)
        result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(0)
      }

      "redirect to `How often do you expect to pay for childcare` when the user answers `No, blind child age 16 and dob before august" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.parse("2003-01-01")))))
        when(answers.noOfChildren).thenReturn(Some(1))
        when(answers.childDisabilityBenefits).thenReturn(Some(false))

        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(true))
        when(answers.now).thenReturn(testDate)

        val result = navigator.nextPage(RegisteredBlindId).value(answers)
        result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(0)
      }

      "redirect to  Your Income This Year" when {
        "the user answers `No and child is 16 years and dob before august and not disable" in {
          val answers: UserAnswers = spy(userAnswers())
          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.parse("2002-01-01")))))
          when(answers.noOfChildren).thenReturn(Some(1))
          when(answers.childDisabilityBenefits).thenReturn(Some(false))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))

          val result = navigator.nextPage(RegisteredBlindId).value(answers)
          result mustEqual routes.YourIncomeInfoController.onPageLoad()
        }
      }

      "redirect to  Partner Income This Year" when {
        "the user answers `No and child is 16 years and dob before august and not disable" in {
          val answers: UserAnswers = spy(userAnswers())
          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.parse("2002-01-01")))))
          when(answers.noOfChildren).thenReturn(Some(1))
          when(answers.childDisabilityBenefits).thenReturn(Some(false))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))

          val result = navigator.nextPage(RegisteredBlindId).value(answers)
          result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
        }
      }

      "user with multiple children" when {
        "redirect to Childcare Pay Frequency if the user answers 'No' to " +
          "registered blind and 1 child is 16 years and dob is before august and disabled" in {

            val answers: UserAnswers = spy(userAnswers())

            when(answers.aboutYourChild).thenReturn(
              Some(
                Map(
                  0 -> AboutYourChild(foo, ageOf19),
                  1 -> AboutYourChild(bar, ageOf16Before31Aug),
                  2 -> AboutYourChild("Quux", ageOf19)
                )
              )
            )
            when(answers.noOfChildren).thenReturn(Some(3))
            when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
            when(answers.whichChildrenDisability).thenReturn(Some(Set(0, 1)))
            when(answers.registeredBlind).thenReturn(Some(false))
            when(answers.now).thenReturn(testDate)

            val result = navigator.nextPage(RegisteredBlindId).value(answers)
            result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(1)
          }

        "redirect to Childcare Pay Frequency if the user answers 'No' to " +
          "registered blind and more than 1 child is 16 years and dob is before august but only 1 is disabled " in {

            val answers: UserAnswers = spy(userAnswers())

            when(answers.aboutYourChild).thenReturn(
              Some(
                Map(
                  0 -> AboutYourChild(foo, ageOf16Before31Aug),
                  1 -> AboutYourChild(bar, ageOf19),
                  2 -> AboutYourChild(bar, ageOf16Before31Aug),
                  3 -> AboutYourChild("Quux", ageOf19)
                )
              )
            )
            when(answers.noOfChildren).thenReturn(Some(4))
            when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
            when(answers.whichChildrenDisability).thenReturn(Some(Set(0, 1)))
            when(answers.registeredBlind).thenReturn(Some(false))
            when(answers.now).thenReturn(testDate)

            val result = navigator.nextPage(RegisteredBlindId).value(answers)
            result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(0)
          }

        "redirect to Partner Income Info when user answers 'No' to registered blind and " +
          "1 child is 16 years and dob is before august and same child is not disabled" in {

            val answers: UserAnswers = spy(userAnswers())

            when(answers.aboutYourChild).thenReturn(
              Some(
                Map(
                  0 -> AboutYourChild(foo, ageOf19),
                  1 -> AboutYourChild(bar, ageOf16Before31Aug),
                  2 -> AboutYourChild("Quux", ageOf19)
                )
              )
            )
            when(answers.noOfChildren).thenReturn(Some(3))
            when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
            when(answers.whichChildrenDisability).thenReturn(Some(Set(0, 2)))
            when(answers.registeredBlind).thenReturn(Some(false))
            when(answers.doYouLiveWithPartner).thenReturn(Some(true))
            when(answers.now).thenReturn(testDate)

            val result = navigator.nextPage(RegisteredBlindId).value(answers)
            result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
          }

        "redirect to Your Income Info when single user answers 'No' to registerd blind and " +
          "1 child is 16 years and dob is before august and not disable " in {

            val answers: UserAnswers = spy(userAnswers())

            when(answers.aboutYourChild).thenReturn(
              Some(
                Map(
                  0 -> AboutYourChild(foo, ageOf19),
                  1 -> AboutYourChild(bar, ageOf16Before31Aug),
                  2 -> AboutYourChild("Quux", ageOf19)
                )
              )
            )
            when(answers.noOfChildren).thenReturn(Some(3))
            when(answers.childrenDisabilityBenefits).thenReturn(Some(false))
            when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
            when(answers.registeredBlind).thenReturn(Some(false))
            when(answers.doYouLiveWithPartner).thenReturn(Some(false))
            when(answers.now).thenReturn(testDate)

            val result = navigator.nextPage(RegisteredBlindId).value(answers)
            result mustEqual routes.YourIncomeInfoController.onPageLoad()
          }

        "redirect to Your Income Info when user with partner answers 'No' to registerd blind and " +
          "1 child is 16 years and dob is before august and not disabled " in {

            val answers: UserAnswers = spy(userAnswers())

            when(answers.aboutYourChild).thenReturn(
              Some(
                Map(
                  0 -> AboutYourChild(foo, ageOf19),
                  1 -> AboutYourChild(bar, ageOf16Before31Aug),
                  2 -> AboutYourChild("Quux", ageOf19)
                )
              )
            )
            when(answers.noOfChildren).thenReturn(Some(3))
            when(answers.childrenDisabilityBenefits).thenReturn(Some(false))
            when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
            when(answers.registeredBlind).thenReturn(Some(false))
            when(answers.doYouLiveWithPartner).thenReturn(Some(true))
            when(answers.now).thenReturn(testDate)

            val result = navigator.nextPage(RegisteredBlindId).value(answers)
            result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
          }

        "redirect to  Partner Income Info when user answers 'No' to registerd blind and more than " +
          "1 child is 16 years and dob is before august and not disable" in {

            val answers: UserAnswers = spy(userAnswers())

            when(answers.aboutYourChild).thenReturn(
              Some(
                Map(
                  0 -> AboutYourChild(foo, ageOf19),
                  1 -> AboutYourChild(bar, ageOf16Before31Aug),
                  2 -> AboutYourChild("Quux", ageOf19),
                  3 -> AboutYourChild("Max", ageOf16Before31Aug)
                )
              )
            )
            when(answers.noOfChildren).thenReturn(Some(4))
            when(answers.childrenDisabilityBenefits).thenReturn(Some(false))
            when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
            when(answers.registeredBlind).thenReturn(Some(false))
            when(answers.doYouLiveWithPartner).thenReturn(Some(true))
            when(answers.now).thenReturn(testDate)

            val result = navigator.nextPage(RegisteredBlindId).value(answers)
            result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
          }

        "redirects to Who Has Childcare Costs when the user answers 'No' and more than " +
          "1 child is 16 years and dob is before august and disable" in {

            val answers: UserAnswers = spy(userAnswers())

            when(answers.aboutYourChild).thenReturn(
              Some(
                Map(
                  0 -> AboutYourChild(foo, ageOf19),
                  1 -> AboutYourChild(bar, ageOf16Before31Aug),
                  2 -> AboutYourChild("Quux", ageOf19),
                  3 -> AboutYourChild("Max", ageOf16Before31Aug)
                )
              )
            )
            when(answers.noOfChildren).thenReturn(Some(4))
            when(answers.childrenDisabilityBenefits).thenReturn(Some(true))
            when(answers.whichChildrenDisability).thenReturn(Some(Set(0, 1, 3)))
            when(answers.registeredBlind).thenReturn(Some(false))
            when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
            when(answers.now).thenReturn(testDate)

            val result = navigator.nextPage(RegisteredBlindId).value(answers)
            result mustEqual routes.WhoHasChildcareCostsController.onPageLoad()
          }

        "redirects to Your Income Info when user answers 'No' and more than " +
          "1 child is 16 years and dob is before august and same children are not disable" in {

            val answers: UserAnswers = spy(userAnswers())

            when(answers.aboutYourChild).thenReturn(
              Some(
                Map(
                  0 -> AboutYourChild(foo, ageOf19),
                  1 -> AboutYourChild(bar, ageOf16Before31Aug),
                  2 -> AboutYourChild("Quux", ageOf19),
                  3 -> AboutYourChild("Max", ageOf16Before31Aug)
                )
              )
            )
            when(answers.noOfChildren).thenReturn(Some(4))
            when(answers.childrenDisabilityBenefits).thenReturn(Some(true))
            when(answers.whichChildrenDisability).thenReturn(Some(Set(0)))
            when(answers.registeredBlind).thenReturn(Some(false))
            when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
            when(answers.doYouLiveWithPartner).thenReturn(Some(false))
            when(answers.now).thenReturn(testDate)

            val result = navigator.nextPage(RegisteredBlindId).value(answers)
            result mustEqual routes.YourIncomeInfoController.onPageLoad()
          }
      }

      "redirect to Your Income This Year" when {
        "the child is over 16 and is single parent" in {
          val answers: UserAnswers = spy(userAnswers())
          when(answers.noOfChildren).thenReturn(Some(1))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))
          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", ageOfOver16))))

          val result = navigator.nextPage(RegisteredBlindId).value(answers)
          result mustEqual routes.YourIncomeInfoController.onPageLoad()
        }
      }

      "redirect to Partner Income This Year" when {
        "the child is over 16 and is single parent" in {
          val answers: UserAnswers = spy(userAnswers())
          when(answers.noOfChildren).thenReturn(Some(1))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))
          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", ageOfOver16))))

          val result = navigator.nextPage(RegisteredBlindId).value(answers)
          result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
        }
      }
    }

    "user has multiple children" when {

      "redirect to `Which children are registered blind` when the user answers `Yes`" in {
        val answers: UserAnswers = mock[UserAnswers]
        when(answers.noOfChildren).thenReturn(Some(2))
        when(answers.childrenWithCosts).thenReturn(None)
        when(answers.registeredBlind).thenReturn(Some(true))
        val result = navigator.nextPage(RegisteredBlindId).value(answers)
        result mustEqual routes.WhichChildrenBlindController.onPageLoad()
      }

      "redirect to `Your Income This Year` when the user answers `No` and all the children aged above 16, single user" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.noOfChildren).thenReturn(Some(2))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.aboutYourChild).thenReturn(
          Some(Map(0 -> AboutYourChild("Test", ageOfOver16), 1 -> AboutYourChild("Dan", ageOfOver16)))
        )

        val result = navigator.nextPage(RegisteredBlindId).value(answers)
        result mustEqual routes.YourIncomeInfoController.onPageLoad()
      }

      "redirect to `Partner Income This Year` when the user answers `No` and all the children aged above 16, both user" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.noOfChildren).thenReturn(Some(2))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.aboutYourChild).thenReturn(
          Some(Map(0 -> AboutYourChild("Test", ageOfOver16), 1 -> AboutYourChild("Dan", ageOfOver16)))
        )

        val result = navigator.nextPage(RegisteredBlindId).value(answers)
        result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
      }

      "redirect to `Who has childcare costs` when the user answers `No` and all the children aged below 16, both user" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.noOfChildren).thenReturn(Some(2))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.aboutYourChild).thenReturn(
          Some(
            Map(
              0 -> AboutYourChild("Test", testDate.minusMonths(3)),
              1 -> AboutYourChild("Dan", testDate.minusMonths(10))
            )
          )
        )

        val result = navigator.nextPage(RegisteredBlindId).value(answers)
        result mustEqual routes.WhoHasChildcareCostsController.onPageLoad()
      }

      "redirect to `Childcare Pay Frequency` when the user answers `No` and the children aged below and above 16" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.noOfChildren).thenReturn(Some(2))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.aboutYourChild).thenReturn(
          Some(Map(0 -> AboutYourChild("Test", ageOfOver16), 1 -> AboutYourChild("Dan", dob)))
        )

        val result = navigator.nextPage(RegisteredBlindId).value(answers)
        result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(1)
      }
    }

    "redirect to `Session Expired` when `childrenWithCosts` is undefined and there's a single child" in {
      val answers: UserAnswers = mock[UserAnswers]
      when(answers.noOfChildren).thenReturn(Some(1))
      when(answers.childrenWithCosts).thenReturn(None)
      when(answers.registeredBlind).thenReturn(Some(false))
      val result = navigator.nextPage(RegisteredBlindId).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad
    }

    "redirect to `Session Expired` when `registeredBlind` is undefined" in {
      val answers: UserAnswers = mock[UserAnswers]
      when(answers.registeredBlind).thenReturn(None)
      when(answers.noOfChildren).thenReturn(Some(2))
      when(answers.childrenWithCosts).thenReturn(Some(Set(2, 5)))
      val result = navigator.nextPage(RegisteredBlindId).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad
    }

    "redirect to `Session Expired` when `noOfChildren` is undefined" in {
      val answers: UserAnswers = mock[UserAnswers]
      when(answers.registeredBlind).thenReturn(None)
      when(answers.noOfChildren).thenReturn(None)
      when(answers.childrenWithCosts).thenReturn(Some(Set(2, 5)))
      val result = navigator.nextPage(RegisteredBlindId).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad
    }
  }

  "Which children are registered blind" must {
    "redirect to `Who has childcare costs` for single user and more than 1 child age below 16" in {

      val answers: UserAnswers = spy(userAnswers())
      when(answers.noOfChildren).thenReturn(Some(3))
      when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
      when(answers.registeredBlind).thenReturn(Some(true))
      when(answers.whichChildrenBlind).thenReturn(Some(Set(0)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      when(answers.aboutYourChild).thenReturn(
        Some(
          Map(
            0 -> AboutYourChild("Test", dob),
            1 -> AboutYourChild("Dan", dob),
            2 -> AboutYourChild("Tan", ageOfOver16)
          )
        )
      )

      val result = navigator.nextPage(WhichChildrenBlindId).value(answers)

      result mustEqual routes.WhoHasChildcareCostsController.onPageLoad()
    }

    "redirect to `Childcare Pay Frequency` for single parent and 1 child age below 16" in {

      val answers: UserAnswers = spy(userAnswers())
      when(answers.noOfChildren).thenReturn(Some(3))
      when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
      when(answers.registeredBlind).thenReturn(Some(true))
      when(answers.whichChildrenBlind).thenReturn(Some(Set(0)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      when(answers.aboutYourChild).thenReturn(
        Some(
          Map(
            0 -> AboutYourChild("Test", ageOfOver16),
            1 -> AboutYourChild("Dan", testDate.minusYears(2)),
            2 -> AboutYourChild("Tan", ageOfOver16)
          )
        )
      )

      val result = navigator.nextPage(WhichChildrenBlindId).value(answers)

      result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(1)
    }

    "redirect to 'Your Income This Year' when single user with multiple children and all above 16" in {
      val answers: UserAnswers = spy(userAnswers())
      when(answers.noOfChildren).thenReturn(Some(2))
      when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
      when(answers.registeredBlind).thenReturn(Some(true))
      when(answers.whichChildrenBlind).thenReturn(Some(Set(0)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      when(answers.aboutYourChild).thenReturn(
        Some(Map(0 -> AboutYourChild("Test", ageOfOver16), 1 -> AboutYourChild("Dan", ageOfOver16)))
      )

      val result = navigator.nextPage(WhichChildrenBlindId).value(answers)
      result mustEqual routes.YourIncomeInfoController.onPageLoad()
    }

    "redirect to 'Partner Income This Year' when single user with multiple children and all above 16" in {
      val answers: UserAnswers = spy(userAnswers())
      when(answers.noOfChildren).thenReturn(Some(2))
      when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
      when(answers.registeredBlind).thenReturn(Some(true))
      when(answers.whichChildrenBlind).thenReturn(Some(Set(0)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(true))
      when(answers.aboutYourChild).thenReturn(
        Some(Map(0 -> AboutYourChild("Test", ageOfOver16), 1 -> AboutYourChild("Dan", ageOfOver16)))
      )

      val result = navigator.nextPage(WhichChildrenBlindId).value(answers)
      result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
    }
  }

  "Who has childcare costs" must {

    "redirect to `How often do you expect to pay for childcare` when `childrenWithCosts` returns a non empty `Set`" in {
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(Some(Set(2, 5)))
      val result = navigator.nextPage(WhoHasChildcareCostsId).value(answers)
      result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(2)
    }

    "redirect to `Session Expired` when `childrenWithCosts` returns an empty `Set`" in {
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(Some(Set.empty[Int]))
      val result = navigator.nextPage(WhoHasChildcareCostsId).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad
    }

    "redirect to `Session Expired` when `childrenWithCosts` is undefined" in {
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(None)
      val result = navigator.nextPage(WhoHasChildcareCostsId).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad
    }
  }

  "How often do you expect to pay for childcare" must
    Seq(0, 1, 2).foreach { id =>
      s"redirect to `What are your expected childcare costs` for the current child, for id: $id" in {
        val result = navigator.nextPage(ChildcarePayFrequencyId(id)).value(userAnswers())
        result mustEqual routes.ExpectedChildcareCostsController.onPageLoad(id)
      }
    }

  "Expected Childcare Costs" when {

    "there are still children to enter childcare costs for" must {

      "redirect to ChildcarePayFrequencyController" when {

        "there is NO partner" in {
          val answers        = mock[UserAnswers]
          val currentChildId = 0
          when(answers.childrenWithCosts).thenReturn(Some(Set(0, 2)))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))

          val result = navigator.nextPage(ExpectedChildcareCostsId(currentChildId)).value(answers)

          result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(2)
        }

        "there is partner" in {
          val answers        = mock[UserAnswers]
          val currentChildId = 0
          when(answers.childrenWithCosts).thenReturn(Some(Set(0, 2)))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))

          val result = navigator.nextPage(ExpectedChildcareCostsId(currentChildId)).value(answers)

          result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(2)
        }
      }
    }

    "there are no more children to enter childcare costs for" when {

      "there is NO partner" when {

        "the parent DOES receive childcare vouchers" must {
          "redirect to YourIncomeInfoController" in {
            val answers        = mock[UserAnswers]
            val currentChildId = 2
            when(answers.childrenWithCosts).thenReturn(Some(Set(0, 2)))
            when(answers.doYouLiveWithPartner).thenReturn(Some(false))
            when(answers.hasVouchers).thenReturn(true)

            val result = navigator.nextPage(ExpectedChildcareCostsId(currentChildId)).value(answers)

            result mustEqual routes.YourIncomeInfoController.onPageLoad()
          }
        }

        "the parent does NOT receive childcare vouchers" must {
          "redirect to ResultController" in {
            val answers        = mock[UserAnswers]
            val currentChildId = 2
            when(answers.childrenWithCosts).thenReturn(Some(Set(0, 2)))
            when(answers.doYouLiveWithPartner).thenReturn(Some(false))
            when(answers.hasVouchers).thenReturn(false)

            val result = navigator.nextPage(ExpectedChildcareCostsId(currentChildId)).value(answers)

            result mustEqual routes.ResultController.onPageLoad()
          }
        }
      }

      "there is partner" must {

        "redirect to PartnerIncomeInfoController" when {
          "any of the parents DOES receive childcare vouchers (UserAnswers.hasVouchers returns true)" in {
            val answers        = mock[UserAnswers]
            val currentChildId = 2
            when(answers.childrenWithCosts).thenReturn(Some(Set(0, 2)))
            when(answers.doYouLiveWithPartner).thenReturn(Some(true))
            when(answers.hasVouchers).thenReturn(true)

            val result = navigator.nextPage(ExpectedChildcareCostsId(currentChildId)).value(answers)

            result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
          }
        }

        "redirect to ResultController" when {
          "neither of the parents receives childcare vouchers (UserAnswers.hasVouchers returns false)" in {
            val answers        = mock[UserAnswers]
            val currentChildId = 2
            when(answers.childrenWithCosts).thenReturn(Some(Set(0, 2)))
            when(answers.doYouLiveWithPartner).thenReturn(Some(true))
            when(answers.hasVouchers).thenReturn(false)

            val result = navigator.nextPage(ExpectedChildcareCostsId(currentChildId)).value(answers)

            result mustEqual routes.ResultController.onPageLoad()
          }
        }
      }
    }

    "UserAnswers.doYouLiveWithPartner returns empty Option" must {
      "redirect to SessionExpiredController" in {
        val answers        = mock[UserAnswers]
        val currentChildId = 13
        when(answers.childrenWithCosts).thenReturn(None)
        when(answers.hasVouchers).thenReturn(true)
        when(answers.doYouLiveWithPartner).thenReturn(None)

        val result = navigator.nextPage(ExpectedChildcareCostsId(currentChildId)).value(answers)

        result mustEqual routes.SessionExpiredController.onPageLoad
      }
    }
  }

}
