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
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
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

    "getCall" should {
      "return the apt call when there is a value in element" in {
        val optionalBooleanValue = Some(true)

        val call1 = Call("GET", "one")
        val call2 = Call("GET", "two")

        val optionalStringValue = Some(partner)
        val partnerCall = Call("GET", "partner")
        val bothCall = Call("GET", "both")

        def booleanPf: PartialFunction[Boolean, Call] ={
          case true => call1
          case false => call2
        }

        def stringPf: PartialFunction[String, Call] ={
          case Partner => partnerCall
          case Both => bothCall
        }

        val utils = new Utils
        utils.getCall(optionalBooleanValue)(booleanPf) mustBe call1
        utils.getCall(optionalStringValue)(stringPf) mustBe partnerCall

      }

      "return SessionExpired call when there is None in element" in {
        val noneValue = None
        val optionalStringValue = Some(You)
        val call1 = Call("GET", "one")
        val call2 = Call("GET", "two")

        def stringPf: PartialFunction[String, Call] ={
          case Partner => call1
          case Both => call2
        }

        val utils = new Utils
        utils.getCall(noneValue){case _ => call1} mustBe routes.SessionExpiredController.onPageLoad()
        utils.getCall(optionalStringValue)(stringPf) mustBe routes.SessionExpiredController.onPageLoad()

        }
      }

  }
}
