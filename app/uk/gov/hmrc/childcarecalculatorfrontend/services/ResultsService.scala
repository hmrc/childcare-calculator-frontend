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

import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligible, NotEligible, Location, Scheme, SchemeEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes._
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ResultsService @Inject()(eligibilityService: EligibilityService,
                               answers: UserAnswers,
                               freeHours: FreeHours,
                               maxFreeHours: MaxFreeHours) {
  def getResultsViewModel()(implicit req: play.api.mvc.Request[_], hc: HeaderCarrier): Future[ResultsViewModel] = {
    val result = eligibilityService.eligibility(answers)

    result.map(results => {
      results.schemes.foldLeft(ResultsViewModel())((result, scheme) => getFreeHours(answers, setSchemeInViewModel(scheme,result)))
    })
  }

  private def setSchemeInViewModel(scheme: Scheme, resultViewModel: ResultsViewModel) = {
    if (scheme.amount > 0) {
      scheme.name match {
        case SchemeEnum.TCELIGIBILITY => resultViewModel.copy(tc = Some(scheme.amount))
        case SchemeEnum.TFCELIGIBILITY => resultViewModel.copy(tfc = Some(scheme.amount))
        case SchemeEnum.ESCELIGIBILITY =>resultViewModel.copy(esc = Some(scheme.amount))
      }
    }
    else {
      resultViewModel
    }
  }

  private def getFreeHours(answers: UserAnswers, resultViewModel: ResultsViewModel) = {
    val freeHoursEligibililty = freeHours.eligibility(answers)
    val maxFreeHoursEligibility = maxFreeHours.eligibility(answers)
    val location: Option[Location.Value] = answers.location

    freeHoursEligibililty match {
      case Eligible if maxFreeHoursEligibility == Eligible => resultViewModel.copy(freeHours = Some(30))
      case Eligible => {
        if(location.contains(Location.ENGLAND)) {
          resultViewModel.copy(freeHours = Some(15))
        } else if(location.contains(Location.SCOTLAND)) {
          resultViewModel.copy(freeHours = Some(16))
        } else if(location.contains(Location.WALES)) {
          resultViewModel.copy(freeHours = Some(10))
        } else {
          resultViewModel.copy(freeHours = Some(12.5))
        }
      }
      case NotEligible => resultViewModel.copy(freeHours = Some(0))
      case _ => resultViewModel

    }
  }

}
