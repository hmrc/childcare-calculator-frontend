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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class UtilsSpec extends SpecBase {

  "Utils" when {
    "getOrException is called" should {
      "throw exception and return the default error when only None value is given" in {

        val utils = new Utils

        val testString = None
        intercept[RuntimeException] {
          utils.getOrException(testString)
        }.getMessage mustBe "no element found"

      }

      "throw exception and return the custom error message when only None value and custom message is given" in {

        val testString = None
        val customMessage = "error occured while fetching the object"
        val utils = new Utils

        intercept[RuntimeException] {
          utils.getOrException(testString, errorMessage = customMessage)
        }.getMessage mustBe customMessage

      }

      "throw exception and return the correct error when only None value and other fields are given" in {

        val testString = None
        val controllerId = Some("testController")
        val objectName = Some("testObject")
        val utils = new Utils

        intercept[RuntimeException] {
          utils.getOrException(optionalElement = testString,
            controllerId = controllerId,
            objectName = objectName)
        }.getMessage mustBe
        s"no element found in ${controllerId.getOrElse("")} while fetching ${objectName.getOrElse("")}"

      }
    }

    "getCallOrSessionExpired" should {
      "return the input call when Some(call) is passed as input" in {

        val optionalElementValue = Some(true)
        val call = Call("GET", "http://abc.com")

        val utils = new Utils
        utils.getCallOrSessionExpired(optionalElementValue, call) mustBe call
      }

      "return the session expired page as call when None is passed as input" in {
        val call = Call("GET", "http://abc.com")

        val utils = new Utils
        utils.getCallOrSessionExpired(None, call) mustBe routes.SessionExpiredController.onPageLoad()
      }
    }

    "getCallForBooleanOrSessionExpired" should {
      "return the true call when optional element has Some(true)" in {

        val optionalElementValue = Some(true)
        val trueCall = Call("GET", "http://true.com")
        val falseCall = Call("GET", "http://false.com")

        val utils = new Utils
        utils.getCallForOptionBooleanOrSessionExpired(optionalElementValue, trueCall, falseCall) mustBe trueCall
      }

      "return the false call when optional element has Some(false)" in {

        val optionalElementValue = Some(false)
        val trueCall = Call("GET", "http://true.com")
        val falseCall = Call("GET", "http://false.com")

        val utils = new Utils
        utils.getCallForOptionBooleanOrSessionExpired(optionalElementValue, trueCall, falseCall) mustBe falseCall
      }

      "return the session expired page as call when None is passed as optional element" in {
        val trueCall = Call("GET", "http://true.com")
        val falseCall = Call("GET", "http://false.com")

        val utils = new Utils
        utils.getCallForOptionBooleanOrSessionExpired(None, trueCall, falseCall) mustBe routes.SessionExpiredController.onPageLoad()
      }
    }

    "getCallYouPartnerBothOrSessionExpired" should {
      "return the youCall when optional element has Some(you)" in {

        val optionalElementValue = Some(You)
        val youCall = Call("GET", "http://you.com")
        val partnerCall = Call("GET", "http://partner.com")
        val bothCall = Call("GET", "http://both.com")

        val utils = new Utils
        utils.getCallYouPartnerBothOrSessionExpired(optionalElementValue, youCall, partnerCall, bothCall) mustBe youCall
      }

      "return the partnerCall when optional element has Some(partner)" in {

        val optionalElementValue = Some(Partner)
        val youCall = Call("GET", "http://you.com")
        val partnerCall = Call("GET", "http://partner.com")
        val bothCall = Call("GET", "http://both.com")

        val utils = new Utils
        utils.getCallYouPartnerBothOrSessionExpired(optionalElementValue, youCall, partnerCall, bothCall) mustBe partnerCall
      }

      "return the bothCall when optional element has Some(both)" in {

        val optionalElementValue = Some(Both)
        val youCall = Call("GET", "http://you.com")
        val partnerCall = Call("GET", "http://partner.com")
        val bothCall = Call("GET", "http://both.com")

        val utils = new Utils
        utils.getCallYouPartnerBothOrSessionExpired(optionalElementValue, youCall, partnerCall, bothCall) mustBe bothCall
      }

      "return the session expired page as call when any invalid data is passed in optional element" in {
        val optionalElementValue = Some("invalid")
        val youCall = Call("GET", "http://you.com")
        val partnerCall = Call("GET", "http://partner.com")
        val bothCall = Call("GET", "http://both.com")

        val utils = new Utils
        utils.getCallYouPartnerBothOrSessionExpired(optionalElementValue, youCall, partnerCall, bothCall) mustBe
          routes.SessionExpiredController.onPageLoad()
      }

      "return the session expired page as call when None is passed as optional element" in {
        val optionalElementValue = None
        val youCall = Call("GET", "http://you.com")
        val partnerCall = Call("GET", "http://partner.com")
        val bothCall = Call("GET", "http://both.com")

        val utils = new Utils
        utils.getCallYouPartnerBothOrSessionExpired(optionalElementValue, youCall, partnerCall, bothCall) mustBe
          routes.SessionExpiredController.onPageLoad()
      }
    }

    "getCall" should {
      "return the apt call when there is a value in element" in {
        val optionalElementValue = Some(true)
        val call1 = Call("GET", "one")
        val call2 = Call("GET", "two")

        def valueToCall[T](element: T) = element match {
          case true => call1
          case false => call2
        }

        val utils = new Utils
        utils.getCall(optionalElementValue)(valueToCall) mustBe call1
      }

      "return SessionExpired call when there is None in element" in {
        val optionalElementValue = None
        val call1 = Call("GET", "one")
        val call2 = Call("GET", "two")

        def valueToCall[T](element: T) = element match {
          case true => call1
          case false => call2
        }

        val utils = new Utils
        utils.getCall(optionalElementValue)(valueToCall) mustBe routes.SessionExpiredController.onPageLoad()
        utils.getCall(optionalElementValue)(_ => call1) mustBe routes.SessionExpiredController.onPageLoad()
      }
    }


    }
}
