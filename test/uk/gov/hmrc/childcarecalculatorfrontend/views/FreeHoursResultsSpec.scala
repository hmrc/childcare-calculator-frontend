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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import play.api.i18n.Messages.Implicits._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursResults
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

class FreeHoursResultsSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "Your eligibility for childcare support"),
//    ElementDetails(tagName = Some("h2"), tagIndex = Some(0), value = "Still to check"),
//    ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = "By giving more information, the calculator can check to see if you’re eligible to get help from:"),
//    ElementDetails(tagName = Some("li"), tagIndex = Some(0), value = "Childcare vouchers"),
//    ElementDetails(tagName = Some("li"), tagIndex = Some(1), value = "Tax-Free Childcare"),
//    ElementDetails(tagName = Some("li"), tagIndex = Some(2), value = "Tax credits"),
//    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = expectChildcareCostsPath)
  )

  "display correct content for 15 hours eligibility" in {
    implicit val doc: Document = {
      val template = freeHoursResults(true, "england")(request, applicationMessages)
      Jsoup.parse(contentAsString(template))
    }

    verifyPageContent()
    verifyPageLinks()

  }
}
