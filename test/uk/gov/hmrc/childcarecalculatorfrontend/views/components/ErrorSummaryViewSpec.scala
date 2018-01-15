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

package uk.gov.hmrc.childcarecalculatorfrontend.views.components

import play.api.data.FormError
import uk.gov.hmrc.childcarecalculatorfrontend.views.ViewSpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.components.error_summary

class ErrorSummaryViewSpec extends ViewSpecBase {


  "ErrorSummary view" when {
    "contains errors" must {
      "display the correct title " in {
        val doc = asDocument(error_summary(Seq(FormError("name", "aboutYourChild.error.maxLength"))))
        assertContainsText(doc, messages("error.summary.title"))
        assertContainsText(doc, messages("aboutYourChild.error.maxLength"))
        doc.getElementsByAttribute("href").text() mustBe messages("aboutYourChild.error.maxLength")
      }
    }

    "does not contains errors" must {
      "not display error related guidance" in {
        val doc = asDocument(error_summary(Seq()))
        assertNotContainsText(doc, messages("error.summary.title"))

      }
    }

  }
}
