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

import play.api.data.{Form, FormError}
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.views.NewViewSpecBase

trait NewCheckboxViewBehaviours[A] extends NewViewSpecBase {

  def form: Form[Set[A]]
  def createView(form: Form[Set[A]]): Html
  def createView(): Html = createView(form)
  def values: Seq[(String, String)]

  def fieldKey: String
  def errorMessage: String
  def messageKeyPrefix: String
  lazy val error = FormError(fieldKey, errorMessage)

  def fieldId(index: Int): String = {
    index + 1 match {
      case 1 => "value"
      case i => form(fieldKey)(s"[$i]").id.replace("_", "-")
    }
  }

  def checkboxPage(legend: Option[String] = None): Unit = {

    "rendered" must {

      "contain a legend for the question" in {
        val doc = asDocument(createView())
        val legends = doc.getElementsByTag("legend")
        legends.size mustBe 1
        legends.first.text mustBe legend.getOrElse(messages(s"$messageKeyPrefix.heading"))
      }

      "contain an input for the value" in {
        val doc = asDocument(createView())
        for { (value, i) <- values.zipWithIndex } yield {
          assertRenderedById(doc, fieldId(i))
        }
      }

      "contain a label for each input" in {
        val doc = asDocument(createView())
        for { ((label, value), i) <- values.zipWithIndex } yield {
          val id = fieldId(i)
          doc.select(s"label[for=$id]").text mustEqual messages(label).capitalize
        }
      }

      "have no values checked when rendered with no form" in {
        val doc = asDocument(createView())
        for { (value, i) <- values.zipWithIndex } yield {
          assert(!doc.getElementById(fieldId(i)).hasAttr("checked"))
        }
      }

      values.zipWithIndex.foreach {
        case (v, i) =>
          s"has correct value checked when value `$v` is given" in {
            val data: Map[String, String] = Map(
              s"$fieldKey[$i]" -> v._2
            )

            val doc = asDocument(createView(form.bind(data)))

            assert(doc.getElementById(fieldId(i)).hasAttr("checked"), s"${fieldId(i)} is not checked")

            values.zipWithIndex.foreach {
              case (value, j) =>
                if (value != v) {
                  assert(!doc.getElementById(fieldId(j)).hasAttr("checked"), s"${fieldId(j)} is checked")
                }
            }
          }
      }

      "not render an error summary" in {
        val doc = asDocument(createView())
        assertNotRenderedById(doc, "error-summary-heading")
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
        errorSpan.text mustBe "Error: " + messages(errorMessage)
      }
    }
  }
}
