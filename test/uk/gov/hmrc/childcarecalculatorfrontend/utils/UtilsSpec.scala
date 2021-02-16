/*
 * Copyright 2021 HM Revenue & Customs
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

import org.joda.time.LocalDate
import play.api.libs.json.JsString
import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.LocationForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.LocationId
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.http.cache.client.CacheMap

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

        def booleanPf: PartialFunction[Boolean, Call] = {
          case true => call1
          case false => call2
        }

        def stringPf: PartialFunction[String, Call] = {
          case `partner` => partnerCall
          case `both` => bothCall
        }

        val utils = new Utils
        utils.getCall(optionalBooleanValue)(booleanPf) mustBe call1
        utils.getCall(optionalStringValue)(stringPf) mustBe partnerCall

      }

      "return SessionExpired call when there is None in element" in {
        val noneValue = None
        val optionalStringValue = Some(you)
        val call1 = Call("GET", "one")
        val call2 = Call("GET", "two")

        def stringPf: PartialFunction[String, Call] = {
          case `partner` => call1
          case `both` => call2
        }

        val utils = new Utils
        utils.getCall(noneValue) { case _ => call1 } mustBe routes.SessionExpiredController.onPageLoad()
        utils.getCall(optionalStringValue)(stringPf) mustBe routes.SessionExpiredController.onPageLoad()

      }
    }

    "valueFormatter" should {
      "return correct value when value has less than 4 digits" in {

        val utils = new Utils
        utils.valueFormatter(300) mustBe "300"
        utils.valueFormatter(30) mustBe "30"
        utils.valueFormatter(3) mustBe "3"
        utils.valueFormatter(30.35) mustBe "30"
      }

      "return correct value with comma when value has more than 3 digits" in {

        val utils = new Utils
        utils.valueFormatter(1433000) mustBe "1,433,000"
        utils.valueFormatter(3000) mustBe "3,000"
        utils.valueFormatter(3030.35) mustBe "3,030"
      }

      "return correct value without decimal when value has decimal points" in {

        val utils = new Utils
        utils.valueFormatter(300.3) mustBe "300"
        utils.valueFormatter(300.35) mustBe "300"
        utils.valueFormatter(28.35) mustBe "28"
        utils.valueFormatter(28.65) mustBe "29"

      }
    }

    "emptyCacheMap" should {
      "clear the existing cache map values and initialize an empty map" in {
        val sessionId = "sessionId"
        val existingMap = new CacheMap(sessionId, Map(LocationId.toString -> JsString(LocationForm.options.head.value)))

        val utils = new Utils
        utils.emptyCacheMap(existingMap) mustBe new CacheMap(sessionId, Map())
      }
    }

    "getEarningsForAgeRange" should {

      val utils = new Utils

      "return the 2019 earnings value for an apprentice on day of tax year change" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-04-06"), Some("apprentice")) mustBe 62
      }

      "return the 2019 earnings value for an apprentice on 1st April 2019" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-04-01"), Some("apprentice")) mustBe 62
      }

      "return the 2018 earnings value for an apprentice on 31 March 2019" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-03-31"), Some("apprentice")) mustBe 59
      }

      "return the 2019 earnings value for under 18 on day of tax year change" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-04-06"), Some("UNDER18")) mustBe 69
      }

      "return the 2019 earnings value for under 18 on 1st April 2019" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-04-01"), Some("UNDER18")) mustBe 69
      }

      "return the 2018 earnings value for under 18 on 31 March 2019" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-03-31"), Some("UNDER18")) mustBe 67
      }

      "return the 2019 earnings value for 18-20 year old on day of tax year change" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-04-06"), Some("EIGHTEENTOTWENTY")) mustBe 98
      }

      "return the 2019 earnings value for 18-20 year old on 1st April 2019" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-04-01"), Some("EIGHTEENTOTWENTY")) mustBe 98
      }

      "return the 2018 earnings value for 18-20 year old on 31 March 2019" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-03-31"), Some("EIGHTEENTOTWENTY")) mustBe 94
      }

      "return the 2019 earnings value for 21-24 year old on day of tax year change" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-04-06"), Some("TWENTYONETOTWENTYFOUR")) mustBe 123
      }

      "return the 2019 earnings value for 21-24 year old on 1st April 2019" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-04-01"), Some("TWENTYONETOTWENTYFOUR")) mustBe 123
      }

      "return the 2018 earnings value for 21-24 year old on 31 March 2019" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-03-31"), Some("TWENTYONETOTWENTYFOUR")) mustBe 118
      }

      "return the 2019 earnings value for over 24 year old on day of tax year change" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-04-06"), Some("OVERTWENTYFOUR")) mustBe 131
      }

      "return the 2019 earnings value for over 24 year old on 1st April 2019" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-04-01"), Some("OVERTWENTYFOUR")) mustBe 131
      }

      "return the 2018 earnings value for over 24 old on 31 March 2019" in {
        utils.getEarningsForAgeRange(frontendAppConfig.configuration, LocalDate.parse("2019-03-31"), Some("OVERTWENTYFOUR")) mustBe 125
      }
    }
  }
}
