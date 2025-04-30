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

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{DateTimeUtils, SessionExpiredRouter, UserAnswers, Utils}

import javax.inject.Inject

class ChildcareNavigator @Inject() (utils: Utils) extends SubNavigator with DateTimeUtils {

  override protected lazy val routeMap: PartialFunction[Identifier, UserAnswers => Call] = {
    case NoOfChildrenId                => _ => routes.AboutYourChildController.onPageLoad(NormalMode, 0)
    case AboutYourChildId(id)          => aboutYourChildRoutes(id)
    case ChildrenDisabilityBenefitsId  => childrenDisabilityBenefitsRoutes
    case WhichChildrenDisabilityId     => whichChildrenDisabilityRoutes
    case WhichDisabilityBenefitsId(id) => whichDisabilityBenefitsRoutes(id)
    case RegisteredBlindId             => registeredBlindRoutes
    case WhichChildrenBlindId          => whichChildrenBlindRoute
    case WhoHasChildcareCostsId        => whoHasChildcareCostsRoutes
    case ChildcarePayFrequencyId(id)   => _ => routes.ExpectedChildcareCostsController.onPageLoad(NormalMode, id)
    case ExpectedChildcareCostsId(id)  => expectedChildcareCostsRoutes(id)
  }

  private def aboutYourChildRoutes(id: Int)(answers: UserAnswers): Call = {
    def isLastChild(id: Int, noOfChildren: Int) = id == (noOfChildren - 1)

    for {
      noOfChildren <- answers.noOfChildren
    } yield
      if (isLastChild(id, noOfChildren)) {
        routes.ChildrenDisabilityBenefitsController.onPageLoad(NormalMode)
      } else {
        routes.AboutYourChildController.onPageLoad(NormalMode, id + 1)
      }
  }.getOrElse(SessionExpiredRouter.route(getClass.getName, "aboutYourChildRoutes", Some(answers)))

  private def childrenDisabilityBenefitsRoutes(answers: UserAnswers): Call = {
    for {
      noOfChildren            <- answers.noOfChildren
      childDisabilityBenefits <- answers.childrenDisabilityBenefits
    } yield
      if (childDisabilityBenefits) {
        if (noOfChildren > 1) {
          routes.WhichChildrenDisabilityController.onPageLoad(NormalMode)
        } else {
          routes.WhichDisabilityBenefitsController.onPageLoad(NormalMode, 0)
        }
      } else {
        routes.RegisteredBlindController.onPageLoad(NormalMode)
      }
  }.getOrElse(SessionExpiredRouter.route(getClass.getName, "childrenDisabilityBenefitsRoutes", Some(answers)))

  private def whichChildrenDisabilityRoutes(answers: UserAnswers): Call =
    answers.whichChildrenDisability
      .map(children => routes.WhichDisabilityBenefitsController.onPageLoad(NormalMode, children.head))
      .getOrElse(SessionExpiredRouter.route(getClass.getName, "whichChildrenDisabilityRoutes", Some(answers)))

  private def whichDisabilityBenefitsRoutes(id: Int)(answers: UserAnswers): Call =
    answers.childrenWithDisabilityBenefits
      .map { whichChildrenDisability =>
        def next: Option[Int] = {
          val children: Seq[Int] = whichChildrenDisability.toSeq
          children.lift(children.indexOf(id) + 1)
        }

        next.map(nextId => routes.WhichDisabilityBenefitsController.onPageLoad(NormalMode, nextId)).getOrElse {
          routes.RegisteredBlindController.onPageLoad(NormalMode)
        }
      }
      .getOrElse(SessionExpiredRouter.route(getClass.getName, "whichDisabilityBenefitsRoutes", Some(answers)))

  private def registeredBlindRoutes(answers: UserAnswers): Call = {
    for {
      totalNumberOfChildren     <- answers.noOfChildren
      isAnyChildRegisteredBlind <- answers.registeredBlind
    } yield
      if (totalNumberOfChildren > 1) {
        handleMultipleChildrenRoute(answers, totalNumberOfChildren, isAnyChildRegisteredBlind)
      } else {
        handleSingleChildRoute(answers)
      }
  }.flatten.getOrElse(SessionExpiredRouter.route(getClass.getName, "registeredBlindRoutes", Some(answers)))

  private def handleSingleChildRoute(answers: UserAnswers): Option[Call] = {
    for {
      children <- answers.childrenWithCosts
    } yield
      if (answers.numberOfChildrenOver16 > 0) {
        Some(routeToIncomeInfoPage(answers))
      } else {
        destinedUrlForSingleChildAged16(answers)
      }
  }.flatten

  private def whichChildrenBlindRoute(answers: UserAnswers): Call =
    handleRoutesIfChildrenOver16(answers, answers.noOfChildren.getOrElse(0))
      .getOrElse(routes.SessionExpiredController.onPageLoad)

  private def handleMultipleChildrenRoute(
      answers: UserAnswers,
      totalNumberOfChildren: Int,
      isAnyChildRegisteredBlind: Boolean
  ) =
    if (isAnyChildRegisteredBlind) {
      Some(routes.WhichChildrenBlindController.onPageLoad(NormalMode))
    } else {

      handleRoutesIfChildrenOver16(answers, totalNumberOfChildren)
    }

  private def handleRoutesIfChildrenOver16(answers: UserAnswers, totalNumberOfChildren: Int): Option[Call] =
    if (answers.numberOfChildrenOver16 == totalNumberOfChildren) {
      Some(routeToIncomeInfoPage(answers))
    } else {
      if (answers.childrenBelow16AndExactly16Disabled.size.equals(1)) {
        destinedUrlForSingleChildAged16(answers)
      } else {
        destinedUrlForMultipleChildAged16(answers)
      }
    }

  private def destinedUrlForSingleChildAged16(answers: UserAnswers): Option[Call] =
    if (answers.childrenBelow16AndExactly16Disabled.size.equals(1)) {
      Some(
        routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, answers.childrenBelow16AndExactly16Disabled.head)
      )
    } else {
      Some(routeToIncomeInfoPage(answers))
    }

  private def destinedUrlForMultipleChildAged16(answers: UserAnswers): Option[Call] =
    if (answers.childrenBelow16AndExactly16Disabled.size > 1) {
      Some(routes.WhoHasChildcareCostsController.onPageLoad(NormalMode))
    } else {
      Some(routeToIncomeInfoPage(answers))
    }

  private def routeToIncomeInfoPage(answers: UserAnswers) =
    utils.getCall(answers.doYouLiveWithPartner) {
      case false => routes.YourIncomeInfoController.onPageLoad()
      case true  => routes.PartnerIncomeInfoController.onPageLoad()
    }

  private def whoHasChildcareCostsRoutes(answers: UserAnswers): Call = {
    for {
      children   <- answers.childrenWithCosts
      childIndex <- children.toSeq.headOption
    } yield routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, childIndex)
  }.getOrElse(SessionExpiredRouter.route(getClass.getName, "whoHasChildcareCostsRoutes", Some(answers)))

  private def expectedChildcareCostsRoutes(childId: Int)(answers: UserAnswers): Call = {
    def nextChildIdOpt: Option[Int] = {
      val children: Seq[Int] = answers.childrenWithCosts.getOrElse(Set.empty).toSeq
      children.lift(children.indexOf(childId) + 1)
    }

    nextChildIdOpt
      .map(nextChildId => routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, nextChildId))
      .getOrElse {
        redirectToTheNextPage(answers)
      }
  }

  private def redirectToTheNextPage(answers: UserAnswers): Call =
    (answers.hasVouchers, answers.doYouLiveWithPartner) match {
      case (true, Some(true))  => routes.PartnerIncomeInfoController.onPageLoad()
      case (true, Some(false)) => routes.YourIncomeInfoController.onPageLoad()
      case (false, _)          => routes.ResultController.onPageLoad()

      case _ => SessionExpiredRouter.route(getClass.getName, "expectedChildcareCostsRoutes", Some(answers))
    }

}
