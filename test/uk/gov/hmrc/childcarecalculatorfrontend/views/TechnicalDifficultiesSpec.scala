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
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.technicalDifficulties
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, CCRoutes, TemplatesValidator}
import play.api.test.Helpers._

class TechnicalDifficultiesSpec extends TemplatesValidator with FakeCCApplication with CCRoutes {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "We're experiencing technical difficulties"),
    ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = "We're having difficulties with the service, try again in a few minutes."),
    ElementDetails(id = Some("technical-difficulties-external"), value = "Return to GOV.UK")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(id = Some("technical-difficulties-external"), checkAttribute = Some("href"), value = "https://www.gov.uk/help-with-childcare-costs/approved-childcare")
  )

  "calling technicalDifficulties template" should {

    "render template" in {
      val template = technicalDifficulties.render(request, applicationMessages)
      template.contentType shouldBe "text/html"

      val template1 = technicalDifficulties.f()(request, applicationMessages)
      template1.contentType shouldBe "text/html"
    }

    "display correct content" in {
      implicit val doc: Document = {
        val template = technicalDifficulties()(request, applicationMessages)
        Jsoup.parse(contentAsString(template))
      }

      verifyPageContent()
      verifyPageLinks()

    }
  }

}
