/*
 * Copyright 2018 HM Revenue & Customs
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

import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.play.test.UnitSpec

class FormErrorHelperSpec extends SpecBase {

  val formErrorHelper = new FormErrorHelper()

  "FormErrorHelper" must {

    "return the form with error when partner answered max earnings question under 100000 but input was above 100000" in {



      val maximumEarnings = Some(false)
      val errorKeyInvalidMaxEarnings = "invalid max key"
      val errorKeyInvalid = "invalid key"

      val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "8000",
      "partnerEmploymentIncomeCY" -> "100000"))

       val formWithError =  inputForm.withError("partnerEmploymentIncomeCY", errorKeyInvalidMaxEarnings)

      formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings
        , errorKeyInvalidMaxEarnings,
        errorKeyInvalid,
        inputForm) mustBe formWithError

    }
  }

}
