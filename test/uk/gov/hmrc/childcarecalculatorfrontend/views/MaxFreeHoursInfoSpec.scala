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
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maxFreeHoursInfo
import uk.gov.hmrc.childcarecalculatorfrontend.utils.HelperManager


/**
 * Created by user on 18/09/17.
 */
class MaxFreeHoursInfoSpec extends TemplatesValidator with FakeCCApplication with HelperManager {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "It looks like you’re eligible for the maximum free hours"),
    ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = "Based on the information you’ve given so far, you can get a maximum of 30 hours free early " +
      "education and childcare a week in term time (1,160 hours a year) for your 3 or 4 year old."),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
  //TODO: update forward link
    ElementDetails(id = Some("next-button"), checkAttribute = Some("href"), value = maxFreeHoursInfoPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = creditsPath)
  )

  def getTemplate(): Document = {
    val template = maxFreeHoursInfo()(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "render template" in {
    val template = maxFreeHoursInfo.render(request, applicationMessages)
    template.contentType shouldBe "text/html"

    val template1 = maxFreeHoursInfo.f()(request, applicationMessages)
    template1.contentType shouldBe "text/html"
  }

  "display correct content" in {
      implicit val doc: Document = getTemplate()
      verifyPageContent()
  }

  "verify links" in {
      implicit val doc: Document = getTemplate()
      verifyLinks(linksData)
  }
}
