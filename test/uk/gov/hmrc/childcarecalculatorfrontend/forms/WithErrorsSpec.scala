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

import org.scalatest.{EitherValues, MustMatchers, WordSpec}
import play.api.data.{FormError, Mapping}

class WithErrorsSpec extends WordSpec with MustMatchers with EitherValues {

  val mapping: Mapping[String] = {
    import play.api.data.Forms._
    single(
      "foo" -> nonEmptyText
        .replaceError("error.required", "my.custom.error")
    )
  }

  ".replaceError" must {

    "replace an error message if it exists" in {
      val result = mapping.bind(Map.empty)
      result.left.value mustNot contain(FormError("foo", "error.required"))
      result.left.value must contain(FormError("foo", "my.custom.error"))
    }

    "successfully bind the original mapping" in {
      val result = mapping.bind(Map("foo" -> "bar"))
      result.right.value mustEqual "bar"
    }

    "replace an error message from an inner binding" in {
      val newMapping = mapping.replaceError(FormError("foo", "my.custom.error"), FormError("bar", "my.custom.error"))
      val result = newMapping.bind(Map.empty)
      result.left.value must contain(FormError("bar", "my.custom.error"))
      result.left.value mustNot contain(FormError("foo", "my.custom.error"))
      result.left.value mustNot contain(FormError("foo", "error.required"))
    }
  }
}
