/*
 * Copyright 2019 HM Revenue & Customs
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

import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm

trait YesNoViewBehaviours extends QuestionViewBehaviours[Boolean] {

  val form = BooleanForm("yourMinimumEarnings.error.notCompleted", 0)

  def yesNoPage(createView: (Form[Boolean]) => HtmlFormat.Appendable,
                messageKeyPrefix: String,
                expectedFormAction: String,
                legend: Option[String] = None,
                legendDynamicValue: Option[String] = None) = {

    "behave like a page with a Yes/No question" when {
      "rendered" must {
        "contain a legend for the question" in {
          val doc = asDocument(createView(form))
          val legends = doc.getElementsByTag("legend")
          legends.size mustBe 1
          legends.first.text mustBe legend.getOrElse(messages(s"$messageKeyPrefix.heading", legendDynamicValue.getOrElse("")))
        }

        "contain an input for the value" in {
          val doc = asDocument(createView(form))
          assertRenderedById(doc, "value-yes")
          assertRenderedById(doc, "value-no")
        }

        "have no values checked when rendered with no form" in {
          val doc = asDocument(createView(form))
          assert(!doc.getElementById("value-yes").hasAttr("checked"))
          assert(!doc.getElementById("value-no").hasAttr("checked"))
        }

        "not render an error summary" in {
          val doc = asDocument(createView(form))
          assertNotRenderedById(doc, "error-summary_header")
        }
      }

      "rendered with a value of true" must {
        behave like answeredYesNoPage(createView, true)
      }

      "rendered with a value of false" must {
        behave like answeredYesNoPage(createView, false)
      }

      "rendered with an error" must {
        "show an error summary" in {
          val doc = asDocument(createView(form.withError(error)))
          assertRenderedById(doc, "error-summary-heading")
        }

        "show an error in the value field's label" in {
          val doc = asDocument(createView(form.withError(error)))
          val errorSpan = doc.getElementsByClass("error-notification").first
          errorSpan.text mustBe messages(errorMessage)
        }
      }
    }
  }


  def answeredYesNoPage(createView: (Form[Boolean]) => HtmlFormat.Appendable, answer: Boolean) = {

    "have only the correct value checked" in {
      val doc = asDocument(createView(BooleanForm().fill(answer)))
      assert(doc.getElementById("value-yes").hasAttr("checked") == answer)
      assert(doc.getElementById("value-no").hasAttr("checked") != answer)
    }

    "not render an error summary" in {
      val doc = asDocument(createView(BooleanForm().fill(answer)))
      assertNotRenderedById(doc, "error-summary_header")
    }
  }
}
