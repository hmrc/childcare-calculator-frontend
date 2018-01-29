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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import org.scalatestplus.play.PlaySpec
import services.MoreInfoService
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel

class MoreInfoServiceSpec extends PlaySpec with SpecBase {

  /*
   * SHOULD:
    * Accept a location argument and scheme eligibility arguments
    * Return an array of title link values
    * Return exception if location doesn't exist
   */

  private val allSchemesValid = ResultsViewModel(
    tc = Some(2.0),
    tfc = Some(2.0),
    esc = Some(2.0),
    freeHours = Some(2.0))

  "MoreInfoService" should {

    "return correct title and links for England" in {

      val service = new MoreInfoService(messagesApi)
      service.get(Location.ENGLAND, allSchemesValid) must contain(
        Map("link" -> messagesApi("aboutYourResults.more.info.england.hours.link"),
          "title" -> messagesApi("aboutYourResults.more.info.england.hours.title")
        ))
    }
  }


}
