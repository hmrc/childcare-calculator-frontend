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

import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class WhatIsYourPartnersTaxCodeFormSpec extends FormSpec {

  val whatIsYourPartnersTaxCodeForm = new WhatIsYourPartnersTaxCodeForm(frontendAppConfig).apply()
  val errorKeyBlank = whatIsYourPartnersTaxCodeBlankErrorKey
  val errorKeyInvalid = whatIsYourPartnersTaxCodeInvalidErrorKey

  "WhatIsYourPartnersTaxCode Form" must {

    Seq("L", "M", "N", "S", "X", "l", "m", "n", "s", "x").foreach { taxCodeChar =>
      s"bind 3 numbers and one leading character $taxCodeChar" in {
        val form = whatIsYourPartnersTaxCodeForm.bind(Map("value" -> s"101$taxCodeChar"))
        form.get shouldBe s"101$taxCodeChar"
      }
    }

    Seq("L", "M", "N", "S", "X", "l", "m", "n", "s", "x").foreach { taxCodeChar =>
      s"bind 4 numbers and one leading character $taxCodeChar" in {
        val form = whatIsYourPartnersTaxCodeForm.bind(Map("value" -> s"1000$taxCodeChar"))
        form.get shouldBe s"1000$taxCodeChar"
      }
    }

    Seq("0T", "BR", "D0", "D1", "NT", "W1", "M1", "0t", "br", "d0", "d1", "nt", "w1", "m1").foreach { taxCodeChar =>
      s"bind 4 numbers and 2 alphanumeric character $taxCodeChar" in {
        val form = whatIsYourPartnersTaxCodeForm.bind(Map("value" -> s"1000$taxCodeChar"))
        form.get shouldBe s"1000$taxCodeChar"
      }
    }

    Seq("0T", "BR", "D0", "D1", "NT", "W1", "M1", "0t", "br", "d0", "d1", "nt", "w1", "m1").foreach { taxCodeChar =>
      s"bind 3 numbers and 2 alphanumeric character $taxCodeChar" in {
        val form = whatIsYourPartnersTaxCodeForm.bind(Map("value" -> s"100$taxCodeChar"))
        form.get shouldBe s"100$taxCodeChar"
      }
    }

    Seq("K100", "K1000", "k100", "k1000").foreach { taxCodeChar =>
      s"bind $taxCodeChar" in {
        val form = whatIsYourPartnersTaxCodeForm.bind(Map("value" -> s"$taxCodeChar"))
        form.get shouldBe s"$taxCodeChar"
      }
    }


    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(whatIsYourPartnersTaxCodeForm, Map("value" -> "-1"), expectedError)
    }

    Seq("011L", "120T", "11111L", "12L", "AAAAA", "11111", "101T", "115T", "1150K", "100K",
      "011l", "120t", "12l", "aaaaa", "101t", "115t", "1150k", "100k").foreach { taxCode =>
      s"fail to bind tax code $taxCode" in {
        val expectedError = error("value", errorKeyInvalid)
        checkForError(whatIsYourPartnersTaxCodeForm, Map("value" -> taxCode), expectedError)
      }
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(whatIsYourPartnersTaxCodeForm, Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(whatIsYourPartnersTaxCodeForm, emptyForm, expectedError)
    }

    "fail to bind decimal numbers" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(whatIsYourPartnersTaxCodeForm, Map("value" -> "123.45"), expectedError)
    }
  }
}
