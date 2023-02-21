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

import org.jsoup.nodes.{Document, Element}
import play.api.data.Form
import play.twirl.api.HtmlFormat

trait NewStringViewBehaviours extends NewQuestionViewBehaviours[String] {

  val answer = "answer"

  def stringPage(createView: Form[String] => HtmlFormat.Appendable,
                 messageKeyPrefix: String,
                 expectedFormAction: String,
                 expectedHintKeyLine1: Option[String],
                 expectedHintKeyLine2: Option[String],
                 args: Any*) = {

    "behave like a page with a string value field" when {
      "rendered" must {

        "contain a label for the value" in {
          val doc = asDocument(createView(form))
          val expectedHintTextLine1 = expectedHintKeyLine1 map (k => messages(k))
          val expectedHintTextLine2 = expectedHintKeyLine2 map (k => messages(k))
          assertContainsLabel(doc, "value", messages(s"$messageKeyPrefix.heading", args: _*), expectedHintTextLine1, expectedHintTextLine2)
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(form))
          assertRenderedById(doc, "value")
        }

        "show error in the title" in {
          val doc = asDocument(createView(form.withError(error)))
          doc.title.contains("Error: ") mustBe true
        }
      }

      "rendered with a valid form" must {
        "include the form's value in the value input" in {
          val doc = asDocument(createView(form.fill(answer)))

          doc.getElementById("value").tagName() match {
            case "textarea" =>
              doc.getElementById("value").text mustBe answer
            case _ =>
              doc.getElementById("value").attr("value") mustBe answer
          }
        }
      }

      "rendered with an error" must {

        "show an error summary" in {
          val doc = asDocument(createView(form.withError(error)))
          assertRenderedById(doc, "error-summary-title")
        }

        "show an error in the value field's label" in {
          val doc = asDocument(createView(form.withError(error)))
          val errorSpan = doc.getElementsByClass("govuk-error-message").first
          errorSpan.text mustBe s"Error: ${messages(errorMessage)}"
        }
      }
    }
  }

  def assertContainsLabel(doc: Document, forElement: String, expectedText: String, expectedHintTextLine1: Option[String] = None,
                                   expectedHintTextLine2: Option[String] = None) = {
    val labels = doc.getElementsByAttributeValue("for", forElement)
    assert(labels.size == 1, s"\n\nLabel for $forElement was not rendered on the page.")
    val label = labels.first
    assert(label.text.contains(expectedText), s"\n\nLabel for $forElement was not $expectedText")

    if (expectedHintTextLine1.isDefined) {
      assert(doc.getElementsByClass("govuk-hint").first.text == expectedHintTextLine1.get,
        s"\n\nLabel for $forElement did not contain hint text $expectedHintTextLine1")
    }

    if (expectedHintTextLine2.isDefined) {
      assert(label.getElementById("hint-line-2").text == expectedHintTextLine2.get,
        s"\n\nLabel for $forElement did not contain hint text $expectedHintTextLine2")
    }
  }
}