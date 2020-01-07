/*
 * Copyright 2020 HM Revenue & Customs
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

import org.joda.time.LocalDate
import org.mockito.Mockito._
import org.scalatest.OptionValues
import org.scalatestplus.mockito.MockitoSugar
import play.api.libs.json.{JsBoolean, JsNumber, JsValue, Json}
import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.DataGenerator.{ageExactly15Relative, ageOf16WithBirthdayBefore31stAugust, ageOf19YearsAgo, ageOfOver16Relative}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, NormalMode, WhichBenefitsEnum, YouPartnerBothNeitherEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.http.cache.client.CacheMap

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
  private def userAnswersOverride(data: (String, JsValue)*)(newDate : LocalDate): UserAnswers =
    new UserAnswers(CacheMap("", data.toMap)) {
      override def now = newDate
    }

  private def aboutYourChildren(children: (String, LocalDate)*): (String, JsValue) =
    AboutYourChildId.toString -> Json.toJson(children.zipWithIndex.map {
      case ((name, dateOfBirth), i) =>
        i.toString -> Json.toJson(AboutYourChild(name, dateOfBirth))
    }.toMap)

  "Number of children" must {
    "redirect to `About your child`" in {
      val result = navigator.nextPage(NoOfChildrenId, NormalMode).value(mock[UserAnswers])
      result mustEqual routes.AboutYourChildController.onPageLoad(NormalMode, 0)
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
        val result = navigator.nextPage(AboutYourChildId(0), NormalMode).value(answers)
        result mustEqual routes.AboutYourChildController.onPageLoad(NormalMode, 1)
      }
    }

    "this is the last child" when {

      "redirect to `Approved education or training` when there is at least one child over 16" in {
        val answers: UserAnswers = userAnswersOverride(
          NoOfChildrenId.toString -> JsNumber(2),
          aboutYourChildren(
            foo -> ageOfOver16,
            bar -> dob
          )
        )(testDate)
        val result = navigator.nextPage(AboutYourChildId(1), NormalMode).value(answers)
        result mustEqual routes.ChildApprovedEducationController.onPageLoad(NormalMode, 0)
      }

      "redirect to `Do any children get disability benefits` when there are no children over 16" in {
        val answers: UserAnswers = userAnswersOverride(
          NoOfChildrenId.toString -> JsNumber(2),
          aboutYourChildren(
            foo -> ageOfExactly15,
            bar -> ageOfExactly15
          )
        )(testDate)
        val result = navigator.nextPage(AboutYourChildId(1), NormalMode).value(answers)
        result mustEqual routes.ChildrenDisabilityBenefitsController.onPageLoad(NormalMode)
      }
    }

    "redirect to `SessionExpired` when no answer for `noOfChildren` is given" in {
      val answers: UserAnswers = userAnswers(
        aboutYourChildren(
          foo -> dob,
          "bar" -> dob
        )
      )
      val result = navigator.nextPage(AboutYourChildId(0), NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }

    "redirect to `SessionExpired` when no answer for `aboutYourChild` is given" in {
      val answers: UserAnswers = userAnswers(
        NoOfChildrenId.toString -> JsNumber(2)
      )
      val result = navigator.nextPage(AboutYourChildId(0), NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }
  }

  "Approved education or training" must {

    "user answers `Yes`" when {

      "redirect to `Child start education` if child is over 19" in {
        val answers: UserAnswers = userAnswers(
          defaultAboutYourChildren,
          ChildApprovedEducationId.toString -> Json.obj(
            "0" -> true
          )
        )
        val result = navigator.nextPage(ChildApprovedEducationId(0), NormalMode).value(answers)
        result mustEqual routes.ChildStartEducationController.onPageLoad(NormalMode, 0)
      }

      "redirect to `Approved education or training` if child is under 19 and this is not the last child" in {
        val answers: UserAnswers = userAnswersOverride(
          aboutYourChildren(
            foo -> ageOfOver16,
            "Spoon" -> dob,
            bar -> ageOfOver16,
            "Baz" -> dob
          ),
          ChildApprovedEducationId.toString -> Json.obj(
            "0" -> true
          )
        )(testDate)
        val result = navigator.nextPage(ChildApprovedEducationId(0), NormalMode).value(answers)
        result mustEqual routes.ChildApprovedEducationController.onPageLoad(NormalMode, 2)
      }

      "redirect to `Do your children get disability benefits` if child is under 19 and this is the last child" in {
        val answers: UserAnswers = userAnswersOverride(
          defaultAboutYourChildren,
          ChildApprovedEducationId.toString -> Json.obj(
            "0" -> true,
            "2" -> true
          )
        )(testDate)
        val result = navigator.nextPage(ChildApprovedEducationId(2), NormalMode).value(answers)
        result mustEqual routes.ChildrenDisabilityBenefitsController.onPageLoad(NormalMode)
      }
    }

    "user answers `No`" when {

      "redirect to `Approved education or training` for the next child if this is not the last child" in {
        val answers: UserAnswers = userAnswers(
          defaultAboutYourChildren,
          ChildApprovedEducationId.toString -> Json.obj(
            "0" -> false
          )
        )
        val result = navigator.nextPage(ChildApprovedEducationId(0), NormalMode).value(answers)
        result mustEqual routes.ChildApprovedEducationController.onPageLoad(NormalMode, 2)
      }

      "redirect to `Do your children get disability benefits` if this is the last child" in {
        val answers: UserAnswers = userAnswers(
          defaultAboutYourChildren,
          ChildApprovedEducationId.toString -> Json.obj(
            "0" -> false,
            "2" -> false
          )
        )
        val result = navigator.nextPage(ChildApprovedEducationId(2), NormalMode).value(answers)
        result mustEqual routes.ChildrenDisabilityBenefitsController.onPageLoad(NormalMode)
      }
    }

    "redirect to `SessionExpired` if there is no answer for `ChildApprovedEducation`" in {
      val answers: UserAnswers = userAnswers(
        defaultAboutYourChildren
      )
      val result = navigator.nextPage(ChildApprovedEducationId(0), NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }

    "redirect to `SessionExpired` if there is no answer for `AboutYourChild`" in {
      val answers: UserAnswers = userAnswers(
        ChildApprovedEducationId.toString -> Json.obj(
          "0" -> false,
          "1" -> false
        )
      )
      val result = navigator.nextPage(ChildApprovedEducationId(0), NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }

    def defaultAboutYourChildren: (String, JsValue) =
      aboutYourChildren(
        foo -> ageOf19,
        "Spoon" -> dob,
        bar -> ageOfOver16,
        "Baz" -> dob
      )
  }

  "Approved education start date" must {

    "redirect to `Approved education or training` when this is not the last applicable child" in {
      val result = navigator.nextPage(ChildStartEducationId(0), NormalMode).value(answers)
      result mustEqual routes.ChildApprovedEducationController.onPageLoad(NormalMode, 2)
    }

    "redirect to `Do your children get disability benefits` when this is the last applicable child" in {
      val result = navigator.nextPage(ChildStartEducationId(2), NormalMode).value(answers)
      result mustEqual routes.ChildrenDisabilityBenefitsController.onPageLoad(NormalMode)
    }

    lazy val answers: UserAnswers = userAnswers(
      aboutYourChildren(
        foo -> ageOf19,
        "Spoon" -> dob,
        bar -> ageOfOver16,
        "Baz" -> dob
      )
    )

    "redirect to `SessionExpired` if the user has no answer for `About your child`" in {
      val result = navigator.nextPage(ChildStartEducationId(0), NormalMode).value(userAnswers())
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }
  }

  "Do any of your children get disability benefits" must {

    "redirect to `Any children blind` when the user answers `No` when the user has 1 child" in {
      val answers: UserAnswers = userAnswers(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
        NoOfChildrenId.toString -> JsNumber(1)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId, NormalMode).value(answers)
      result mustEqual routes.RegisteredBlindController.onPageLoad(NormalMode)
    }

    "redirect to `Any children blind` when the user answers `No` when the user has more than 1 child" in {
      val answers: UserAnswers = userAnswers(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
        NoOfChildrenId.toString -> JsNumber(2)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId, NormalMode).value(answers)
      result mustEqual routes.RegisteredBlindController.onPageLoad(NormalMode)
    }

    "redirect to `Which disability benefits` when the user answers `Yes` and has 1 child" in {
      val answers: UserAnswers = userAnswers(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
        NoOfChildrenId.toString -> JsNumber(1)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId, NormalMode).value(answers)
      result mustEqual routes.WhichDisabilityBenefitsController.onPageLoad(NormalMode, 0)
    }

    "redirect to `Which of your children get disability benefits` when the user answers `Yes` and has more than 1 child" in {
      val answers: UserAnswers = userAnswers(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
        NoOfChildrenId.toString -> JsNumber(2)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId, NormalMode).value(answers)
      result mustEqual routes.WhichChildrenDisabilityController.onPageLoad(NormalMode)
    }

    "redirect to `SessionExpired` when the user has no answer for `Do any of your children get disability benefits`" in {
      val answers: UserAnswers = userAnswers(
        NoOfChildrenId.toString -> JsNumber(1)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId, NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }

    "redirect to `SessionExpired` when the user has no answer for `Number of children`" in {
      val answers: UserAnswers = userAnswers(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId, NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }
  }

  "Which of your children get disability benefits" must {

    Seq(0, 2).foreach {
      id =>
        s"redirect to `WhichDisabilityBenefits` for the first appropriate child, for id: $id" in {
          val answers: UserAnswers = userAnswers(
            WhichChildrenDisabilityId.toString -> Json.toJson(Seq(id))
          )
          val result = navigator.nextPage(WhichChildrenDisabilityId, NormalMode).value(answers)
          result mustEqual routes.WhichDisabilityBenefitsController.onPageLoad(NormalMode, id)
        }
    }

    "redirect to `SessionExpired` when there is no answer for `WhichChildrenDisability`" in {
      val result = navigator.nextPage(WhichChildrenDisabilityId, NormalMode).value(userAnswers())
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }
  }

  "Which disability benefits" must {

    "redirect to `Which disability benefits` for the next applicable child, if this is not the last child" in {
      val answers: UserAnswers = userAnswers(
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2))
      )
      val result = navigator.nextPage(WhichDisabilityBenefitsId(0), NormalMode).value(answers)
      result mustEqual routes.WhichDisabilityBenefitsController.onPageLoad(NormalMode, 2)
    }

    "redirect to `Any children blind` when this is the last applicable child" in {
      val answers: UserAnswers = userAnswers(
        WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 2))
      )
      val result = navigator.nextPage(WhichDisabilityBenefitsId(2), NormalMode).value(answers)
      result mustEqual routes.RegisteredBlindController.onPageLoad(NormalMode)
    }

    "redirect to `Any children blind` when this is the only child" in {
      val answers: UserAnswers = userAnswers(
        NoOfChildrenId.toString -> JsNumber(1),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true)
      )
      val result = navigator.nextPage(WhichDisabilityBenefitsId(0), NormalMode).value(answers)
      result mustEqual routes.RegisteredBlindController.onPageLoad(NormalMode)
    }

    "redirect to `SessionExpired` when there is no answer for `WhichChildrenDisability`" in {
      val result = navigator.nextPage(WhichDisabilityBenefitsId(0), NormalMode).value(userAnswers())
      result mustEqual routes.SessionExpiredController.onPageLoad()
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

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, 0)
      }

      "redirect to `How often do you expect to pay for childcare` when the user answers `No` and child age below 16" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.parse("2003-1-1")))))
        when(answers.noOfChildren).thenReturn(Some(1))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.childrenDisabilityBenefits).thenReturn(Some(true))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.now).thenReturn(testDate)

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, 0)
      }

      "redirect to `How often do you expect to pay for childcare` when the user answers `No, disable child age 16 and dob before august" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.parse("2003-1-1")))))
        when(answers.noOfChildren).thenReturn(Some(1))
        when(answers.childrenDisabilityBenefits).thenReturn(Some(true))

        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.now).thenReturn(testDate)

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, 0)
      }

      "redirect to `How often do you expect to pay for childcare` when the user answers `No, blind child age 16 and dob before august" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.parse("2003-1-1")))))
        when(answers.noOfChildren).thenReturn(Some(1))
        when(answers.childDisabilityBenefits).thenReturn(Some(false))

        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(true))
        when(answers.now).thenReturn(testDate)

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, 0)
      }

      "redirect to  Your Income This Year" when {
        "the user answers `No and child is 16 years and dob before august and not disable" in {
          val answers: UserAnswers = spy(userAnswers())
          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.parse("2002-1-1")))))
          when(answers.noOfChildren).thenReturn(Some(1))
          when(answers.childDisabilityBenefits).thenReturn(Some(false))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))


          val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
          result mustEqual routes.YourIncomeInfoController.onPageLoad()
        }
      }

      "redirect to  Partner Income This Year" when {
        "the user answers `No and child is 16 years and dob before august and not disable" in {
          val answers: UserAnswers = spy(userAnswers())
          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.parse("2002-1-1")))))
          when(answers.noOfChildren).thenReturn(Some(1))
          when(answers.childDisabilityBenefits).thenReturn(Some(false))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))

          val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
          result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
        }
      }

      "user with multiple children" when{
        "redirect to Childcare Pay Frequency if the user answers 'No' to " +
          "registered blind and 1 child is 16 years and dob is before august and disabled" in{

          val answers: UserAnswers = spy(userAnswers())


          when(answers.aboutYourChild).thenReturn(Some(
            Map(0 -> AboutYourChild(foo, ageOf19),
              1 -> AboutYourChild(bar, ageOf16Before31Aug),
              2 -> AboutYourChild("Quux", ageOf19)
            )
          ))
          when(answers.noOfChildren).thenReturn(Some(3))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.whichChildrenDisability).thenReturn(Some(Set(0, 1)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.now).thenReturn(testDate)

          val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
          result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, 1)
        }


        "redirect to Childcare Pay Frequency if the user answers 'No' to " +
          "registered blind and more than 1 child is 16 years and dob is before august but only 1 is disabled " in{

          val answers: UserAnswers = spy(userAnswers())

          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild(foo, ageOf16Before31Aug),
            1 -> AboutYourChild(bar, ageOf19),
            2 -> AboutYourChild(bar, ageOf16Before31Aug),
            3 -> AboutYourChild("Quux", ageOf19))))
          when(answers.noOfChildren).thenReturn(Some(4))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.whichChildrenDisability).thenReturn(Some(Set(0, 1)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.now).thenReturn(testDate)

          val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
          result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, 0)
        }

        "redirect to Partner Income Info when user answers 'No' to registered blind and " +
          "1 child is 16 years and dob is before august and same child is not disabled" in{


          val answers: UserAnswers = spy(userAnswers())

          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild(foo, ageOf19),
            1 -> AboutYourChild(bar, ageOf16Before31Aug),
            2 -> AboutYourChild("Quux", ageOf19))))
          when(answers.noOfChildren).thenReturn(Some(3))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.whichChildrenDisability).thenReturn(Some(Set(0, 2)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))
          when(answers.now).thenReturn(testDate)


          val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
          result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
        }

        "redirect to Your Income Info when single user answers 'No' to registerd blind and " +
          "1 child is 16 years and dob is before august and not disable " in {

          val answers: UserAnswers = spy(userAnswers())

          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild(foo, ageOf19),
            1 -> AboutYourChild(bar, ageOf16Before31Aug),
            2 -> AboutYourChild("Quux", ageOf19))))
          when(answers.noOfChildren).thenReturn(Some(3))
          when(answers.childrenDisabilityBenefits).thenReturn(Some(false))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))
          when(answers.now).thenReturn(testDate)

          val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
          result mustEqual routes.YourIncomeInfoController.onPageLoad()
        }

        "redirect to Your Income Info when user with partner answers 'No' to registerd blind and " +
          "1 child is 16 years and dob is before august and not disabled " in{

          val answers: UserAnswers = spy(userAnswers())


          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild(foo, ageOf19),
            1 -> AboutYourChild(bar, ageOf16Before31Aug),
            2 -> AboutYourChild("Quux", ageOf19))))
          when(answers.noOfChildren).thenReturn(Some(3))
          when(answers.childrenDisabilityBenefits).thenReturn(Some(false))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))
          when(answers.now).thenReturn(testDate)

          val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
          result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
        }

        "redirect to  Partner Income Info when user answers 'No' to registerd blind and more than " +
          "1 child is 16 years and dob is before august and not disable" in{

          val answers: UserAnswers = spy(userAnswers())


          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild(foo, ageOf19),
            1 -> AboutYourChild(bar, ageOf16Before31Aug),
            2 -> AboutYourChild("Quux", ageOf19),
            3 -> AboutYourChild("Max", ageOf16Before31Aug))))
          when(answers.noOfChildren).thenReturn(Some(4))
          when(answers.childrenDisabilityBenefits).thenReturn(Some(false))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(true))
          when(answers.now).thenReturn(testDate)

          val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
          result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
        }

        "redirects to Who Has Childcare Costs when the user answers 'No' and more than " +
          "1 child is 16 years and dob is before august and disable" in{

          val answers: UserAnswers = spy(userAnswers())

          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild(foo, ageOf19),
            1 -> AboutYourChild(bar, ageOf16Before31Aug),
            2 -> AboutYourChild("Quux", ageOf19),
            3 -> AboutYourChild("Max", ageOf16Before31Aug))))
          when(answers.noOfChildren).thenReturn(Some(4))
          when(answers.childrenDisabilityBenefits).thenReturn(Some(true))
          when(answers.whichChildrenDisability).thenReturn(Some(Set(0, 1,3)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.now).thenReturn(testDate)

          val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
          result mustEqual routes.WhoHasChildcareCostsController.onPageLoad()
        }


        "redirects to Your Income Info when user answers 'No' and more than " +
          "1 child is 16 years and dob is before august and same children are not disable" in{

          val answers: UserAnswers = spy(userAnswers())

          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild(foo, ageOf19),
            1 -> AboutYourChild(bar, ageOf16Before31Aug),
            2 -> AboutYourChild("Quux", ageOf19),
            3 -> AboutYourChild("Max", ageOf16Before31Aug))))
          when(answers.noOfChildren).thenReturn(Some(4))
          when(answers.childrenDisabilityBenefits).thenReturn(Some(true))
          when(answers.whichChildrenDisability).thenReturn(Some(Set(0)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))
          when(answers.now).thenReturn(testDate)

          val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
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

          val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
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

          val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
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
        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.WhichChildrenBlindController.onPageLoad(NormalMode)
      }

      "redirect to `Your Income This Year` when the user answers `No` and all the children aged above 16, single user" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.noOfChildren).thenReturn(Some(2))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", ageOfOver16),1 -> AboutYourChild("Dan", ageOfOver16))))

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.YourIncomeInfoController.onPageLoad()
      }

      "redirect to `Partner Income This Year` when the user answers `No` and all the children aged above 16, both user" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.noOfChildren).thenReturn(Some(2))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", ageOfOver16),1 -> AboutYourChild("Dan", ageOfOver16))))

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
      }

      "redirect to `Who has childcare costs` when the user answers `No` and all the children aged below 16, both user" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.noOfChildren).thenReturn(Some(2))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.aboutYourChild).thenReturn(Some(Map(
          0 -> AboutYourChild("Test", testDate.minusMonths(3)),
          1 -> AboutYourChild("Dan", testDate.minusMonths(10))
        )))

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.WhoHasChildcareCostsController.onPageLoad()
      }

      "redirect to `Childcare Pay Frequency` when the user answers `No` and the children aged below and above 16" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.noOfChildren).thenReturn(Some(2))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", ageOfOver16),1 -> AboutYourChild("Dan", dob))))

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode,1)
      }
    }

    "redirect to `Session Expired` when `childrenWithCosts` is undefined and there's a single child" in {
      val answers: UserAnswers = mock[UserAnswers]
      when(answers.noOfChildren).thenReturn(Some(1))
      when(answers.childrenWithCosts).thenReturn(None)
      when(answers.registeredBlind).thenReturn(Some(false))
      val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }

    "redirect to `Session Expired` when `registeredBlind` is undefined" in {
      val answers: UserAnswers = mock[UserAnswers]
      when(answers.registeredBlind).thenReturn(None)
      when(answers.noOfChildren).thenReturn(Some(2))
      when(answers.childrenWithCosts).thenReturn(Some(Set(2, 5)))
      val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }

    "redirect to `Session Expired` when `noOfChildren` is undefined" in {
      val answers: UserAnswers = mock[UserAnswers]
      when(answers.registeredBlind).thenReturn(None)
      when(answers.noOfChildren).thenReturn(None)
      when(answers.childrenWithCosts).thenReturn(Some(Set(2, 5)))
      val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
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
      when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", dob),1 -> AboutYourChild("Dan", dob),2 -> AboutYourChild("Tan", ageOfOver16))))


      val result = navigator.nextPage(WhichChildrenBlindId, NormalMode).value(answers)

      result mustEqual routes.WhoHasChildcareCostsController.onPageLoad(NormalMode)
    }


    "redirect to `Childcare Pay Frequency` for single parent and 1 child age below 16" in {

      val answers: UserAnswers = spy(userAnswers())
      when(answers.noOfChildren).thenReturn(Some(3))
      when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
      when(answers.registeredBlind).thenReturn(Some(true))
      when(answers.whichChildrenBlind).thenReturn(Some(Set(0)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      when(answers.aboutYourChild).thenReturn(Some(Map(
        0 -> AboutYourChild("Test", ageOfOver16),
        1 -> AboutYourChild("Dan", testDate.minusYears(2)),
        2 -> AboutYourChild("Tan", ageOfOver16)
      )))


      val result = navigator.nextPage(WhichChildrenBlindId, NormalMode).value(answers)

      result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode,1)
    }


    "redirect to 'Your Income This Year' when single user with multiple children and all above 16" in {
      val answers: UserAnswers = spy(userAnswers())
      when(answers.noOfChildren).thenReturn(Some(2))
      when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
      when(answers.registeredBlind).thenReturn(Some(true))
      when(answers.whichChildrenBlind).thenReturn(Some(Set(0)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", ageOfOver16),1 -> AboutYourChild("Dan", ageOfOver16))))

      val result = navigator.nextPage(WhichChildrenBlindId, NormalMode).value(answers)
      result mustEqual routes.YourIncomeInfoController.onPageLoad()
    }

    "redirect to 'Partner Income This Year' when single user with multiple children and all above 16" in {
      val answers: UserAnswers = spy(userAnswers())
      when(answers.noOfChildren).thenReturn(Some(2))
      when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
      when(answers.registeredBlind).thenReturn(Some(true))
      when(answers.whichChildrenBlind).thenReturn(Some(Set(0)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(true))
      when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", ageOfOver16),1 -> AboutYourChild("Dan", ageOfOver16))))

      val result = navigator.nextPage(WhichChildrenBlindId, NormalMode).value(answers)
      result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
    }
  }

  "Who has childcare costs" must {

    "redirect to `How often do you expect to pay for childcare` when `childrenWithCosts` returns a non empty `Set`" in {
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(Some(Set(2, 5)))
      val result = navigator.nextPage(WhoHasChildcareCostsId, NormalMode).value(answers)
      result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, 2)
    }

    "redirect to `Session Expired` when `childrenWithCosts` returns an empty `Set`" in {
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(Some(Set.empty[Int]))
      val result = navigator.nextPage(WhoHasChildcareCostsId, NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }

    "redirect to `Session Expired` when `childrenWithCosts` is undefined" in {
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(None)
      val result = navigator.nextPage(WhoHasChildcareCostsId, NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }
  }

  "How often do you expect to pay for childcare" must {
    Seq(0, 1, 2).foreach {
      id =>
        s"redirect to `What are your expected childcare costs` for the current child, for id: $id" in {
          val result = navigator.nextPage(ChildcarePayFrequencyId(id), NormalMode).value(userAnswers())
          result mustEqual routes.ExpectedChildcareCostsController.onPageLoad(NormalMode, id)
        }
    }
  }

  "What are your expected childcare costs" must {
    def setupNavigator(value: Call): ChildcareNavigator = new ChildcareNavigator(new Utils()) {
      override def now: LocalDate = testDate
      override def isEligibleForTaxCredits(answers: UserAnswers, hasPartner: Boolean): Call = value
    }

    val yourIncomeNavigator = setupNavigator(routes.YourIncomeInfoController.onPageLoad())

    "redirect to `Your income this year` for a single user when this is the last child" in {
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(Some(Set(0, 3, 4)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      val result = yourIncomeNavigator.nextPage(ExpectedChildcareCostsId(4), NormalMode).value(answers)
      result mustEqual routes.YourIncomeInfoController.onPageLoad()
    }

    "redirect to Your Income This Year for a user when only one child is younger than 16" in {
      val answers = mock[UserAnswers]
      when(answers.aboutYourChild).thenReturn(Some(Map(
        0 -> AboutYourChild("Over16",ageOf19),
        1 -> AboutYourChild("Under16",testDate),
        2 -> AboutYourChild("Over16",ageOf19)
      )))
      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      val result = yourIncomeNavigator.nextPage(ExpectedChildcareCostsId(1), NormalMode).value(answers)
      result mustEqual routes.YourIncomeInfoController.onPageLoad()
    }

    "redirect to next child for a user when two children are younger than 16" in {
      val answers = mock[UserAnswers]
      when(answers.aboutYourChild).thenReturn(Some(Map(
        0 -> AboutYourChild("Over16",ageOf19),
        1 -> AboutYourChild("Under16",testDate),
        2 -> AboutYourChild("Under16",ageOf19)
      )))
      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      val result = yourIncomeNavigator.nextPage(ExpectedChildcareCostsId(1), NormalMode).value(answers)
      result mustEqual routes.YourIncomeInfoController.onPageLoad()
    }

    "redirect to Childcare Pay Frequency when two of the children are younger than 16 and we've entered details for the first under 16" in {
      val answers = mock[UserAnswers]
      when(answers.aboutYourChild).thenReturn(Some(Map(
        0 -> AboutYourChild("Over16",ageOf19),
        1 -> AboutYourChild("Under16",testDate),
        2 -> AboutYourChild("Over16",testDate)
      )))
      when(answers.childrenWithCosts).thenReturn(Some(Set(1,2)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(true))
      val result = navigator.nextPage(ExpectedChildcareCostsId(1), NormalMode).value(answers)
      result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, 2)
    }


    "redirect to `Your partner's income this year` for a partner user when this is the last child" in {
      val partnerNavigator = setupNavigator(routes.PartnerIncomeInfoController.onPageLoad())
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(Some(Set(0, 3, 4)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(true))
      val result = partnerNavigator.nextPage(ExpectedChildcareCostsId(4), NormalMode).value(answers)
      result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
    }

    "redirect to `What are your expected childcare costs` for the next child when this is not the last child" in {
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(Some(Set(0, 3, 4)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      val result = navigator.nextPage(ExpectedChildcareCostsId(3), NormalMode).value(answers)
      result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, 4)
    }

    "redirect to `Session Expired` when `doYouLiveWithPartner` is undefined" in {
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(Some(Set(0, 3, 4)))
      when(answers.doYouLiveWithPartner).thenReturn(None)
      val result = navigator.nextPage(ExpectedChildcareCostsId(3), NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }
  }

  "notEligibleForTaxCredits" must {
    "redirect to the results page" when {
      "taxOrUniversal is not 'tc', neither parent or partner is on severe disability premium" in {
        val answers = mock[UserAnswers]
        when(answers.hasVouchers).thenReturn(false)
        when(answers.whichBenefitsYouGet).thenReturn(Some(Set(WhichBenefitsEnum.CARERSALLOWANCE.toString)))
        when(answers.whichBenefitsPartnerGet).thenReturn(Some(Set(WhichBenefitsEnum.DISABILITYBENEFITS.toString, WhichBenefitsEnum.INCOMEBENEFITS.toString)))
        when(answers.taxOrUniversalCredits).thenReturn(Some("uc"))

        val result = navigator.isEligibleForTaxCredits(answers, hasPartner = false)
        result mustEqual routes.ResultController.onPageLoadHideTC()
      }

      "taxOrUniversal is not 'tc', neither parent or partner is on benefits" in {
        val answers = mock[UserAnswers]
        when(answers.hasVouchers).thenReturn(false)
        when(answers.whichBenefitsYouGet).thenReturn(None)
        when(answers.whichBenefitsPartnerGet).thenReturn(None)
        when(answers.taxOrUniversalCredits).thenReturn(Some("none"))

        val result = navigator.isEligibleForTaxCredits(answers, hasPartner = false)
        result mustEqual routes.ResultController.onPageLoadHideTC()
      }

      "taxOrUniversal is not 'tc', hasVouchers is false, the user has a partner" in {
        val answers = mock[UserAnswers]
        when(answers.hasVouchers).thenReturn(false)
        when(answers.whichBenefitsYouGet).thenReturn(None)
        when(answers.whichBenefitsPartnerGet).thenReturn(None)
        when(answers.taxOrUniversalCredits).thenReturn(Some("uc"))

        val result = navigator.isEligibleForTaxCredits(answers, hasPartner = true)
        result mustEqual routes.ResultController.onPageLoadHideTC()
      }
    }

    "redirect to the 'PartnerIncomeInfoController' page" when {
      "hasVouchers is false, taxOrUniversal is 'tc' and the user has a partner" in {
        val answers = mock[UserAnswers]
        when(answers.hasVouchers).thenReturn(false)
        when(answers.whichBenefitsYouGet).thenReturn(None)
        when(answers.whichBenefitsPartnerGet).thenReturn(Some(Set(WhichBenefitsEnum.INCOMEBENEFITS.toString)))
        when(answers.taxOrUniversalCredits).thenReturn(Some("tc"))

        when(answers.isOnSevereDisabilityPremium).thenCallRealMethod()

        val result = navigator.isEligibleForTaxCredits(answers, hasPartner = true)
        result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
      }

      "hasVouchers is false and the user has a partner that is severely disabled" in {
        val answers = mock[UserAnswers]
        when(answers.hasVouchers).thenReturn(false)
        when(answers.whichBenefitsYouGet).thenReturn(None)
        when(answers.whichBenefitsPartnerGet).thenReturn(Some(Set(WhichBenefitsEnum.SEVEREDISABILITYPREMIUM.toString)))
        when(answers.taxOrUniversalCredits).thenReturn(Some("uc"))

        when(answers.isOnSevereDisabilityPremium).thenCallRealMethod()

        val result = navigator.isEligibleForTaxCredits(answers, hasPartner = true)
        result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
      }

      "hasVouchers is true and the user has a partner" in {
        val answers = mock[UserAnswers]
        when(answers.hasVouchers).thenReturn(true)
        val result = navigator.isEligibleForTaxCredits(answers, hasPartner = true)
        result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
      }
    }

    "redirect to the 'YourIncomeInfoController' page" when {
      "hasVouchers is false, taxOrUniversal is 'tc' and the user is single" in {
        val answers = mock[UserAnswers]
        when(answers.hasVouchers).thenReturn(false)
        when(answers.whichBenefitsYouGet).thenReturn(None)
        when(answers.whichBenefitsPartnerGet).thenReturn(Some(Set(WhichBenefitsEnum.INCOMEBENEFITS.toString)))
        when(answers.taxOrUniversalCredits).thenReturn(Some("tc"))

        val result = navigator.isEligibleForTaxCredits(answers, hasPartner = false)
        result mustEqual routes.YourIncomeInfoController.onPageLoad()
      }

      "hasVouchers is true and the user is single" in {
        val answers = mock[UserAnswers]
        when(answers.hasVouchers).thenReturn(true)
        val result = navigator.isEligibleForTaxCredits(answers, hasPartner = false)
        result mustEqual routes.YourIncomeInfoController.onPageLoad()
      }
    }
  }
}

