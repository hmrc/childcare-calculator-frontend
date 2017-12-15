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

import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, Location, Scheme}
import uk.gov.hmrc.childcarecalculatorfrontend.models.SchemeEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{FreeHours, MaxFreeHours}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ResultsService @Inject()(eligibilityService: EligibilityService,
                               freeHours: FreeHours,
                               maxFreeHours: MaxFreeHours) {
  def getResultsViewModel(answers: UserAnswers)(implicit req: play.api.mvc.Request[_], hc: HeaderCarrier): Future[ResultsViewModel] = {

    val numberOfChildren = if (answers.noOfChildren.getOrElse(0) == 0) "don't have" else "have"

    val section1 = s"You told the calculator that you $numberOfChildren children"

    val childcareCosts = answers.expectedChildcareCosts.fold(BigDecimal(0))(costs => costs.toSeq.foldLeft(BigDecimal(0))((costs,elements)=>{
      costs + elements._2
    }))

    val section2 = s", with childcare costs of £$childcareCosts."


    val resultViewModel = ResultsViewModel(Some(section1 + section2))

    val result = eligibilityService.eligibility(answers)

    result.map(results => {
      results.schemes.foldLeft(resultViewModel)((result, scheme) => getViewModelWithFreeHours(answers, setSchemeInViewModel(scheme,result)))
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
