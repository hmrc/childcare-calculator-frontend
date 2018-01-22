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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import java.text.DecimalFormat

import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils.Utils
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.resultEligible

class ResultEligibleViewSpec extends ViewBehaviours {

  val utils = new Utils

  "Result eligible view" must {

    "Contain results" when {
      "We have free hours value" in {
        val model = ResultsViewModel(freeHours = Some(15))
        val view = asDocument(resultEligible(model, utils)(fakeRequest, messages))

        assertContainsMessages(view, "15")
      }

      "User is eligible for TC scheme" in {
        val modelWithLessThan1000 = ResultsViewModel(tc = Some(500))
        val modelWithMoreThan1000 = ResultsViewModel(tc = Some(1500))

        val viewWithLessThan1000 = asDocument(resultEligible(modelWithLessThan1000, utils)(fakeRequest, messages))
        val viewWithMoreThan1000 = asDocument(resultEligible(modelWithMoreThan1000, utils)(fakeRequest, messages))

        assertContainsMessages(viewWithLessThan1000, "500")
        assertContainsMessages(viewWithMoreThan1000, "1,500")
      }

     "User is eligible for TC scheme but has answered UC to do you get tax credits or universal credits " in {

       val modelWithLessThan1000 = ResultsViewModel(tc = Some(500))
       val modelWithMoreThan1000 = ResultsViewModel(tc = Some(1500))
       val model = ResultsViewModel(tc = Some(500), taxCreditsOrUC = Some("tc"))
       val view = asDocument(resultEligible(model, utils)(fakeRequest, messages))

       val viewWithLessThan1000 = asDocument(resultEligible(modelWithLessThan1000, utils)(fakeRequest, messages))
       val viewWithMoreThan1000 = asDocument(resultEligible(modelWithMoreThan1000, utils)(fakeRequest, messages))

       assertContainsMessages(viewWithLessThan1000, "500")
       assertContainsMessages(viewWithMoreThan1000, "1,500")
      }

      "User is eligible for TFC scheme" in {
        val modelWithLessThan1000 = ResultsViewModel(tfc = Some(600))
        val modelWithMoreThan1000 = ResultsViewModel(tfc = Some(1600))

        val viewWithLessThan1000 = asDocument(resultEligible(modelWithLessThan1000, utils)(fakeRequest, messages))
        val viewWithMoreThan1000 = asDocument(resultEligible(modelWithMoreThan1000, utils)(fakeRequest, messages))

        assertContainsMessages(viewWithLessThan1000, "600")
        assertContainsMessages(viewWithMoreThan1000, "1,600")
      }

      "User is eligible for ESC scheme" in {
        val modelWithLessThan1000 = ResultsViewModel(esc = Some(900))
        val modelWithMoreThan1000 = ResultsViewModel(tfc = Some(1900))


        val viewWithLessThan1000 = asDocument(resultEligible(modelWithLessThan1000, utils)(fakeRequest, messages))
        val viewWithMoreThan1000 = asDocument(resultEligible(modelWithMoreThan1000, utils)(fakeRequest, messages))

        assertContainsMessages(viewWithLessThan1000, "900")
        assertContainsMessages(viewWithMoreThan1000, "1,900")
      }

    }

    "display correct no of free hours and text when user is eligible for free hours" when {
      "location is England and no of hours is 15" in {
        val model = ResultsViewModel(freeHours = Some(BigDecimal(15)), location = Some(Location.ENGLAND))
        val view = asDocument(resultEligible(model, utils)(fakeRequest, messages))

        assertContainsMessages(view, "15")
        assertContainsText(view, messages("result.free.hours.title"))
        assertContainsText(view, messages("result.you.could.get.up.to"))
        assertContainsText(view, messages("result.free.hours.hours"))
        assertContainsText(view, messages("result.free.hours.period.england", 570))
        assertContainsText(view, messages("result.free.hours.para1"))
      }

      "location is England and no of hours is 30" in {
        val model = ResultsViewModel(freeHours = Some(BigDecimal(30)), location = Some(Location.ENGLAND))
        val view = asDocument(resultEligible(model, utils)(fakeRequest, messages))

        assertContainsMessages(view, "30")
        assertContainsText(view, messages("result.free.hours.title"))
        assertContainsText(view, messages("result.you.could.get.up.to"))
        assertContainsText(view, messages("result.free.hours.hours"))
        assertContainsText(view, messages("result.free.hours.period.england", new DecimalFormat("##,###").format(1140)))
        assertContainsText(view, messages("result.free.hours.para1"))
      }

      "location is Wales" in {
        val model = ResultsViewModel(freeHours = Some(10), location = Some(Location.WALES))
        val view = asDocument(resultEligible(model, utils)(fakeRequest, messages))

        assertContainsMessages(view, "10")
        assertContainsText(view, messages("result.free.hours.title"))
        assertContainsText(view, messages("result.you.could.get"))
        assertContainsText(view, messages("result.free.hours.hours"))
        assertContainsText(view, messages("result.free.hours.period.wales"))
        assertContainsText(view, messages("result.free.hours.para1"))

      }

      "location is Scotland" in {
        val model = ResultsViewModel(freeHours = Some(16), location = Some(Location.SCOTLAND))
        val view = asDocument(resultEligible(model, utils)(fakeRequest, messages))

        assertContainsMessages(view, "16")
        assertContainsText(view, messages("result.free.hours.title"))
        assertContainsText(view, messages("result.you.could.get"))
        assertContainsText(view, messages("result.free.hours.hours"))
        assertContainsText(view, messages("result.free.hours.period.scotland"))
        assertContainsText(view, messages("result.free.hours.para1"))
      }

      "location is NI" in {
        val model = ResultsViewModel(freeHours = Some(12.5), location = Some(Location.NORTHERN_IRELAND))
        val view = asDocument(resultEligible(model, utils)(fakeRequest, messages))

        assertContainsMessages(view, "12 and a half hours")
        assertContainsText(view, messages("result.free.hours.title"))
        assertContainsText(view, messages("result.you.could.get.up.to"))
        assertContainsText(view, messages("result.free.hours.hours"))
        assertContainsText(view, messages("result.free.hours.period.northern-ireland"))
        assertContainsText(view, messages("result.free.hours.para1"))
      }
    }
  }
}
