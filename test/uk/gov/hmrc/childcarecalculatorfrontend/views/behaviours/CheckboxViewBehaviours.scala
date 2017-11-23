/*
 * Copyright 2017 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.views.ViewSpecBase

trait CheckboxViewBehaviours[A] extends ViewSpecBase {

  def form: Form[Set[A]]
  def createView(form: Form[Set[A]]): Html
  def createView(): Html = createView(form)
  def values: Map[String, A]

  def fieldKey: String
  def errorMessage: String
  def messageKeyPrefix: String
  lazy val error = FormError(fieldKey, errorMessage)

  def checkboxPage(): Unit = {

    "rendered" must {

      "contain a legend for the question" in {
        val doc = asDocument(createView())
        val legends = doc.getElementsByTag("legend")
        legends.size mustBe 1
        legends.first.text mustBe messages(s"$messageKeyPrefix.heading")
      }

      "contain an input for the value" in {
        val doc = asDocument(createView())
        for { (value, i) <- values.values.zipWithIndex } yield {
          assertRenderedById(doc, form(fieldKey)(s"[$i]").id)
        }
      }

      "contain a label for each input" in {
        val doc = asDocument(createView())
        for { ((label, value), i) <- values.zipWithIndex } yield {
          val id = form(fieldKey)(s"[$i]").id
          doc.select(s"label[for=$id]").text mustEqual messages(label)
        }
      }

      "have no values checked when rendered with no form" in {
        val doc = asDocument(createView())
        for { (value, i) <- values.values.zipWithIndex } yield {
          assert(!doc.getElementById(form(fieldKey)(s"[$i]").id).hasAttr("checked"))
        }
      }

      values.values.zipWithIndex.foreach {
        case (v, i) =>
          s"has correct value checked when value `$v` is given" in {
            val data: Map[String, String] = Map(
              s"$fieldKey[$i]" -> v.toString
            )

            val doc = asDocument(createView(form.bind(data)))
            val field = form(fieldKey)(s"[$i]")

            assert(doc.getElementById(field.id).hasAttr("checked"), s"${field.id} is not checked")

            values.values.zipWithIndex.foreach {
              case (value, j) =>
                if (value != v) {
                  val field = form(fieldKey)(s"[$j]")
                  assert(!doc.getElementById(field.id).hasAttr("checked"), s"${field.id} is checked")
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
