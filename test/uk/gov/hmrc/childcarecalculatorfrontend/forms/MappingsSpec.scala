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

import org.scalatestplus.play.PlaySpec
import play.api.data.validation.{Invalid, Valid}
import play.api.data.{Form, FormError}

class MappingsSpec extends PlaySpec with Mappings {

  "decimal" must {

    val testForm: Form[BigDecimal] =
      Form(
        "value" -> decimal("error.required", "error.invalid")
      )

    "bind a valid integer" in {
      val result = testForm.bind(Map("value" -> "1"))
      result.get mustEqual 1.0
    }

    "bind a valid decimal" in {
      val result = testForm.bind(Map("value" -> "1.2"))
      result.get mustEqual 1.2
    }

    "not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "unbind a valid value" in {
      val result = testForm.fill(123)
      result.apply("value").value.value mustEqual "123"
    }
  }

  "int" must {

    val testForm: Form[Int] =
      Form(
        "value" -> int("error.required", "error.invalid")
      )

    "bind a valid integer" in {
      val result = testForm.bind(Map("value" -> "1"))
      result.get mustEqual 1
    }

    "not bind a decimal" in {
      val result = testForm.bind(Map("value" -> "1.2"))
      result.errors must contain(FormError("value", "error.invalid"))
    }

    "not bind non numerics" in {
      val result = testForm.bind(Map("value" -> "not a number"))
      result.errors must contain(FormError("value", "error.invalid"))
    }

    "not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }
  }

  "firstError" must {

    "return Valid when all constraints pass" in {
      val result = firstError(maximumValue(10, "error.max"), minimumValue(1, "error.min"))(5)
      result mustEqual Valid
    }

    "return Invalid when the first constraint fails" in {
      val result = firstError(maximumValue(10, "error.max"), minimumValue(1, "error.min"))(20)
      result mustEqual Invalid("error.max")
    }

    "return Invalid when the second constraint fails" in {
      val result = firstError(maximumValue(10, "error.max"), minimumValue(1, "error.min"))(0)
      result mustEqual Invalid("error.min")
    }

    "return Invalid for the first error when both constraints fail" in {
      val result = firstError(maximumValue(5, "error.one"), maximumValue(10, "error.two"))(20)
      result mustEqual Invalid("error.one")
    }
  }

  "minimumValue" must {

    "return Valid for a number greater than the threshold" in {
      val result = minimumValue(1, "error.min").apply(2)
      result mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      val result = minimumValue(1, "error.min").apply(1)
      result mustEqual Valid
    }

    "return Invalid for a number below the threshold" in {
      val result = minimumValue(1, "error.min").apply(0)
      result mustEqual Invalid("error.min")
    }
  }

  "maximumValue" must {

    "return Valid for a number less than the threshold" in {
      val result = maximumValue(1, "error.max").apply(0)
      result mustEqual Valid
    }

    "return Valid for a number equal to the threshold" in {
      val result = maximumValue(1, "error.max").apply(1)
      result mustEqual Valid
    }

    "return Invalid for a number above the threshold" in {
      val result = maximumValue(1, "error.max").apply(2)
      result mustEqual Invalid("error.max")
    }
  }

  "inRange" must {

    "return Valid for a number in range" in {
      val result = inRange(1, 3, "error.invalid").apply(2)
      result mustEqual Valid
    }

    "return Invalid for a number lower than the threshold" in {
      val result = inRange(1, 3, "error.invalid").apply(0)
      result mustEqual Invalid("error.invalid")
    }

    "return Invalid for a number higher than the threshold" in {
      val result = inRange(1, 3, "error.invalid").apply(4)
      result mustEqual Invalid("error.invalid")
    }
  }

}
