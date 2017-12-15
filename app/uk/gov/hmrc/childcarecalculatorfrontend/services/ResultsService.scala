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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import javax.inject.Inject

import uk.gov.hmrc.childcarecalculatorfrontend.models.Location._
import uk.gov.hmrc.childcarecalculatorfrontend.models.SchemeEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{FreeHours, MaxFreeHours}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Location, YouPartnerBothEnum, _}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.HeaderCarrier
import play.api.i18n.Messages

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ResultsService @Inject()(eligibilityService: EligibilityService,
                               freeHours: FreeHours,
                               maxFreeHours: MaxFreeHours) {
  def getResultsViewModel(answers: UserAnswers)(implicit req: play.api.mvc.Request[_], hc: HeaderCarrier, messages: Messages): Future[ResultsViewModel] = {
    val resultViewModel = ResultsViewModel(buildFirstParagraph(answers))
    val result = eligibilityService.eligibility(answers)

    result.map(results => {
      results.schemes.foldLeft(resultViewModel)((result, scheme) => getViewModelWithFreeHours(answers, setSchemeInViewModel(scheme,result)))
    })
  }

  private def buildFirstParagraph(answers: UserAnswers)(implicit messages: Messages) = {
    val doYouHaveChildren = buildFirstSection(answers, _: String)
    val yearlyChildcareCosts = buildSecondSection(answers, _: String)
    val whoAreYouLivingWith = buildThidSection(answers, _: String)
    val areYouInPaidWork = buildFourthSection(answers, _: String)
    val firstParagraph = (doYouHaveChildren andThen yearlyChildcareCosts andThen whoAreYouLivingWith andThen areYouInPaidWork) ("")
    firstParagraph
  }

  private def buildFirstSection(answers: UserAnswers, paragraph: String)(implicit messages: Messages) = {
    val numberOfChildren = if (answers.noOfChildren.getOrElse(0) == 0) Messages("results.firstParagraph.dontHave") else Messages("results.firstParagraph.have")
    s"$paragraph${Messages("results.firstParagraph.haveChildren", numberOfChildren)}"
  }

  private def buildSecondSection(answers: UserAnswers, paragraph: String)(implicit messages: Messages) = {
    val childcareCosts = CalculateChildcareCosts(answers)
    val section2 = if (childcareCosts == 0) "." else s", ${Messages("results.firstParagraph.yearlyChildcareCosts")}$childcareCosts."
    s"$paragraph$section2"
  }

  private def buildThidSection(answers: UserAnswers, paragraph: String)(implicit messages: Messages) = {
    val livesOnOwnOrWithPartner: Option[String] = answers.doYouLiveWithPartner.map(livesWithPartner => if (livesWithPartner) Messages("results.firstParagraph.withYourPartner") else Messages("results.firstParagraph.onYourOwn"))
    val section3 = livesOnOwnOrWithPartner.fold("")(livesOnOwnOrWithPartner => s" ${Messages("results.firstParagraph.youLiveAnd", livesOnOwnOrWithPartner)} ")
    s"$paragraph$section3"
  }

  private def buildFourthSection(answers: UserAnswers, paragraph: String)(implicit messages: Messages) = {
    val section4 = answers.whoIsInPaidEmployment.fold("")(whoInPaidEmployment => {
      val You = YouPartnerBothEnum.YOU.toString
      val Partner = YouPartnerBothEnum.PARTNER.toString
      val Both = YouPartnerBothEnum.BOTH.toString

      val currentlyInPaidWork = Messages("results.firstParagraph.inPaidWork")

      whoInPaidEmployment match {
        case You => {
          val hoursAWeek = answers.parentWorkHours.fold("")(hours => s" ${Messages("results.firstParagraph.youWorkXHoursAweek", hours)}")
          Messages("results.firstParagraph.onlyYouAre", currentlyInPaidWork, hoursAWeek)
        }
        case Partner => {
          val hoursAweek = answers.partnerWorkHours.fold("")(hours => s" ${Messages("results.firstParagraph.yourPartnerWorksXHoursAweek", hours)}")
          Messages("results.firstParagraph.onlyYourPartnerIs", currentlyInPaidWork, hoursAweek)
        }
        case Both => {
          val yourHours = answers.parentWorkHours.fold(BigDecimal(0))(c => c)
          val partnerHours = answers.partnerWorkHours.fold(BigDecimal(0))(c => c)
          val hoursAweek = s" ${Messages("results.firstParagraph.youAndYourPartnerWorkXhoursAweek", yourHours, partnerHours)}"
          Messages("results.firstParagraph.bothYouAndYourPartnerAre", currentlyInPaidWork, hoursAweek)
        }
      }
    })

    s"$paragraph$section4"
  }

  private def CalculateChildcareCosts(answers: UserAnswers) = {
    answers.expectedChildcareCosts.fold(BigDecimal(0))(costs =>
      costs.toSeq.foldLeft(BigDecimal(0))((costs, elements) => AddCostsToYearlyAggregation(answers, costs, elements)))
  }

  private def AddCostsToYearlyAggregation(answers: UserAnswers, costs: BigDecimal, elements: (Int,BigDecimal)) = {
    answers.childcarePayFrequency.fold(costs)(frequencies => {
      val frequency = frequencies.get(elements._1)
      frequency.getOrElse(costs) match {
        case ChildcarePayFrequency.WEEKLY => {
          costs + (elements._2 * 52)
        }
        case ChildcarePayFrequency.MONTHLY => {
          costs + (elements._2 * 12)
        }
        case _ => costs
      }
    })

  }

  private def setSchemeInViewModel(scheme: Scheme, resultViewModel: ResultsViewModel) = {
    if (scheme.amount > 0) {
      scheme.name match {
        case TCELIGIBILITY => resultViewModel.copy(tc = Some(scheme.amount))
        case TFCELIGIBILITY => resultViewModel.copy(tfc = Some(scheme.amount))
        case ESCELIGIBILITY =>resultViewModel.copy(esc = Some(scheme.amount))
      }
    }
    else {
      resultViewModel
    }
  }

  private def getViewModelWithFreeHours(answers: UserAnswers, resultViewModel: ResultsViewModel) = {
    val freeHoursEligibility = freeHours.eligibility(answers)
    val maxFreeHoursEligibility = maxFreeHours.eligibility(answers)
    val location: Option[Location.Value] = answers.location

    freeHoursEligibility match {
      case Eligible if maxFreeHoursEligibility == Eligible => resultViewModel.copy(freeHours = Some(eligibleMaxFreeHours))
      case Eligible =>  getFreeHoursForLocation(location, resultViewModel)
      case _ => resultViewModel
    }
  }

  private def getFreeHoursForLocation(optionLocation: Option[Location.Value], resultViewModel: ResultsViewModel)  =
    optionLocation.fold(resultViewModel){
          case ENGLAND => resultViewModel.copy(freeHours = Some(freeHoursForEngland))
          case SCOTLAND => resultViewModel.copy(freeHours = Some(freeHoursForScotland))
          case WALES => resultViewModel.copy(freeHours = Some(freeHoursForWales))
          case NORTHERN_IRELAND => resultViewModel.copy(freeHours = Some(freeHoursForNI))
    }
}
