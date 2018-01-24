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

package uk.gov.hmrc.childcarecalculatorfrontend.models.views

import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase

class ResultsViewModelSpec extends SpecBase {

  "ResultViewModel" must {
    "return correct number of eligible schemes" in {
      val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = None, esc = None)
      resultsView.noOfEligibleSchemes mustBe 2
    }

    "return number of eligible schemes 0 when there is no eligible scheme" in {
      val resultsView = ResultsViewModel()
      resultsView.noOfEligibleSchemes mustBe 0
    }

    "return number of eligible schemes as 3 when all schemes appilcable and UC selected on TC/UC page" in {
      val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(15) , esc = Some(120), taxCreditsOrUC = Some("uc"))
      resultsView.noOfEligibleSchemes mustBe 3
    }
  }
}