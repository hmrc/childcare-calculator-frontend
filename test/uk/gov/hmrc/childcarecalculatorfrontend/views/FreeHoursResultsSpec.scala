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
import play.api.test.Helpers.{contentAsString, _}
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursResults
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

class FreeHoursResultsSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "Your eligibility for childcare support"),
    ElementDetails(id = Some("free-hours-results-eligible"), tagName=Some("h2"), tagIndex=Some(0), value = "To be eligible for other schemes"),
    ElementDetails(id = Some("free-hours-results-eligible"), tagName=Some("h2"), tagIndex=Some(1),  value = "What you told the calculator"),
    ElementDetails(id = Some("free-hours-results-eligible"), tagName = Some("p"), tagIndex = Some(0), value = "You " +
      "need to have childcare costs to get support through the other schemes. So, if you’re here to see what " +
      "childcare you can afford, you should tell the calculator that you expect to have costs. You can estimate " +
      "how much you might pay at a later point."),
    ElementDetails(id = Some("free-hours-results-eligible"), tagName = Some("p"), tagIndex = Some(1),
      value = "You can go back and change your answers and see what you could get."),
    ElementDetails(tagName = Some("th"), tagIndex = Some(0), value = "General"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(1), value = "Childcare vouchers"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(2), value = "Tax-Free Childcare"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(3), value = "Tax credits"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = expectChildcareCostsPath),
    ElementDetails(id = Some("free-hours-results-general-aged2"), checkAttribute = Some("href"), value = childAgedTwoEditPath),
    ElementDetails(id = Some("free-hours-results-general-aged3-or-4"), checkAttribute = Some("href"), value = childAgedThreeOrFourEditPath),
    ElementDetails(id = Some("free-hours-results-eligible-cc"), checkAttribute = Some("href"), value = expectChildcareCostsEditPath)
  )

  def getTemplate(isChild3or4: Boolean, location: LocationEnum): Document = {
    val template = freeHoursResults(isChild3or4, location)(request, applicationMessages)
    println(s"$template")
    Jsoup.parse(contentAsString(template))
  }

  "render template" in {
    val template = freeHoursResults.render(false, LocationEnum.ENGLAND, request, applicationMessages)
    template.contentType shouldBe "text/html"

    val template1 = freeHoursResults.f(true, LocationEnum.WALES)(request, applicationMessages)
    template1.contentType shouldBe "text/html"
  }

//  "display correct content if having a child of 3 or 4 years" when {
//    LocationEnum.values.foreach { loc =>
//      s"${loc} is selected" in {
//        implicit val doc: Document = getTemplate(false, loc)
//        if (loc != "northern-ireland") {
//          println("no NI")
//          verifyPageContent(
//            List(
//            ElementDetails(id = Some("free-hours-results-not-entitled"), tagName = Some("p"), tagIndex = Some(0),
//              value = "You’re currently not eligible for any free hours because you don’t have a child aged between " +
//                "2 and 4. You don’t currently have or expect to have any approved childcare costs so you would not be " +
//                "eligible for any further support.")
//            )
//          )
//        } else {
//          println("NI")
//          verifyPageContent(
//            List(
//            ElementDetails(id = Some("free-hours-results-not-entitled"), tagName = Some("p"), tagIndex = Some(0),
//              value = "You’re currently not eligible for support because you don’t have a child aged between 3 and 4. You " +
//                "don’t currently have or expect to have any approved childcare costs so you would not be eligible for " +
//                "any further support.")
//            )
//          )
//        }
//        verifyPageLinks()
//      }
//    }
//  }


}
