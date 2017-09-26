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

import org.joda.time.LocalDate
import org.scalatest.mock.MockitoSugar
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table
import play.api.Application
import uk.gov.hmrc.childcarecalculatorfrontend.{ObjectBuilder, FakeCCApplication}
import uk.gov.hmrc.play.test.UnitSpec

class HelperManagerSpec extends UnitSpec with FakeCCApplication with HelperManager with ObjectBuilder{

  "HelperManager" should {

    "selecting correct nmw" should {

      val configValues = Map(
        "default" -> Map(
          "apprentice" -> 56,
          "UNDER18" -> 64,
          "EIGHTEENTOTWENTY" -> 89,
          "TWENTYONETOTWENTYFOUR" -> 112,
          "OVERTWENTYFOUR" -> 120
        ),
        "2016/2017" -> Map(
          "apprentice" -> 54,
          "UNDER18" -> 64,
          "EIGHTEENTOTWENTY" -> 88,
          "TWENTYONETOTWENTYFOUR" -> 111,
          "OVERTWENTYFOUR" -> 115
        ),
        "2017/2018" -> Map(
          "apprentice" -> 56,
          "UNDER18" -> 64,
          "EIGHTEENTOTWENTY" -> 89,
          "TWENTYONETOTWENTYFOUR" -> 112,
          "OVERTWENTYFOUR" -> 120
        )
      )

      val testCases = Table(
        ("Date", "Configuration"),
        ("2016-04-05", "default"),
        ("2016-04-06", "2016/2017"),
        ("2017-01-01", "2016/2017"),
        ("2017-04-05", "2016/2017"),
        ("2017-04-06", "2017/2018"),
        ("2018-01-01", "2017/2018"),
        ("2018-04-05", "2017/2018"),
        ("2018-04-06", "default")
      )

      forAll(testCases) { case (date, configuration) =>
        s"return $configuration configuration for date $date" in {
          val nmw = getNMWConfig(LocalDate.parse(date))
          val conf = configValues(configuration)
          nmw.getInt("UNDER18").get shouldBe conf("UNDER18")
          nmw.getInt("EIGHTEENTOTWENTY").get shouldBe conf("EIGHTEENTOTWENTY")
          nmw.getInt("TWENTYONETOTWENTYFOUR").get shouldBe conf("TWENTYONETOTWENTYFOUR")
          nmw.getInt("OVERTWENTYFOUR").get shouldBe conf("OVERTWENTYFOUR")
        }
      }
    }
  }

  "HelperManager" when {
    "getOrException is called" should {
      "throw exception and return the default error when only None value is given" in {

        val testString = None
        intercept[RuntimeException] {
          HelperManager.getOrException(testString)
        }.getMessage should include ("no element found")

      }

      "throw exception and return the custom error message when only None value and custom message is given" in {

        val testString = None
        val customMessage = "error occured while fetching the object"

        intercept[RuntimeException] {
          HelperManager.getOrException(testString, errorMessage = customMessage)
        }.getMessage should include (customMessage)

      }

      "throw exception and return the correct error when only None value and other fields are given" in {

        val testString = None
        val controllerId = Some("testController")
        val objectName = Some("testObject")

        intercept[RuntimeException] {
          HelperManager.getOrException(optionalElement = testString,
                                        controllerId = controllerId,
                                        objectName = objectName)
        }.getMessage should include
            (s"no element found in ${controllerId.getOrElse("")} while fetching ${objectName.getOrElse("")} object")

      }

      "return the value of the optional element" in {
        val controllerId = Some("testController")
        val objectName = Some("testObject")

        val optionalBoolean = Some(true)
        val optionalString = Some("testString")

        val pageObjects = buildPageObjects
        val optionalPageObjects = Some(pageObjects)

        HelperManager.getOrException(optionalBoolean, controllerId, objectName) shouldBe true
        HelperManager.getOrException(optionalString, controllerId, objectName) shouldBe "testString"
        HelperManager.getOrException(optionalPageObjects, controllerId, objectName) shouldBe pageObjects
        HelperManager.getOrException(optionalPageObjects) shouldBe pageObjects
      }

    }
  }

  "HelperManager" when {
    "isLivingWithPartner is called" should {
      "throw exception and return the default error when value for livingWithPartner is None" in {
        val pageObjects = buildPageObjects.copy(livingWithPartner = None)

        intercept[RuntimeException] {
          HelperManager.isLivingWithPartner(pageObjects)
        }.getMessage should include ("no element found in HelperManager while fetching livingWithPartner")

      }

      "return the value of livingWithPartner" in {
        val pageObjects = buildPageObjects

        HelperManager.isLivingWithPartner(pageObjects) shouldBe true
      }
    }
  }

}
