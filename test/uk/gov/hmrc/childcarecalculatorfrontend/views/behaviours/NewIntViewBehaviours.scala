/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat

trait NewIntViewBehaviours extends NewQuestionViewBehaviours[Int] {

  val number = 12

  def intPage(
      createView: (Form[Int]) => HtmlFormat.Appendable,
      messageKeyPrefix: String,
      expectedFormAction: String,
      messageDynamicValue: Option[String] = None
  ) =

    "behave like a page with an integer value field" when {
      "rendered" must {

        "contain a label for the value" in {
          val doc = asDocument(createView(form))
          assertContainsLabel(doc, "value", messages(s"$messageKeyPrefix.title", messageDynamicValue.getOrElse("")))
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(form))
          assertRenderedById(doc, "value")
        }
      }

      "rendered with a valid form" must {
        "include the form's value in the value input" in {
          val doc = asDocument(createView(form.fill(number)))
          doc.getElementById("value").attr("value") mustBe number.toString
        }
      }

      "rendered with an error" must {

        "show an error summary" in {
          val doc = asDocument(createView(form.withError(error)))
          assertRenderedByCssSelector(doc, ".govuk-error-summary__title")
        }

        "show an error in the value field's label" in {
          val doc       = asDocument(createView(form.withError(error)))
          val errorSpan = doc.getElementsByClass("govuk-error-message").first
          errorSpan.text must include(messages(errorMessage))
        }
      }
    }

}
