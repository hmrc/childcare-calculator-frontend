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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import org.joda.time.LocalDate
import org.scalatest.OptionValues
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.{JsBoolean, JsNumber, JsValue, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.{SpecBase, SubNavigator}
import uk.gov.hmrc.http.cache.client.CacheMap

class ChildcareNavigatorSpec extends SpecBase with OptionValues with MockitoSugar {

  val navigator: SubNavigator = new ChildcareNavigator()

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
      Seq(0, 1).foreach {
        id =>
          s"redirect to `Child start education`, for id: $id" in {
            val answers: UserAnswers = userAnswers(
              aboutYourChildren(
                "Foo" -> dob16,
                "Spoon" -> dob,
                "Bar" -> dob16,
                "Baz" -> dob
              ),
              ChildApprovedEducationId.toString -> Json.obj(
                id.toString -> true
              )
            )
            val result = navigator.nextPage(ChildApprovedEducationId(id), NormalMode).value(answers)
            result mustEqual routes.ChildStartEducationController.onPageLoad(NormalMode, id)
          }
      }
    }

    "user answers `No`" when {

      "redirect to `Approved education or training` for the next child if this is not the last child" in {
        val answers: UserAnswers = userAnswers(
          aboutYourChildren(
            "Foo" -> dob16,
            "Spoon" -> dob,
            "Bar" -> dob16,
            "Baz" -> dob
          ),
          ChildApprovedEducationId.toString -> Json.obj(
            "0" -> false
          )
        )
        val result = navigator.nextPage(ChildApprovedEducationId(0), NormalMode).value(answers)
        result mustEqual routes.ChildApprovedEducationController.onPageLoad(NormalMode, 2)
      }

      "redirect to `Do your children get disability benefits` if this is the last child" in {
        val answers: UserAnswers = userAnswers(
          aboutYourChildren(
            "Foo" -> dob16,
            "Spoon" -> dob,
            "Bar" -> dob16,
            "Baz" -> dob
          ),
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
        aboutYourChildren(
          "Foo" -> dob16,
          "Spoon" -> dob,
          "Bar" -> dob16,
          "Baz" -> dob
        )
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
  }

  private def userAnswers(data: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", data.toMap))

  private def aboutYourChildren(children: (String, LocalDate)*): (String, JsValue) =
    AboutYourChildId.toString -> Json.toJson(children.zipWithIndex.map {
      case ((name, dob), i) =>
        i.toString -> Json.toJson(AboutYourChild(name, dob))
    }.toMap)

  private def dob16: LocalDate = LocalDate.now.minusYears(17)
  private def dob: LocalDate = LocalDate.now.minusYears(1)
}
