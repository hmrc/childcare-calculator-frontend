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
import play.api.data.Form
import play.api.i18n.Messages.Implicits._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.LocationForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CCConstants
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.location
import uk.gov.hmrc.childcarecalculatorfrontend.{CCRoutes, FakeCCApplication, TemplatesValidator}

class LocationSpec extends TemplatesValidator with FakeCCApplication with CCRoutes with CCConstants {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "Where do you live?"),
    ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = "Free childcare hours are different between countries in the UK."),
    ElementDetails(attribute = Some("for"), attributeValue = Some("location-england"), value = "England"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("location-scotland"), value = "Scotland"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("location-wales"), value = "Wales"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("location-northern-ireland"), value = "Northern Ireland"),
    ElementDetails(attribute = Some("type"), attributeValue = Some("submit"), checkAttribute = Some("title"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = locationPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = whatYouNeedPath)
  )

  def getTemplate(form: Form[Option[String]]): Document = {
    val template = location(form)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "calling location template" should {

    "render template" in {
      val template = location.render(new LocationForm(applicationMessagesApi).form, request, applicationMessages)
      template.contentType shouldBe "text/html"

      val template1 = location.f(new LocationForm(applicationMessagesApi).form)(request, applicationMessages)
      template1.contentType shouldBe "text/html"
    }

    "display correct content" when {
      "nothing is selected initially" in {
        implicit val doc: Document = getTemplate(new LocationForm(applicationMessagesApi).form.fill(None))

        verifyPageContent()
        verifyPageLinks()
        verifyErrors()
      }

      LocationEnum.values.foreach { loc => {
        val locationValue = loc.toString
        s"${locationValue} is selected" in {
          implicit val doc: Document = getTemplate(new LocationForm(applicationMessagesApi).form.fill(Some(locationValue)))

          verifyPageContent()
          verifyPageLinks()
          verifyChecks(Some(List(s"${locationKey}-${locationValue}")))
          verifyErrors()
        }
      }}

      "form is submitted without data" in {
        val form = new LocationForm(applicationMessagesApi).form.bind(
          Map(
            locationKey -> ""
          )
        )
        implicit val doc: Document = getTemplate(form)

        verifyPageContent()
        verifyPageLinks()
        verifyErrors(
          errorTitle = Some("There is a problem"),
          errorHeading = Some("Check you have answered the question correctly"),
          errors = Map(locationKey -> "You must tell the calculator where you live")
        )
      }
    }
  }
}
