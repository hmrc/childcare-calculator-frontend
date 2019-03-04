/*
 * Copyright 2019 HM Revenue & Customs
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

  class TestObject extends FormErrorHelper

  val formErrorHelper = new TestObject
  val maxIncome = 100000
  val  errorKeyInvalidMaxEarnings = "invalid max earnings key"

  "FormErrorHelper" when {
    "validateBothMaxIncomeEarnings" must {

      "return the form with error when partner answered max earnings question under 100000 but input was above 100000" in {

        val maximumEarnings = Some(false)
        val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "8000",
          "partnerEmploymentIncomeCY" -> "100000"))

        val formWithError = inputForm.withError("partnerEmploymentIncomeCY", partnerEmploymentIncomeInvalidMaxEarningsErrorKey)

        formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
          maxIncome,
          inputForm) mustBe formWithError
      }

      "return the form with error when parent answered max earnings question under 100000 but input was above 100000" in {

        val maximumEarnings = Some(false)
        val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "100000",
          "partnerEmploymentIncomeCY" -> "8000"))

        val formWithError = inputForm.withError("parentEmploymentIncomeCY", parentEmploymentIncomeInvalidMaxEarningsErrorKey)

        formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
          maxIncome,
          inputForm) mustBe formWithError
      }

      "return the form with error when both answered max earnings question under 100000 but input was above 100000" in {

        val maximumEarnings = Some(false)
        val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "100000",
          "partnerEmploymentIncomeCY" -> "100000"))

        val formWithError = inputForm.withError("parentEmploymentIncomeCY", parentEmploymentIncomeBothInvalidMaxEarningsErrorKey).
          withError("partnerEmploymentIncomeCY", partnerEmploymentIncomeBothInvalidMaxEarningsErrorKey)

        formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
          maxIncome,
          inputForm) mustBe formWithError

      }

      "return the same form without error when both answered max earnings question under 100000 and input was below 100000" in {

        val maximumEarnings = Some(false)
        val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "8000",
          "partnerEmploymentIncomeCY" -> "8000"))

        formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
          maxIncome,
          inputForm) mustBe inputForm
      }

      "return the form when both answered max earnings question above 1000000 but input was above 1000000" in {

        val maximumEarnings = Some(true)
        val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "1000000",
          "partnerEmploymentIncomeCY" -> "1000000"))

        formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
          maxIncome,
          inputForm) mustBe inputForm
      }

      "return the form with error when partner answered max earnings question under 1000000 but input was above 1000000" in {

        val maximumEarnings = Some(true)
        val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "80000",
          "partnerEmploymentIncomeCY" -> "1000000"))

        formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
          maxIncome,
          inputForm) mustBe inputForm
      }

      "return the form with error when parent answered max earnings question under 1000000 but input was above 1000000" in {

        val maximumEarnings = Some(true)
        val inputForm = new EmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map("parentEmploymentIncomeCY" -> "1000000",
          "partnerEmploymentIncomeCY" -> "100000"))

        formErrorHelper.validateBothMaxIncomeEarnings(maximumEarnings,
          maxIncome,
          inputForm) mustBe inputForm
      }
    }

    "validateMaxIncomeEarnings" must {
      "return the original form if input value is good in case of Parent employment form" in {

        val maximumEarnings = Some(true)
        val inputForm = new ParentEmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map(defaultFormValueField -> "80000"))

        formErrorHelper.validateMaxIncomeEarnings(maximumEarnings,
          maxIncome,
          "invalid key",
          inputForm) mustBe inputForm
      }

      "return the original form if input value is good in case of Partner employment form" in {

        val maximumEarnings = Some(true)
        val inputForm = new PartnerEmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map(defaultFormValueField -> "80000"))

        formErrorHelper.validateMaxIncomeEarnings(maximumEarnings,
          maxIncome,
          "invalid key",
          inputForm) mustBe inputForm
      }

      "return the form with correct error if user has max earnings below 100,000 but input income value is more than 99,999.9 " in {

        val maximumEarnings = Some(false)
        val inputForm = new PartnerEmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map(defaultFormValueField -> "100000"))

        val formWithError = inputForm.withError(defaultFormValueField, errorKeyInvalidMaxEarnings)

        formErrorHelper.validateMaxIncomeEarnings(maximumEarnings,
          maxIncome,
          errorKeyInvalidMaxEarnings,
          inputForm) mustBe formWithError
      }

      "return the original form if user has max earnings over 100,000 and input income value is more than 99,999.9 and below 999,999.9 " in {

        val maximumEarnings = Some(true)
        val inputForm = new PartnerEmploymentIncomeCYForm(frontendAppConfig).apply().bind(Map(defaultFormValueField -> "102000"))

        formErrorHelper.validateMaxIncomeEarnings(maximumEarnings,
          maxIncome,
          errorKeyInvalidMaxEarnings,
          inputForm) mustBe inputForm
      }

    }
  }
}
