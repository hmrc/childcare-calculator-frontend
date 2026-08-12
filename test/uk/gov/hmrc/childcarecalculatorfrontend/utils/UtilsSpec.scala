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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.YouPartnerBoth

class UtilsSpec extends SpecBase {

  "Utils" when {

    "getCall" should {
      "return the apt call when there is a value in element" in {
        val optionalBooleanValue = Some(true)

        val call1 = Call("GET", "one")
        val call2 = Call("GET", "two")

        val optionalPartner = Some(YouPartnerBoth.Partner)

        val partnerCall = Call("GET", "partner")
        val bothCall    = Call("GET", "both")

        def booleanPf: PartialFunction[Boolean, Call] = {
          case true  => call1
          case false => call2
        }

        def partnerPf: PartialFunction[YouPartnerBoth, Call] = {
          case YouPartnerBoth.Partner => partnerCall
          case YouPartnerBoth.Both    => bothCall
        }

        val utils = new Utils
        utils.getCall(optionalBooleanValue)(booleanPf) mustBe call1
        utils.getCall(optionalPartner)(partnerPf) mustBe partnerCall

      }

      "return SessionExpired call when there is None in element" in {
        val noneValue       = None
        val optionalPartner = Some(YouPartnerBoth.You)
        val call1           = Call("GET", "one")
        val call2           = Call("GET", "two")

        def stringPf: PartialFunction[YouPartnerBoth, Call] = {
          case YouPartnerBoth.Partner => call1
          case YouPartnerBoth.Both    => call2
        }

        val utils = new Utils
        utils.getCall(noneValue) { case _ => call1 } mustBe routes.SessionExpiredController.onPageLoad
        utils.getCall(optionalPartner)(stringPf) mustBe routes.SessionExpiredController.onPageLoad

      }
    }

    "formatBigDecimal" should {
      "return correct value when value has less than 4 digits" in {

        val utils = new Utils
        utils.formatBigDecimal(300) mustBe "300"
        utils.formatBigDecimal(30) mustBe "30"
        utils.formatBigDecimal(3) mustBe "3"
        utils.formatBigDecimal(30.35) mustBe "30"
      }

      "return correct value with comma when value has more than 3 digits" in {

        val utils = new Utils
        utils.formatBigDecimal(1433000) mustBe "1,433,000"
        utils.formatBigDecimal(3000) mustBe "3,000"
        utils.formatBigDecimal(3030.35) mustBe "3,030"
      }

      "return correct value without decimal when value has decimal points" in {

        val utils = new Utils
        utils.formatBigDecimal(300.3) mustBe "300"
        utils.formatBigDecimal(300.35) mustBe "300"
        utils.formatBigDecimal(28.35) mustBe "28"
        utils.formatBigDecimal(28.65) mustBe "29"

      }
    }

  }

}
