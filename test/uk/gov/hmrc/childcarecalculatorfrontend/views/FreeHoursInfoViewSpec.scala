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

import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location._
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursInfo

class FreeHoursInfoViewSpec extends NewViewBehaviours {

  val messageKeyPrefix = "freeHoursInfo"
  val view             = application.injector.instanceOf[freeHoursInfo]

  def createView(location: Location) = () => view(location)(fakeRequest, messages)

  "FreeHoursInfo view" must {

    behave.like(normalPage(createView(Location.ENGLAND), messageKeyPrefix))

    behave.like(pageWithBackLink(createView(Location.ENGLAND)))

    "display correct content" when {

      "location is England" in {
        val viewInstance = view(Location.ENGLAND)(fakeRequest, messages)

        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.para1.england"))
        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.para2.england"))
        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.heading2"))
        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.guidance"))
        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.li.workingParents"))
        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.li.tfc"))
        assertContainsText(asDocument(viewInstance), messages(s"$messageKeyPrefix.li.vouchers"))
      }

      Seq(SCOTLAND, WALES, NORTHERN_IRELAND).foreach { location =>
        s"location is $location" in {
          val viewInstance = view(location)(fakeRequest, messages)

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
