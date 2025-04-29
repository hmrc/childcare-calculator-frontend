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
  lazy val error   = FormError(fieldKey, errorMessage)
  lazy val divider = "divider"

  def fieldId(index: Int): String =
    index + 1 match {
      case 1 => fieldKey
      case i => form(fieldKey)(s"[$i]").id.replace("_", "-")
    }

  // scalastyle:off
  def checkboxPage(legend: Option[String] = None): Unit = {

    "rendered" must {

      "contain a legend for the question" in {
        val doc     = asDocument(createView())
        val legends = doc.getElementsByTag("legend")
        legends.size mustBe 1
        legends.first.text mustBe legend.getOrElse(messages(s"$messageKeyPrefix.heading"))
      }

      "contain an input or divider for the value" in {
        val doc = asDocument(createView())
        for { (value, i) <- values.zipWithIndex } yield
          if (value._2 != divider) assertRenderedById(doc, fieldId(i))
          else assertRenderedByCssSelector(doc, ".govuk-checkboxes__divider")
      }

      "contain a label for each input or divider" in {
        val doc = asDocument(createView())
        for { ((label, value), i) <- values.zipWithIndex } yield
          if (value != divider) {
            val id = fieldId(i)
            doc.select(s"label[for=$id]").text mustEqual messages(label).capitalize
          } else {
            doc.select(".govuk-checkboxes__divider").text mustEqual messages(label)
          }
      }

      "have no values checked when rendered with no form" in {
        val doc = asDocument(createView())
        for { (value, i) <- values.zipWithIndex.filterNot(_._1._2 == divider) } yield assert(
          !doc.getElementById(fieldId(i)).hasAttr("checked")
        )
      }

      values.zipWithIndex.filterNot(_._1._2 == divider).foreach { case (v, i) =>
        s"has correct value checked when value `$v` is given" in {
          val data: Map[String, String] = Map(
            s"$fieldKey[$i]" -> v._2
          )

          val doc = asDocument(createView(form.bind(data)))

          assert(doc.getElementById(fieldId(i)).hasAttr("checked"), s"${fieldId(i)} is not checked")

          values.zipWithIndex.filterNot(_._1._2 == divider).foreach { case (value, j) =>
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
        assertRenderedByCssSelector(doc, ".govuk-error-summary__title")
      }

      "show an error in the value field's label" in {
        val doc       = asDocument(createView(form.withError(error)))
        val errorSpan = doc.getElementsByClass("govuk-error-message").first
        errorSpan.text mustBe "Error: " + messages(errorMessage)
      }
    }
  }

}
