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

package uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours

import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat

trait DateViewBehaviours[A] extends ViewBehaviours {

  val form: Form[A]

  def pageWithDateFields(createView: (Form[A]) => HtmlFormat.Appendable,
                         messageKeyPrefix: String,
                         expectedFormAction: String,
                         fields: String*) = {

    "behave like a date view page" when {
      for(field <- fields; sub <- Seq(field, s"$field.day", s"$field.month", s"$field.year")) {
        s"rendered with an error with field '$sub'" must {
          "show an error summary" in {
            val doc = asDocument(createView(form.withError(FormError(sub, "error"))))
            assertRenderedById(doc, "error-summary-heading")
          }

          s"show an error in the legend for fieldset when '$sub' has an error" in {
            val doc = asDocument(createView(form.withError(FormError(sub, "error"))))
            val errorSpan = doc.getElementsByClass("error-notification").first
            errorSpan mustNot be(null)
            errorSpan.parent mustNot be(null)
          }
        }
      }
    }
  }
}
