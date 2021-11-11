/*
 * Copyright 2021 HM Revenue & Customs
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

import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.EligibilityModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.playComponents.scheme_result
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.tcSchemeGuidanceLinkUrl

class SchemeResultViewSpec extends NewViewBehaviours {

  lazy val appSchemeResult = app.injector.instanceOf[scheme_result]

  "Scheme Result view" must {

    "Contain title and paragraph" in {
        val view = asDocument(appSchemeResult(
          title = "You are eligible",
          couldGet = Some("you could get"),
          eligibility = Some(EligibilityModel("100", "")),
          periodText = Some("a month"),
          para1 = Some("some text"),
          para2 = Some(Html("some more text")),
          para3 = Some("some even more text")
        )(messages, lang))

        assertContainsMessages(view, "You are eligible", "you could get", "100", "a month", "some text", "some more text", "some even more text")
    }

    "contain the hyper link next to Tax Credit ineligibility message" in {
      val view = asDocument(appSchemeResult(
        title = "You are eligible",
        couldGet = Some("you could get"),
        eligibility = Some(EligibilityModel("100", "")),
        periodText = Some("a month"),
        para1 = Some("some text"),
        para2 = Some(Html("some more text")),
        para3 = Some("some even more text"),
        displayTCGuidanceLink = true
      )(messages, lang))

      assertContainsMessages(view, "You are eligible", "you could get", "100", "a month", "some text", "some more text", "some even more text")
      view.getElementById("tcGuidanceLink").attr("href") mustBe tcSchemeGuidanceLinkUrl
      view.getElementById("tcGuidanceLink").text mustBe messages("result.tc.scheme.guidance.link") +
        messages("feedback.hint.link.opens.new.tab")
    }
  }
}
