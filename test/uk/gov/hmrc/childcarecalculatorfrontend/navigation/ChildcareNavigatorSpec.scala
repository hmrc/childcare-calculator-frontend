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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import org.joda.time.LocalDate
import org.scalatest.OptionValues
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import play.api.libs.json.{JsBoolean, JsNumber, JsValue, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.DataGenerator.over19
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.{SpecBase, SubNavigator}
import uk.gov.hmrc.http.cache.client.CacheMap

class ChildcareNavigatorSpec extends SpecBase with OptionValues with MockitoSugar {

  val navigator: SubNavigator = new ChildcareNavigator(new Utils())

  "Number of children" must {
    "redirect to `About your child`" in {
      val result = navigator.nextPage(NoOfChildrenId, NormalMode).value(mock[UserAnswers])
      result mustEqual routes.AboutYourChildController.onPageLoad(NormalMode, 0)
    }
  }

  "About your child" must {

    "this isn't the last child" when {
      "redirect to `About your child` for the next index" in {
        val answers: UserAnswers = userAnswers(
          NoOfChildrenId.toString -> JsNumber(2),
          aboutYourChildren(
            "Foo" -> dob,
            "Bar" -> dob
          )
        )
        val result = navigator.nextPage(AboutYourChildId(0), NormalMode).value(answers)
        result mustEqual routes.AboutYourChildController.onPageLoad(NormalMode, 1)
      }
    }

    "this is the last child" when {

      "redirect to `Approved education or training` when there is at least one child over 16" in {
        val answers: UserAnswers = userAnswers(
          NoOfChildrenId.toString -> JsNumber(2),
          aboutYourChildren(
            "Foo" -> dob16,
            "Bar" -> dob
          )
        )
        val result = navigator.nextPage(AboutYourChildId(1), NormalMode).value(answers)
        result mustEqual routes.ChildApprovedEducationController.onPageLoad(NormalMode, 0)
      }

      "redirect to `Do any children get disability benefits` when there are no children over 16" in {
        val answers: UserAnswers = userAnswers(
          NoOfChildrenId.toString -> JsNumber(2),
          aboutYourChildren(
            "Foo" -> dob,
            "Bar" -> dob
          )
        )
        val result = navigator.nextPage(AboutYourChildId(1), NormalMode).value(answers)
        result mustEqual routes.ChildrenDisabilityBenefitsController.onPageLoad(NormalMode)
      }
    }

    "redirect to `SessionExpired` when no answer for `noOfChildren` is given" in {
      val answers: UserAnswers = userAnswers(
        aboutYourChildren(
          "Foo" -> dob,
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
        val answers: UserAnswers = userAnswers(
          aboutYourChildren(
            "Foo" -> dob16,
            "Spoon" -> dob,
            "Bar" -> dob16,
            "Baz" -> dob
          ),
          ChildApprovedEducationId.toString -> Json.obj(
            "0" -> true
          )
        )
        val result = navigator.nextPage(ChildApprovedEducationId(0), NormalMode).value(answers)
        result mustEqual routes.ChildApprovedEducationController.onPageLoad(NormalMode, 2)
      }

      "redirect to `Do your children get disability benefits` if child is under 19 and this is the last child" in {
        val answers: UserAnswers = userAnswers(
          defaultAboutYourChildren,
          ChildApprovedEducationId.toString -> Json.obj(
            "0" -> true,
            "2" -> true
          )
        )
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
        "Foo" -> dob19,
        "Spoon" -> dob,
        "Bar" -> dob16,
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
        "Foo" -> dob19,
        "Spoon" -> dob,
        "Bar" -> dob16,
        "Baz" -> dob
      )
    )

    "redirect to `SessionExpired` if the user has no answer for `About your child`" in {
      val result = navigator.nextPage(ChildStartEducationId(0), NormalMode).value(userAnswers())
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }
  }

  "Do any of your children get disability benefits" must {

    "redirect to `Any children blind` when the user ansers `No` when the user has 1 child" in {
      val answers: UserAnswers = userAnswers(
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(false),
        NoOfChildrenId.toString -> JsNumber(1)
      )
      val result = navigator.nextPage(ChildrenDisabilityBenefitsId, NormalMode).value(answers)
      result mustEqual routes.RegisteredBlindController.onPageLoad(NormalMode)
    }

    "redirect to `Any children blind` when the user ansers `No` when the user has more than 1 child" in {
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
      "redirect to `How often do you expect to pay for childcare` when the user answers `Yes`" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.noOfChildren).thenReturn(Some(1))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.now().minusYears(1)))))

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, 0)
      }

      "redirect to `How often do you expect to pay for childcare` when the user answers `No`" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.now().minusYears(1)))))
        when(answers.noOfChildren).thenReturn(Some(1))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, 0)
      }

      "redirect to Your Income This Year" when {
        "the child is over 16 and is single parent" in {
          val answers: UserAnswers = spy(userAnswers())
          when(answers.noOfChildren).thenReturn(Some(1))
          when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
          when(answers.registeredBlind).thenReturn(Some(false))
          when(answers.doYouLiveWithPartner).thenReturn(Some(false))
          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", dob16))))

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
          when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", dob16))))

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
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", dob16),1 -> AboutYourChild("Dan", dob16))))

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.YourIncomeInfoController.onPageLoad()
      }

      "redirect to `Partner Income This Year` when the user answers `No` and all the children aged above 16, both user" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.noOfChildren).thenReturn(Some(2))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.doYouLiveWithPartner).thenReturn(Some(true))
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", dob16),1 -> AboutYourChild("Dan", dob16))))

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.PartnerIncomeInfoController.onPageLoad()
      }

      "redirect to `Who has childcare costs` when the user answers `No` and all the children aged below 16, both user" in {
        val answers: UserAnswers = spy(userAnswers())
        when(answers.noOfChildren).thenReturn(Some(2))
        when(answers.childrenWithCosts).thenReturn(Some(Set(0)))
        when(answers.registeredBlind).thenReturn(Some(false))
        when(answers.doYouLiveWithPartner).thenReturn(Some(false))
        when(answers.aboutYourChild).thenReturn(Some(Map(0 -> AboutYourChild("Test", LocalDate.now().minusMonths(3)),1 -> AboutYourChild("Dan", LocalDate.now().minusMonths(10)))))

        val result = navigator.nextPage(RegisteredBlindId, NormalMode).value(answers)
        result mustEqual routes.WhoHasChildcareCostsController.onPageLoad()
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
    "redirect to `Who has childcare costs`" in {
      val result = navigator.nextPage(WhichChildrenBlindId, NormalMode).value(userAnswers())
      result mustEqual routes.WhoHasChildcareCostsController.onPageLoad(NormalMode)
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

    "redirect to `Your income this year` for a single user when this is the last child" in {
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(Some(Set(0, 3, 4)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      val result = navigator.nextPage(ExpectedChildcareCostsId(4), NormalMode).value(answers)
      result mustEqual routes.YourIncomeInfoController.onPageLoad()
    }

    "redirect to `Your partner's income this year` for a partner user when this is the last child" in {
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(Some(Set(0, 3, 4)))
      when(answers.doYouLiveWithPartner).thenReturn(Some(true))
      val result = navigator.nextPage(ExpectedChildcareCostsId(4), NormalMode).value(answers)
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

    "redirect to `Session Expired` when `childrenWithCosts` is undefined" in {
      val answers = mock[UserAnswers]
      when(answers.childrenWithCosts).thenReturn(None)
      when(answers.doYouLiveWithPartner).thenReturn(Some(false))
      val result = navigator.nextPage(ExpectedChildcareCostsId(3), NormalMode).value(answers)
      result mustEqual routes.SessionExpiredController.onPageLoad()
    }
  }

  private def userAnswers(data: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", data.toMap))

  private def aboutYourChildren(children: (String, LocalDate)*): (String, JsValue) =
    AboutYourChildId.toString -> Json.toJson(children.zipWithIndex.map {
      case ((name, dob), i) =>
        i.toString -> Json.toJson(AboutYourChild(name, dob))
    }.toMap)

  private lazy val dob16: LocalDate = LocalDate.now.minusYears(16).minusDays(1)
  private lazy val dob19: LocalDate = LocalDate.now.minusYears(19).minusDays(1)
  private lazy val dob: LocalDate = LocalDate.now.minusYears(1)
}
