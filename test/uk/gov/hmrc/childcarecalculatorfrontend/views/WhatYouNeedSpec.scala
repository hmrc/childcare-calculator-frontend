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
import org.jsoup.nodes._
import play.api.i18n.Messages.Implicits._
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whatYouNeed
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, CCRoutes, TemplatesValidator}
import play.api.test.Helpers._

class WhatYouNeedSpec extends TemplatesValidator with FakeCCApplication with CCRoutes {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "What you need"),
    ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = "You can use exact figures if you know them or estimates for any you may be unsure of about you and your partner (if you have one) such as:"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(0), value = "estimated or known childcare costs"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(1), value = "employment and working hours"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(2), value = "income this year and last"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(3), value = "personal tax code"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(4), value = "pension contributions"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(5), value = "family’s benefits"),
    ElementDetails(tagName = Some("li"), tagIndex = Some(6), value = "weeks and pay taken while on leave for a birth or an adoption"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(id = Some("next-button"), checkAttribute = Some("href"), value = locationPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = "https://www.gov.uk/childcare-calculator")
  )

  "calling whatYouNeed template" should {

    "render template" in {
      val template = whatYouNeed.render(request, applicationMessages)
      template.contentType shouldBe "text/html"

      val template1 = whatYouNeed.f()(request, applicationMessages)
      template1.contentType shouldBe "text/html"
    }

    "display correct content" in {
      implicit val doc: Document = {
        val template = whatYouNeed()(request, applicationMessages)
        Jsoup.parse(contentAsString(template))
      }

      verifyPageContent()
      verifyPageLinks()

    }
  }

}
