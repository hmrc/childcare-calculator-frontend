/*
 * Copyright 2020 HM Revenue & Customs
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

import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class NoOfChildrenFormSpec extends FormSpec {

  val errorKeyBlank = "blank"
  val errorKeyNonNumeric: String = noOfChildrenNotInteger
  val NoOfChildrenForm = new NoOfChildrenForm(frontendAppConfig).apply()

  "NoOfChildren Form" must {

    "bind positive numbers" in {
      val form = NoOfChildrenForm.bind(Map("value" -> "1"))
      form.get shouldBe 1
    }

    "bind positive, max number" in {
      val expectedError = error("value", noOfChildrenErrorKey)
      checkForError(NoOfChildrenForm, Map("value" -> "20"), expectedError)
    }

    "bind positive, comma separated numbers" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(NoOfChildrenForm, Map("value" -> "1,0"), expectedError)
    }

    "fail to bind zero number" in {
      val expectedError = error("value", noOfChildrenErrorKey)
      checkForError(NoOfChildrenForm, Map("value" -> "0"), expectedError)
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(NoOfChildrenForm, Map("value" -> "-1"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(NoOfChildrenForm, Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", noOfChildrenRequiredErrorKey)
      checkForError(NoOfChildrenForm, Map("value" -> ""), expectedError)
    }

    "fail to bind decimal numbers" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(NoOfChildrenForm, Map("value" -> "1.234"), expectedError)
    }
  }
}
