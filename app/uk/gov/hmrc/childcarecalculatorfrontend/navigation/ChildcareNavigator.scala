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

import javax.inject.Inject

import org.joda.time.LocalDate
import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}

class ChildcareNavigator @Inject() (utils: Utils) extends SubNavigator {

  override protected lazy val routeMap: PartialFunction[Identifier, UserAnswers => Call] = {
    case NoOfChildrenId => _ => routes.AboutYourChildController.onPageLoad(NormalMode, 0)
    case AboutYourChildId(id) => aboutYourChildRoutes(id)
    case ChildApprovedEducationId(id) => childApprovedEducationRoutes(id)
    case ChildStartEducationId(id) => childEducationStartRoutes(id)
    case ChildrenDisabilityBenefitsId => childrenDisabilityBenefitsRoutes
    case WhichChildrenDisabilityId => whichChildrenDisabilityRoutes
    case WhichDisabilityBenefitsId(id) => whichDisabilityBenefitsRoutes(id)
    case RegisteredBlindId => registeredBlindRoutes
    case WhichChildrenBlindId => whichChildrenBlindRoute
    case WhoHasChildcareCostsId => whoHasChildcareCostsRoutes
    case ChildcarePayFrequencyId(id) => _ => routes.ExpectedChildcareCostsController.onPageLoad(NormalMode, id)
    case ExpectedChildcareCostsId(id) => expectedChildcareCostsRoutes(id)
  }

  private def aboutYourChildRoutes(id: Int)(answers: UserAnswers): Call = {

    def isLast(id: Int, noOfChildren: Int): Boolean =
      id == (noOfChildren - 1)

    for {
      noOfChildren   <- answers.noOfChildren
      aboutYourChild <- answers.aboutYourChild
    } yield if (isLast(id, noOfChildren)) {

      val childrenOver16 = answers.childrenOver16
      if (childrenOver16.get.size > 0) {
        routes.ChildApprovedEducationController.onPageLoad(NormalMode, childrenOver16.get.head._1)
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

  private def whichChildrenDisabilityRoutes(answers: UserAnswers): Call = {
    answers.whichChildrenDisability.map {
      children =>
        routes.WhichDisabilityBenefitsController.onPageLoad(NormalMode, children.head.toInt)
    }.getOrElse(routes.SessionExpiredController.onPageLoad())
  }

  private def whichDisabilityBenefitsRoutes(id: Int)(answers: UserAnswers): Call = {
    answers.childrenWithDisabilityBenefits.map {
      whichChildrenDisability =>

        def next: Option[Int] = {
          val children: Seq[Int] = whichChildrenDisability.toSeq
          children.lift(children.indexOf(id) + 1)
        }

        next.map {
          nextId =>
            routes.WhichDisabilityBenefitsController.onPageLoad(NormalMode, nextId)
        }.getOrElse {
          routes.RegisteredBlindController.onPageLoad(NormalMode)
        }
    }.getOrElse(routes.SessionExpiredController.onPageLoad())
  }

  private def registeredBlindRoutes(answers: UserAnswers): Call = {

    for {
      totalNumberOfChildren    <- answers.noOfChildren
      isAnyChildRegisteredBlind <- answers.registeredBlind

    } yield if (totalNumberOfChildren > 1) {

      handleMultipleChildrenRoute(answers, totalNumberOfChildren, isAnyChildRegisteredBlind)
    } else {

      handleSingleChildRoute(answers)
    }
  }.flatten.getOrElse(routes.SessionExpiredController.onPageLoad())


  private def handleSingleChildRoute(answers: UserAnswers): Option[Call] = {

    for {
      children <- answers.childrenWithCosts
      childIndex <- children.toSeq.headOption

    } yield {
      if (answers.numberOfChildrenOver16 > 0) {
        Some(routeToIncomeInfoPage(answers))
      }
      else {
        destinedUrlForSingleChildAged16(answers)
      }
    }
  }.flatten


  private def whichChildrenBlindRoute(answers:UserAnswers):Call=
    handleRoutesIfChildrenOver16(answers, answers.noOfChildren.getOrElse(0)).getOrElse(routes.SessionExpiredController.onPageLoad())

  private def handleMultipleChildrenRoute(answers: UserAnswers,
                                          totalNumberOfChildren: Int,
                                          isAnyChildRegisteredBlind: Boolean) = {
    if (isAnyChildRegisteredBlind) {
      Some(routes.WhichChildrenBlindController.onPageLoad(NormalMode))
    } else {

      handleRoutesIfChildrenOver16(answers, totalNumberOfChildren)
    }
  }

  private def handleRoutesIfChildrenOver16(answers: UserAnswers, totalNumberOfChildren: Int): Option[Call] = {
    if (answers.numberOfChildrenOver16 == totalNumberOfChildren) {
      Some(routeToIncomeInfoPage(answers))
    } else {
      if(answers.test3.size.equals(1) || answers.aboutYourChild.getOrElse(Map()).filter(_._2.dob.isAfter(LocalDate.now.minusYears(16))).keys.toSeq.size==1)
          {destinedUrlForSingleChildAged16(answers)}
          else { destinedUrlForMultipleChildAged16(answers)}
    }
  }

  private def destinedUrlForSingleChildAged16(answers: UserAnswers): Option[Call] = {
    if (answers.test3.size.equals(1) && answers.test) {
      Some(routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, answers.test3.head))
    } else if (answers.aboutYourChild.getOrElse(Map()).filter(_._2.dob.isAfter(LocalDate.now.minusYears(16))).keys.toSeq.size == 1) {
      Some(routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, answers.test3.head))
    } else {
      Some(routeToIncomeInfoPage(answers))
    }
  }




  private def  destinedUrlForMultipleChildAged16(answers: UserAnswers): Option[Call] = {
    if (answers.test3.size > 1 && answers.test2) {
      Some(routes.WhoHasChildcareCostsController.onPageLoad(NormalMode))
    } else if (answers.aboutYourChild.getOrElse(Map()).filter(_._2.dob.isAfter(LocalDate.now.minusYears(16))).keys.toSeq.size > 1) {
      Some(routes.WhoHasChildcareCostsController.onPageLoad(NormalMode))
    } else {
      Some(routeToIncomeInfoPage(answers))
    }
  }

  private def routeToIncomeInfoPage(answers: UserAnswers) =
   utils.getCall(answers.doYouLiveWithPartner) {
      case false => routes.YourIncomeInfoController.onPageLoad()
      case true => routes.PartnerIncomeInfoController.onPageLoad()
    }


  private def whoHasChildcareCostsRoutes(answers: UserAnswers): Call = {
    for {
      children   <- answers.childrenWithCosts
      childIndex <- children.toSeq.headOption
    } yield {
      routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, childIndex)
    }
  }.getOrElse(routes.SessionExpiredController.onPageLoad())

  private def expectedChildcareCostsRoutes(id: Int)(answers: UserAnswers): Call = {
    answers.doYouLiveWithPartner.map(hasPartner => answers.childrenWithCosts match {
      case Some(childrenWithCosts) => checkNextChildWithCosts(id, hasPartner, childrenWithCosts)
      case _ => routeBasedIfPartnerOrNot(hasPartner)
    })
  }.getOrElse(routes.SessionExpiredController.onPageLoad())

  private def checkNextChildWithCosts(id: Int, hasPartner: Boolean, childrenWithCosts: Set[Int]) = {
    def next: Option[Int] = {
      val children: Seq[Int] = childrenWithCosts.toSeq
      children.lift(children.indexOf(id) + 1)
    }
    next.map {
      nextId =>
        routes.ChildcarePayFrequencyController.onPageLoad(NormalMode, nextId)
    }.getOrElse {
      routeBasedIfPartnerOrNot(hasPartner)
    }
  }

  private def routeBasedIfPartnerOrNot(hasPartner: Boolean) = {
    if (hasPartner) {
      routes.PartnerIncomeInfoController.onPageLoad()
    } else {
      routes.YourIncomeInfoController.onPageLoad()
    }
  }
}
