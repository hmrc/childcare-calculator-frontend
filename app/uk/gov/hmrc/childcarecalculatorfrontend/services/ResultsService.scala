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

import uk.gov.hmrc.childcarecalculatorfrontend.models.Scheme
import uk.gov.hmrc.childcarecalculatorfrontend.models.SchemeEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ResultsService @Inject()(eligibilityService: EligibilityService, answers: UserAnswers) {
  def getResultsViewModel()(implicit req: play.api.mvc.Request[_], hc: HeaderCarrier): Future[ResultsViewModel] = {
    val result = eligibilityService.eligibility(answers)

    result.map(results => results.schemes.foldLeft(ResultsViewModel())((result, scheme) => setSchemeInViewModel(scheme,result)))
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
}
