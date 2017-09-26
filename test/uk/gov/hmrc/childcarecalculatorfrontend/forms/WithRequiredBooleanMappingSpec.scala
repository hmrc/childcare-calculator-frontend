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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.Form
import play.api.data.Forms._

class WithRequiredBooleanMappingSpec extends FormSpec {

  "With Required Boolean Mapping" must {

    object TestForm extends WithRequiredBooleanMapping {
      def apply(): Form[Boolean] = Form(
        single(
          "value" -> requiredBoolean
        )
      )
    }

    "bind true" in {
      val form = TestForm().bind(Map("value" -> "true"))
      form.get shouldBe true
    }

    "bind false" in {
      val form = TestForm().bind(Map("value" -> "false"))
      form.get shouldBe false
    }

    "fail to bind non-booleans" in {
      val expectedError = error("value", "error.boolean")
      checkForError(TestForm(), Map("value" -> "not a boolean"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", "error.boolean")
      checkForError(TestForm(), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", "error.boolean")
      checkForError(TestForm(), emptyForm, expectedError)
    }
  }
}
