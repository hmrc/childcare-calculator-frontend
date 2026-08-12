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
  def render(form: Form[Set[A]]): Html
  def render: () => Html = () => render(form)
  def values: Seq[(String, A)]

  def fieldKey: String
  def errorMessage: String
  def messageKeyPrefix: String
  def dividerMessageKey: String = s"$messageKeyPrefix.or"

  lazy val error = FormError(fieldKey, errorMessage)

  // scalastyle:off
  def checkboxPage(legend: Option[String] = None, divider: Boolean = true): Unit =

    def fieldId(index: Int): String =
      index + 1 match {
        case 1                                => fieldKey
        case i if i == values.size && divider => s"$fieldKey-${i + 1}"
        case i                                => s"$fieldKey-$i"
      }

    "behave like a multi-checkbox page" when {

      "rendered" must {

        "contain a legend for the question" in {
          val doc     = asDocument(render())
          val legends = doc.getElementsByTag("legend")
          legends.size mustBe 1
          legends.first.text mustBe legend.getOrElse(messages(s"$messageKeyPrefix.heading"))
        }

        if (divider) {
          "contain a divider" in {
            val doc = asDocument(render())
            assertRenderedByCssSelector(doc, ".govuk-checkboxes__divider")
          }

          "contain a label for the divider" in {
            val doc = asDocument(render())
            doc.select(".govuk-checkboxes__divider").text mustEqual messages(dividerMessageKey)
          }
        }

        values.zipWithIndex.foreach { case ((label, value), index) =>

          s"have an input for value '$value'".that {

            val id = fieldId(index)

            s"contains an input element with id '$id'" in {
              val doc = asDocument(render())

              assertRenderedById(doc, id)
            }

            s"contains a label with message key '$label'" in {
              val doc = asDocument(render())

              doc.select(s"label[for=$id]").text mustEqual messages(label).capitalize
            }

            s"is not checked when rendered with no form" in {
              val doc = asDocument(render())

              doc.getElementById(id).hasAttr("checked") mustBe false
            }

            s"is checked when bound in form" in {
              val doc = asDocument(render(form.fill(Set(value))))

              doc.getElementById(id).hasAttr("checked") mustBe true

              values.zipWithIndex.foreach { case (_, index2) =>
                if (index2 != index) {
                  val id2 = fieldId(index2)
                  doc.getElementById(id2).hasAttr("checked") mustBe false
                }
              }
            }

          }

        }

        "not render an error summary" in {
          val doc = asDocument(render())
          assertNotRenderedById(doc, "error-summary-heading")
        }
      }

      "rendered with an error" must {
        "show an error summary" in {
          val doc = asDocument(render(form.withError(error)))
          assertRenderedByCssSelector(doc, ".govuk-error-summary__title")
        }

        "show an error in the value field's label" in {
          val doc       = asDocument(render(form.withError(error)))
          val errorSpan = doc.getElementsByClass("govuk-error-message").first
          errorSpan.text mustBe "Error: " + messages(errorMessage)
        }
      }
    }

}
