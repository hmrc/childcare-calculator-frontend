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
  val view = application.injector.instanceOf[freeHoursInfo]

  def createView = () => view(false, false, true, true, Location.ENGLAND)(fakeRequest, messages)

  "FreeHoursInfo view" must {
    behave like normalPage(createView, messageKeyPrefix, "heading2", "li.vouchers", "li.tfc", "li.tax_credits")

    Seq(ENGLAND, SCOTLAND, WALES).foreach { location =>
      s"display correct content when user with location $location and have child aged 2" in {
        val view1 = view(true, false, false, false, location)(fakeRequest, messages)
        assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.para1.$location"))
        assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.li.workingParents"))
      }
    }

    Location.values.foreach { location =>
      s"display correct content when user with location $location and don't have child aged 2" in {
        val view1 = view(false, false, false, false, location)(fakeRequest, messages)
        val doc = asDocument(view1)
        assertContainsText(asDocument(view1), messages(s"$messageKeyPrefix.para1.$location"))
        if (location == ENGLAND) {
          assertContainsText(doc, messages(s"$messageKeyPrefix.para2.$location"))
        }
      }
    }

    "display correct information when user in England has a 3 year old child" in {
      val view1 = view(false, true, true, true, ENGLAND)(fakeRequest, messages)
      val doc = asDocument(view1)
      assertContainsText(doc, messages(s"$messageKeyPrefix.para1.england"))
    }


    "display the correct guidance text without any bullet points when user is eligible for only one scheme" in {
      val view1 = view(false, true, true, false, ENGLAND)(fakeRequest, messages)
      val doc = asDocument(view1)

      assertContainsText(doc, messages(s"$messageKeyPrefix.para1.$ENGLAND"))
      assertContainsText(doc, messages(s"$messageKeyPrefix.para2.$ENGLAND"))
      assertContainsText(doc, messages("freeHoursInfo.guidance.isEligibleForOnlyOneScheme"))
      assertContainsText(doc, messages("freeHoursInfo.no.approved.para"))
      assertContainsText(doc, messages("freeHoursInfo.no.childcare.para.end"))
      assertContainsText(doc, messages("freeHoursInfo.no.approved.para.link"))
      assertNotRenderedByCssSelector(doc, "bullets")
    }
  }
}
