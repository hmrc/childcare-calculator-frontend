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
  }

  "isEligibleForAllButVouchers" must {
   "return true" when {
     "user is eligible for all the schemes but vouchers" in {
       val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = None)
       resultsView.isEligibleForAllButVouchers mustBe true
     }
   }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300))
        resultsView.isEligibleForAllButVouchers mustBe false
      }
    }
  }

  "isEligibleForAllButTc" must {
    "return true" when {
      "user is eligible for all the schemes but tax credits" in {
        val resultsView = ResultsViewModel(tc = None, tfc = Some(100), freeHours = Some(200), esc = Some(200))
        resultsView.isEligibleForAllButTc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300))
        resultsView.isEligibleForAllButTc mustBe false
      }
    }
  }

  "isEligibleForAllButFreeHours" must {
    "return true" when {
      "user is eligible for all the schemes but free hours" in {
        val resultsView = ResultsViewModel(freeHours = None, tfc = Some(100), tc = Some(200), esc = Some(200))
        resultsView.isEligibleForAllButFreeHours mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300))
        resultsView.isEligibleForAllButTc mustBe false
      }
    }
  }

  "isEligibleForAllButTfc" must {
    "return true" when {
      "user is eligible for all the schemes but TFC" in {
        val resultsView = ResultsViewModel(tc = Some(300), tfc = None, freeHours = Some(200), esc = Some(200))
        resultsView.isEligibleForAllButTfc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300))
        resultsView.isEligibleForAllButTfc mustBe false
      }
    }
  }

  "isEligibleForFreeHoursAndTFC" must {
    "return true" when {
      "user is eligible for Free hours and TFC" in {
        val resultsView = ResultsViewModel(esc = None, tc = None, tfc = Some(500), freeHours = Some(200))
        resultsView.isEligibleOnlyForFreeHoursAndTfc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300))
        resultsView.isEligibleOnlyForFreeHoursAndTfc mustBe false
      }
    }
  }
//
  "isEligibleOnlyForFreeHoursAndTC" must {
    "return true" when {
      "user is eligible for Free hours and TC" in {
        val resultsView = ResultsViewModel(esc = None, tfc = None, tc = Some(500), freeHours = Some(200))
        resultsView.isEligibleOnlyForFreeHoursAndTc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300))
        resultsView.isEligibleOnlyForFreeHoursAndTc mustBe false
      }
    }
  }

  "isEligibleForFreeHoursAndESC" must {
    "return true" when {
      "user is eligible for Free hours and ESC" in {
        val resultsView = ResultsViewModel(tc = None, tfc = None, esc = Some(500), freeHours = Some(200))
        resultsView.isEligibleOnlyForFreeHoursAndEsc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tc = Some(200), tfc = Some(100), freeHours = Some(200), esc = Some(300))
        resultsView.isEligibleOnlyForFreeHoursAndEsc mustBe false
      }
    }
  }

  "isEligibleForTCAndTFC" must {
    "return true" when {
      "user is eligible for TC and TFC" in {
        val resultsView = ResultsViewModel(tc = Some(500), tfc = Some(200))
        resultsView.isEligibleOnlyForTCAndTfc mustBe true
      }
    }

    "return false" when {
      "user is not eligible for TC and TFC" in {
        val resultsView = ResultsViewModel(tc = None, tfc = None, freeHours = Some(200), esc = Some(300))
        resultsView.isEligibleOnlyForTCAndTfc mustBe false
      }
    }
  }

  "isEligibleOnlyForTCAndESC" must {
    "return true" when {
      "user is only eligible for TC and ESC" in {
        val resultsView = ResultsViewModel(tc = Some(500), esc = Some(300), tfc = None, freeHours = None)
        resultsView.isEligibleOnlyForTCAndEsc mustBe true
      }
    }

    "return false" when {
      "user is not eligible for TC and ESC" in {
        val resultsView = ResultsViewModel(tc = None, esc = None, tfc = Some(500), freeHours = Some(600))
        resultsView.isEligibleOnlyForTCAndEsc mustBe false
      }
    }
  }

  "isEligibleForTFCAndESC" must {
    "return true" when {
      "user is eligible for TFC and ESC" in {
        val resultsView = ResultsViewModel(esc = Some(500), tfc = Some(200))
        resultsView.isEligibleOnlyForTfcAndEsc mustBe true
      }
    }

    "return false" when {
      "user is eligible for all the schemes" in {
        val resultsView = ResultsViewModel(tfc = None, esc = None)
        resultsView.isEligibleOnlyForTfcAndEsc mustBe false
      }
    }
  }

}