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

import play.api.i18n.Messages
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{ChildcarePayFrequency, YouPartnerBothNeither}

import javax.inject.{Inject, Singleton}

@Singleton
class FirstParagraphBuilder @Inject() (utils: Utils) {

  def buildFirstParagraph(answers: UserAnswers)(using messages: Messages): List[String] = {
    val doYouHaveChildren    = buildFirstSection(answers)
    val yearlyChildcareCosts = buildSecondSection(answers)
    val whoAreYouLivingWith  = buildThirdSection(answers)
    val areYouInPaidWork     = buildFourthSection(answers)
    val firstParagraph = List(
      doYouHaveChildren,
      yearlyChildcareCosts,
      whoAreYouLivingWith,
      areYouInPaidWork
    ).filter(_.nonEmpty)
    firstParagraph
  }

  private def buildFirstSection(answers: UserAnswers)(using messages: Messages) =
    answers.noOfChildren match {
      case Some(numberOfChildren) =>
        val childOrChildren =
          if (numberOfChildren == 1) Messages("results.firstParagraph.aChild")
          else Messages("results.firstParagraph.children")
        val numberOfChildrenMessage =
          if (numberOfChildren == 0) {
            Messages("results.firstParagraph.dontHave")
          } else {
            if (numberOfChildren > 1) s"${Messages("results.firstParagraph.have")} $numberOfChildren"
            else Messages("results.firstParagraph.have")
          }
        s"${Messages("results.firstParagraph.youToldTheCalculator", numberOfChildrenMessage, childOrChildren)}"
      case _ => ""
    }

  private def buildSecondSection(answers: UserAnswers)(using messages: Messages) = {
    val section2 = answers.noOfChildren match {
      case Some(_) =>
        val childcareCosts = CalculateChildcareCosts(answers)
        s"${Messages("results.firstParagraph.yearlyChildcareCosts")}${utils.formatBigDecimal(childcareCosts)}"
      case _ => ""
    }

    section2
  }

  private def buildThirdSection(answers: UserAnswers)(using messages: Messages) = {
    val livesOnOwnOrWithPartner: Option[String] = answers.doYouLiveWithPartner.map(livesWithPartner =>
      if (livesWithPartner) Messages("results.firstParagraph.withYourPartner")
      else Messages("results.firstParagraph.onYourOwn")
    )
    val section3 = livesOnOwnOrWithPartner.fold("")(livesOnOwnOrWithPartner =>
      Messages("results.firstParagraph.youLiveAnd", livesOnOwnOrWithPartner)
    )
    s"$section3"
  }

  private def buildFourthSection(answers: UserAnswers)(using messages: Messages) = {
    val section4 = answers.doYouLiveWithPartner.fold("")(livesWithPartner =>
      if (livesWithPartner) {
        checkWhoIsInPaidEmployment(answers)
      } else {
        checkIfInPaidWork(answers)
      }
    )

    s"$section4"
  }

  private def checkIfInPaidWork(answers: UserAnswers)(using messages: Messages) =
    if (answers.areYouInPaidWork.getOrElse(false)) {
      Messages("results.firstParagraph.youInPaidWork")
    } else {
      ""
    }

  private def checkWhoIsInPaidEmployment(answers: UserAnswers)(using messages: Messages) =
    answers.whoIsInPaidEmployment.fold("") {
      case YouPartnerBothNeither.You     => Messages("results.firstParagraph.onlyYouInPaidWork")
      case YouPartnerBothNeither.Partner => Messages("results.firstParagraph.onlyPartnerInPaidWork")
      case YouPartnerBothNeither.Both    => Messages("results.firstParagraph.youAndPartnerInPaidWork")
      case _                             => Messages("results.firstParagraph.neitherInPaidWork")
    }

  private def CalculateChildcareCosts(answers: UserAnswers) =
    answers.expectedChildcareCosts.fold(BigDecimal(0))(costs =>
      costs.toSeq.foldLeft(BigDecimal(0))((costs, elements) => AddCostsToYearlyAggregation(answers, costs, elements))
    )

  private def AddCostsToYearlyAggregation(answers: UserAnswers, costs: BigDecimal, elements: (Int, BigDecimal)) =
    answers.childcarePayFrequency.fold(costs) { frequencies =>
      val frequency = frequencies.get(elements._1)
      frequency.getOrElse(costs) match {
        case ChildcarePayFrequency.Weekly =>
          costs + (elements._2 * 52)
        case ChildcarePayFrequency.Monthly =>
          costs + (elements._2 * 12)
        case _ => costs
      }
    }

}
