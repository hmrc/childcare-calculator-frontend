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

import javax.inject.Inject

import org.joda.time.LocalDate
import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class ChildcareNavigator @Inject() () extends SubNavigator {

  override protected lazy val routeMap: PartialFunction[Identifier, UserAnswers => Call] = {
    case NoOfChildrenId => _ => routes.AboutYourChildController.onPageLoad(NormalMode, 0)
    case AboutYourChildId(id) => aboutYourChildRoutes(id)
    case ChildApprovedEducationId(id) => childApprovedEducationRoutes(id)
    case ChildStartEducationId(id) => childEducationStartRoutes(id)
    case ChildrenDisabilityBenefitsId => childrenDisabilityBenefitsRoutes
  }

  private def aboutYourChildRoutes(id: Int)(answers: UserAnswers): Call = {

    def isLast(id: Int, noOfChildren: Int): Boolean =
      id == (noOfChildren - 1)

    for {
      noOfChildren   <- answers.noOfChildren
      aboutYourChild <- answers.aboutYourChild
    } yield if (isLast(id, noOfChildren)) {

      val firstOver16 = aboutYourChild.values.toSeq.indexWhere {
        // TODO helper with more specific logic
        model =>
          model.dob.isBefore(LocalDate.now.minusYears(16))
      }

      if (firstOver16 > -1) {
        routes.ChildApprovedEducationController.onPageLoad(NormalMode, firstOver16)
      } else {
        routes.ChildrenDisabilityBenefitsController.onPageLoad(NormalMode)
      }
    } else {
      routes.AboutYourChildController.onPageLoad(NormalMode, id + 1)
    }
  }.getOrElse(routes.SessionExpiredController.onPageLoad())

  private def childApprovedEducationRoutes(id: Int)(answers: UserAnswers): Call = {

    def next(i: Int, childrenOver16: Map[Int, AboutYourChild]): Option[Int] = {
      val ints: Seq[Int] = childrenOver16.keys.toSeq
      ints.lift(ints.indexOf(i) + 1)
    }

    def over19(dob: LocalDate): Boolean =
      dob.isBefore(LocalDate.now.minusYears(19))

    for {
      childrenOver16    <- answers.childrenOver16
      approvedEducation <- answers.childApprovedEducation(id)
      dob               <- childrenOver16.get(id).map(_.dob)
    } yield if (approvedEducation && over19(dob)) {
      routes.ChildStartEducationController.onPageLoad(NormalMode, id)
    } else {
      next(id, childrenOver16).map {
        nextId =>
          routes.ChildApprovedEducationController.onPageLoad(NormalMode, nextId)
      }.getOrElse(routes.ChildrenDisabilityBenefitsController.onPageLoad(NormalMode))
    }
  }.getOrElse(routes.SessionExpiredController.onPageLoad())

  private def childEducationStartRoutes(id: Int)(answers: UserAnswers): Call = {

    def next(i: Int, childrenOver16: Map[Int, AboutYourChild]): Option[Int] = {
      val ints: Seq[Int] = childrenOver16.keys.toSeq
      ints.lift(ints.indexOf(i) + 1)
    }

    answers.childrenOver16.map {
      childrenOver16 =>
        next(id, childrenOver16).map {
          nextId =>
            routes.ChildApprovedEducationController.onPageLoad(NormalMode, nextId)
        }.getOrElse(routes.ChildrenDisabilityBenefitsController.onPageLoad(NormalMode))
    }.getOrElse(routes.SessionExpiredController.onPageLoad())
  }

  private def childrenDisabilityBenefitsRoutes(answers: UserAnswers): Call = {
    for {
      noOfChildren            <- answers.noOfChildren
      childDisabilityBenefits <- answers.childrenDisabilityBenefits
    } yield if (childDisabilityBenefits) {
      if (noOfChildren > 1) {
        routes.WhichChildrenDisabilityController.onPageLoad(NormalMode)
      } else {
        routes.WhichDisabilityBenefitsController.onPageLoad(NormalMode, 0)
      }
    } else {
      routes.RegisteredBlindController.onPageLoad(NormalMode)
    }
  }.getOrElse(routes.SessionExpiredController.onPageLoad())
}
