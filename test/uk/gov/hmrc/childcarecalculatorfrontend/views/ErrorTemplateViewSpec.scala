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

import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.error_template

class errorTemplateViewSpec extends NewViewSpecBase {

  val view = app.injector.instanceOf[error_template]

  val pageTitle = "title"
  val headingText = "heading"
  val message = "message"

  def createView = () => view(pageTitle, headingText, message)(fakeRequest, messages)

  "behave like a normal page" when {
    "rendered" must {
      "have the correct banner title" in {
        val doc = asDocument(createView())
        val nav = doc.getElementsByClass("govuk-header__link govuk-header__link--service-name")
        nav.text mustBe messages("site.service_name")
      }
    }
  }

  "display the correct browser title" in {
    val doc = asDocument(createView())
    assertEqualsValue(doc, "title", s"$pageTitle - "+messages("site.service_name")+" - GOV.UK")
  }

  "display the correct page title" in {
    val doc = asDocument(createView())
    assertPageTitleEqualsMessage(doc, s"$headingText", 0)
  }

  "display the correct guidance" in {
    val doc = asDocument(createView())
    assertContainsText(doc, message)
  }

  "not display HMRC branding" in {
    val doc = asDocument(createView())
    assertNotRenderedByCssSelector(doc, ".organisation-logo")
  }

}
