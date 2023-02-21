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
import play.api.data.FormError

class WithPrefixSpec extends PlaySpec {

  ".withPrefix" must {

    "add prefix to an existing key" in {
      val error = FormError("key", "error").withPrefix("prefix")
      error.key mustEqual "prefix.key"
      error.message mustEqual "error"
    }

    "not add an empty prefix" in {
      val error = FormError("key", "error").withPrefix("")
      error.key mustEqual "key"
      error.message mustEqual "error"
    }

    "use only a prefix if the key is empty" in {
      val error = FormError("", "error").withPrefix("prefix")
      error.key mustEqual "prefix"
      error.message mustEqual "error"
    }
  }
}
