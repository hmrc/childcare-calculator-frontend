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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.{Form, FormError}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichChildrenDisabilityForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.InputOption
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichChildrenDisability

class WhichChildrenDisabilityViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "whichChildrenDisability"

  val fieldKey = "value"
  val errorMessage = "error.invalid"
  val error = FormError(fieldKey, errorMessage)

  val values = Seq(
    InputOption("Foo", "0"),
    InputOption("Bar", "1")
  )

  def form: Form[Set[String]] = WhichChildrenDisabilityForm()

  def createView = () =>
    whichChildrenDisability(frontendAppConfig, WhichChildrenDisabilityForm(), values, NormalMode)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Set[String]]) =>
    whichChildrenDisability(frontendAppConfig, form, values, NormalMode)(fakeRequest, messages)

  "WhichChildrenDisability view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    "rendered" must {
      "contain a legend for the question" in {
        val doc = asDocument(createView())
        val legends = doc.getElementsByTag("legend")
        legends.size mustBe 1
        legends.first.text mustBe messages(s"$messageKeyPrefix.heading")
      }

      "contain an input for the value" in {
        val doc = asDocument(createView())
        for { value <- values } yield {
          assertRenderedById(doc, value.id)
        }
      }

      "have no values checked when rendered with no form" in {
        val doc = asDocument(createView())
        for { value <- values } yield {
          assert(!doc.getElementById(value.id).hasAttr("checked"))
        }
      }

      values.zipWithIndex.foreach {
        case (v, i) =>
          s"has correct value checked when value `$v` is given" in {
            val data: Map[String, String] = Map(
              s"$fieldKey[$i]" -> v.value
            )

            val doc = asDocument(createViewUsingForm(form.bind(data)))

            assert(doc.getElementById(v.id).hasAttr("checked"))

            values.filterNot(_ == v).foreach {
              field =>
                assert(!doc.getElementById(field.id).hasAttr("checked"))
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
        val doc = asDocument(createViewUsingForm(form.withError(error)))
        assertRenderedById(doc, "error-summary-heading")
      }

      "show an error in the value field's label" in {
        val doc = asDocument(createViewUsingForm(form.withError(error)))
        val errorSpan = doc.getElementsByClass("error-notification").first
        errorSpan.text mustBe messages(errorMessage)
      }
    }
  }
}
