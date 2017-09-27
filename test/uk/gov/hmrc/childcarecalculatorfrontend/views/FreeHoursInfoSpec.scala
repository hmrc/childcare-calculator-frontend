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
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursInfo
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

class FreeHoursInfoSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "It looks like you’re eligible for free hours"),
    ElementDetails(tagName = Some("h2"), tagIndex = Some(0), value = "Still to check"),
    ElementDetails(id=Some("free-hours-info-info"), value = "By giving more information, the calculator can check to see if you’re eligible to get help from:"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(0), value = "Childcare vouchers"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(1), value = "Tax-Free Childcare"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(2), value = "Tax credits"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = freeHoursInfoPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = expectChildcareCostsPath)
  )

  def getTemplate(haveCost: Boolean, isChild2: Boolean, location: LocationEnum): Document = {
    val template = freeHoursInfo(haveCost, isChild2, location)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "render template" in {
    val template = freeHoursInfo.render(false, true, LocationEnum.ENGLAND, request, applicationMessages)
    template.contentType shouldBe "text/html"

    val template1 = freeHoursInfo.f(true, false, LocationEnum.ENGLAND)(request, applicationMessages)
    template1.contentType shouldBe "text/html"
  }

  "display correct content" when {
    LocationEnum.values.foreach { loc =>
      s"${loc} is selected and no child of age 2" in {
        implicit val doc: Document = getTemplate(true, false, loc)
          verifyPageContent(
            List(
              ElementDetails(id=Some("free-hours-entitled-info"), tagName = Some("p"), tagIndex = Some(0), value = applicationMessages.messages(s"free.hours.entitled.info.${loc}"))
            )
          )
          verifyPageLinks()
      }

      s"${loc} is selected and has a child of age 2" in {
        implicit val doc: Document = getTemplate(false, true, loc)
        verifyPageContent(
          List(
            ElementDetails(id=Some("free-hours-entitled-info"), tagName = Some("p"), tagIndex = Some(0), value = applicationMessages.messages(s"free.hours.entitled.info.${loc}")),
            ElementDetails(id=Some("free-hours-entitled-info"), tagName = Some("p"), tagIndex = Some(1), value = applicationMessages.messages(s"free.hours.entitled.2.info.${loc}"))
          )
        )
        verifyPageLinks()
      }

    }
  }
}
