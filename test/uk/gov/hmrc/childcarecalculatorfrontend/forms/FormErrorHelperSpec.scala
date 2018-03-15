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
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class FormErrorHelperSpec extends SpecBase {

  val formErrorHelper = new FormErrorHelper()

  val errorKeyInvalidPartnerMaxEarnings = "invalid max key"
  val errorKeyInvalidParentMaxEarningsBoth = "invalid both max key"
  val errorKeyInvalidParentMaxEarnings = "invalid max key"
  val errorKeyInvalidPartnerMaxEarningsBoth  = "invalid both max key"


  "FormErrorHelper" must {

    "return the form with error when partner answered max earnings question under 100000 but input was above 100000" in {

      val maximumEarnings = Some(false)

      val errorParentKeyInvalid = "invalid key"
      val errorPartnerKeyInvalid = "invalid key"

      val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "8000",
      "partnerEmploymentIncomeCY" -> "100000"))

       val formWithError =  inputForm.withError("partnerEmploymentIncomeCY", errorKeyInvalidPartnerMaxEarnings)

      formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
        errorKeyInvalidParentMaxEarnings,
        errorKeyInvalidPartnerMaxEarnings,
        errorKeyInvalidParentMaxEarningsBoth,
        errorKeyInvalidPartnerMaxEarningsBoth,
        errorParentKeyInvalid,
        errorPartnerKeyInvalid,
        inputForm) mustBe formWithError

    }

    "return the form with error when parent answered max earnings question under 100000 but input was above 100000" in {

      val maximumEarnings = Some(false)
      val errorParentKeyInvalid = "invalid key"
      val errorPartnerKeyInvalid = "invalid key"

      val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "100000",
        "partnerEmploymentIncomeCY" -> "8000"))

      val formWithError =  inputForm.withError("parentEmploymentIncomeCY", errorKeyInvalidParentMaxEarnings)

      formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
        errorKeyInvalidParentMaxEarnings,
        errorKeyInvalidPartnerMaxEarnings,
        errorKeyInvalidParentMaxEarningsBoth,
        errorKeyInvalidPartnerMaxEarningsBoth,
        errorParentKeyInvalid,
        errorPartnerKeyInvalid,
        inputForm) mustBe formWithError

    }

    "return the form with error when both answered max earnings question under 100000 but input was above 100000" in {

      val maximumEarnings = Some(false)
      val errorParentKeyInvalid = "invalid key"
      val errorPartnerKeyInvalid = "invalid key"

      val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "100000",
        "partnerEmploymentIncomeCY" -> "100000"))

      val formWithError =  inputForm.withError("parentEmploymentIncomeCY", errorKeyInvalidParentMaxEarningsBoth).
        withError("partnerEmploymentIncomeCY", errorKeyInvalidPartnerMaxEarningsBoth)

      formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
        errorKeyInvalidParentMaxEarnings,
        errorKeyInvalidPartnerMaxEarnings,
        errorKeyInvalidParentMaxEarningsBoth,
        errorKeyInvalidPartnerMaxEarningsBoth,
        errorParentKeyInvalid,
        errorPartnerKeyInvalid,
        inputForm) mustBe formWithError

    }

    "return the same form without error when both answered max earnings question under 100000 and input was below 100000" in {

      val maximumEarnings = Some(false)
      val errorParentKeyInvalid = "invalid key"
      val errorPartnerKeyInvalid = "invalid key"

      val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "8000",
        "partnerEmploymentIncomeCY" -> "8000"))

        formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
          errorKeyInvalidParentMaxEarnings,
          errorKeyInvalidPartnerMaxEarnings,
          errorKeyInvalidParentMaxEarningsBoth,
          errorKeyInvalidPartnerMaxEarningsBoth,
          errorParentKeyInvalid,
          errorPartnerKeyInvalid,
        inputForm) mustBe inputForm
    }

  "return the form when both answered max earnings question above 1000000 but input was above 1000000" in {

      val maximumEarnings = Some(true)
      val errorParentKeyInvalid = parentEmploymentIncomeInvalidErrorKey
      val errorPartnerKeyInvalid = partnerEmploymentIncomeInvalidErrorKey

      val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "1000000",
        "partnerEmploymentIncomeCY" -> "1000000"))

      formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
        errorParentKeyInvalid,
        errorPartnerKeyInvalid,
        errorKeyInvalidParentMaxEarningsBoth,
        errorKeyInvalidPartnerMaxEarningsBoth,
        errorParentKeyInvalid,
        errorPartnerKeyInvalid,
        inputForm) mustBe inputForm

    }

    "return the form with error when partner answered max earnings question under 1000000 but input was above 1000000" in {

      val maximumEarnings = Some(true)
      val errorParentKeyInvalid = parentEmploymentIncomeInvalidErrorKey
      val errorPartnerKeyInvalid = partnerEmploymentIncomeInvalidErrorKey

      val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "80000",
        "partnerEmploymentIncomeCY" -> "1000000"))

      formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
        errorKeyInvalidParentMaxEarnings,
        errorKeyInvalidPartnerMaxEarnings,
        errorKeyInvalidParentMaxEarningsBoth,
        errorKeyInvalidPartnerMaxEarningsBoth,
        errorParentKeyInvalid,
        errorPartnerKeyInvalid,
        inputForm) mustBe inputForm
    }

    "return the form with error when parent answered max earnings question under 1000000 but input was above 1000000" in {

      val maximumEarnings = Some(true)
      val errorParentKeyInvalid = parentEmploymentIncomeInvalidErrorKey
      val errorPartnerKeyInvalid = partnerEmploymentIncomeInvalidErrorKey

      val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "1000000",
        "partnerEmploymentIncomeCY" -> "100000"))

      formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
        errorKeyInvalidParentMaxEarnings,
        errorKeyInvalidPartnerMaxEarnings,
        errorKeyInvalidParentMaxEarningsBoth,
        errorKeyInvalidPartnerMaxEarningsBoth,
        errorParentKeyInvalid,
        errorPartnerKeyInvalid,
        inputForm) mustBe inputForm
    }
  }
}
