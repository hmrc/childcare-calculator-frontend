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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import javax.inject.Inject

import play.api.Logger
import play.api.i18n.Messages
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location._
import uk.gov.hmrc.childcarecalculatorfrontend.models.SchemeEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{FreeHours, MaxFreeHours}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Location, _}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{FirstParagraphBuilder, UserAnswers}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ResultsService @Inject()(eligibilityService: EligibilityService,
                               freeHours: FreeHours,
                               maxFreeHours: MaxFreeHours,
                               firstParagraphBuilder: FirstParagraphBuilder) {
  def getResultsViewModel(answers: UserAnswers)(implicit req: play.api.mvc.Request[_], hc: HeaderCarrier, messages: Messages): Future[ResultsViewModel] = {
    val resultViewModel = ResultsViewModel(firstParagraph = firstParagraphBuilder.buildFirstParagraph(answers),
      location = answers.location, childAgedTwo = answers.childAgedTwo.getOrElse(false))
    val result: Future[SchemeResults] = eligibilityService.eligibility(answers)

    result.map(results => {
      val result = results.schemes.foldLeft(resultViewModel)((result, scheme) => getViewModelWithFreeHours(answers, setSchemeInViewModel(scheme, result, answers.taxOrUniversalCredits)))
      if (result.tfc.isDefined && result.taxCreditsOrUC.contains("tc")) {
        result.copy(showTFCWarning = true, tfcWarningMessage = messages("result.schemes.tfc.tc.warning"))
      } else {
        if (result.taxCreditsOrUC.contains("uc") && result.tfc.isDefined)
          result.copy(showTFCWarning = true, tfcWarningMessage = messages("result.schemes.tfc.uc.warning"))
        else
          result
      }
    })
  }

  private def setSchemeInViewModel(scheme: Scheme, resultViewModel: ResultsViewModel, taxCreditsOrUC: Option[String]) = {
    if (scheme.amount > 0) {
      scheme.name match {
        case TCELIGIBILITY => {
          if (taxCreditsOrUC == Some(universalCredits)) resultViewModel.copy(taxCreditsOrUC = taxCreditsOrUC) else resultViewModel.copy(tc = Some(scheme.amount), taxCreditsOrUC = taxCreditsOrUC)
        }
        case TFCELIGIBILITY => resultViewModel.copy(tfc = Some(scheme.amount))
        case ESCELIGIBILITY => resultViewModel.copy(esc = Some(scheme.amount))
      }
    }
    else {
      resultViewModel.copy(taxCreditsOrUC = taxCreditsOrUC)
    }
  }

  private def getViewModelWithFreeHours(answers: UserAnswers, resultViewModel: ResultsViewModel) = {
    val freeHoursEligibility = freeHours.eligibility(answers)
    val maxFreeHoursEligibility = maxFreeHours.eligibility(answers)
    val location: Option[Location.Value] = answers.location

    freeHoursEligibility match {
      case Eligible if maxFreeHoursEligibility == Eligible => resultViewModel.copy(freeHours = Some(eligibleMaxFreeHours))
      case Eligible => getFreeHoursForLocation(location, resultViewModel)
      case _ => resultViewModel
    }
  }

  private def getFreeHoursForLocation(optionLocation: Option[Location.Value], resultViewModel: ResultsViewModel) =
    optionLocation.fold(resultViewModel) {
      case ENGLAND => resultViewModel.copy(freeHours = Some(freeHoursForEngland))
      case SCOTLAND => resultViewModel.copy(freeHours = Some(freeHoursForScotland))
      case WALES => resultViewModel.copy(freeHours = Some(freeHoursForWales))
      case NORTHERN_IRELAND => resultViewModel.copy(freeHours = Some(freeHoursForNI))
    }
}
