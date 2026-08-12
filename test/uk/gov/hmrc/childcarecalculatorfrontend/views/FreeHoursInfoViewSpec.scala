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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location.*
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursInfo

class FreeHoursInfoViewSpec extends NewViewBehaviours {

  val messageKeyPrefix    = "freeHoursInfo"
  val view: freeHoursInfo = inject[freeHoursInfo]

  def render(location: Location): Html = view(location)(using fakeRequest, messages)

  "FreeHoursInfo view" must {

    behave.like(normalPage(() => render(Location.England), messageKeyPrefix))

    behave.like(pageWithBackLink(() => render(Location.England)))

    "display correct content" when {

      "location is England" in {
        val viewInstance = view(Location.England)(using fakeRequest, messages)

        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.para1.england"))
        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.para2.england"))
        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.heading2"))
        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.guidance"))
        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.li.workingParents"))
        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.li.tfc"))
        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.li.vouchers"))
      }

      Seq(Scotland, Wales, NorthernIreland).foreach { location =>
        s"location is $location" in {
          val viewInstance = view(location)(using fakeRequest, messages)

          assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.para1.$location"))
          assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.heading2"))
          assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.guidance"))
          assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.li.tfc"))
          assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.li.vouchers"))
        }
      }
    }

  }

}
