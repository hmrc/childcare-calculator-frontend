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

import play.api.i18n.Messages.Implicits._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeCCApplication
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum
import uk.gov.hmrc.play.test.UnitSpec

class LocationFormSpec extends UnitSpec with FakeCCApplication {

  "LocationForm" should {

    "pre populate the form" in {
      val form = new LocationForm(applicationMessagesApi).form.fill(Some(LocationEnum.SCOTLAND.toString))
      form.get shouldBe Some(LocationEnum.SCOTLAND.toString)
    }

    "accept valid value" when {
      LocationEnum.values.foreach { loc => {
        val locationValue = loc.toString
        s"${locationValue} is selected" in {
          val result = new LocationForm(applicationMessagesApi).form.bind(Map(
            locationKey -> locationValue
          ))
          result.hasErrors shouldBe false
          result.value.get.get shouldBe locationValue
        }
      }
      }
    }

    "throw error" when {
      val invalidValues = List("", "abcd", "123")

      invalidValues.foreach { invalidValue =>
        s"'${invalidValue}' is selected" in {
          val result = new LocationForm(applicationMessagesApi).form.bind(Map(
            locationKey -> invalidValue
          ))
          result.hasErrors shouldBe true
          result.errors.length shouldBe 1
          result.errors.head.message shouldBe applicationMessages.messages("location.radio.not.selected.error")
          result.value shouldBe None
        }
      }
    }

  }

}
