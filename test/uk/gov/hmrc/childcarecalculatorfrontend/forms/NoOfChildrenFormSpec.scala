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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.*

class NoOfChildrenFormSpec extends FormSpec {

  val missingErrorKey             = "blank"
  val errorKeyNonNumeric: String  = noOfChildrenNotInteger
  val noOfChildrenForm: Form[Int] = new NoOfChildrenForm(frontendAppConfig).apply()

  "NoOfChildren Form" must {

    "successfully bind positive numbers" in {
      val form = noOfChildrenForm.bind(Map("value" -> "1"))
      form.get mustBe 1
    }

    "fail to bind 20" in {
      val expectedError = error("value", noOfChildrenErrorKey)
      checkForError(noOfChildrenForm, Map("value" -> "20"), expectedError)
    }

    "fail to bind positive, comma separated numbers" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(noOfChildrenForm, Map("value" -> "1,0"), expectedError)
    }

    "fail to bind 0" in {
      val expectedError = error("value", noOfChildrenErrorKey)
      checkForError(noOfChildrenForm, Map("value" -> "0"), expectedError)
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(noOfChildrenForm, Map("value" -> "-1"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(noOfChildrenForm, Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", noOfChildrenRequiredErrorKey)
      checkForError(noOfChildrenForm, Map("value" -> ""), expectedError)
    }

    "fail to bind decimal numbers" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(noOfChildrenForm, Map("value" -> "1.234"), expectedError)
    }
  }

}
