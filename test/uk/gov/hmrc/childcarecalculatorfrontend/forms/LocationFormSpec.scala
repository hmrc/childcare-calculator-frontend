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

    "accept when 'England' is selected" in {
      new LocationForm(applicationMessagesApi).form.bind(Map(
        "location" -> LocationEnum.ENGLAND.toString
      )).fold(
        errors =>
           errors.errors shouldBe empty,
        success =>
          success shouldBe Some("ENGLAND")
      )
    }

    "throw error when nothing is selected" in {
      new LocationForm(applicationMessagesApi).form.bind(Map(
        "location" -> ""
      )).fold(
        errors =>
          errors.errors.head.message shouldBe "You must tell the calculator where you live",
        success =>
          success shouldBe empty
      )
    }

    "pre populate the form" in {
      val form = new LocationForm(applicationMessagesApi).form.fill(Some(LocationEnum.SCOTLAND.toString))
      form.get shouldBe Some("SCOTLAND")
    }

  }

}
