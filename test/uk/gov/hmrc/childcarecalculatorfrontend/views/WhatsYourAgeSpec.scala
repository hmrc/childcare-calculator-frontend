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
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhatsYourAgeForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.AgeRangeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whatsYourAge
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

class WhatsYourAgeSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(

    ElementDetails(attribute = Some("for"), attributeValue = Some("whatsYourAge-UNDER18"), value = "Under 18"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("whatsYourAge-EIGHTEENTOTWENTY"), value = "18 to 20"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("whatsYourAge-TWENTYONETOTWENTYFOUR"), value = "21 to 24"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("whatsYourAge-OVERTWENTYFOUR"), value = "25 or over"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = whatYouNeedPath)
  )

  val backUrl: Call = Call("GET", whatYouNeedPath)

  def getTemplate(form: Form[Option[String]], isPartner: Boolean): Document = {
    val template = whatsYourAge(form, backUrl, isPartner)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "calling whatsYourAge template" should {

    "render template" in {
      val parentTemplate = whatsYourAge.render(new WhatsYourAgeForm(false, applicationMessagesApi).form, backUrl, false, request, applicationMessages)
      parentTemplate.contentType shouldBe "text/html"

      val partnerTemplate = whatsYourAge.f(new WhatsYourAgeForm(true, applicationMessagesApi).form, backUrl, true)(request, applicationMessages)
      partnerTemplate.contentType shouldBe "text/html"
    }

    "display correct content for parent" when {
      val dynamicContent: List[ElementDetails] = List(
        ElementDetails(id = Some("page-title"), value = "What’s your age?")
      )

      //TODO Change links to correct values
      val dynamicLinks:  List[ElementDetails] = List(
        ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = whatsYourAgePath + "/parent")
      )

      "nothing is selected initially for parent" in {
        implicit val doc: Document = getTemplate(new WhatsYourAgeForm(false, applicationMessagesApi).form.fill(None), false)

        verifyPageContent(dynamicContent)
        verifyPageLinks(dynamicLinks)
        verifyChecks()
        verifyErrors()
      }

      AgeRangeEnum.values.foreach { range => {
        val ageRangeValue = range.toString
        s"${ageRangeValue} is selected for parent" in {
          implicit val doc: Document = getTemplate(new WhatsYourAgeForm(false, applicationMessagesApi).form.fill(Some(ageRangeValue)), false)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks(List(s"${whatsYourAgeKey}-${ageRangeValue}"))
          verifyErrors()
        }
      }}

      "form is submitted without data for parent" in {
        val form = new WhatsYourAgeForm(false, applicationMessagesApi).form.bind(
          Map(
            whatsYourAgeKey -> ""
          )
        )
        implicit val doc: Document = getTemplate(form, false)

        verifyPageContent()
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map(whatsYourAgeKey -> "You must tell the calculator what your age is")
        )
      }
    }

    "display correct content for partner" when {
      val dynamicContent: List[ElementDetails] = List(
        ElementDetails(id = Some("page-title"), value = "What’s your partner’s age?")
      )

      //TODO Change links to correct values
      val dynamicLinks:  List[ElementDetails] = List(
        ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = whatsYourAgePath + "/partner")
      )

      "nothing is selected initially for partner" in {
        implicit val doc: Document = getTemplate(new WhatsYourAgeForm(true, applicationMessagesApi).form.fill(None), true)

        verifyPageContent(dynamicContent)
        verifyPageLinks(dynamicLinks)
        verifyChecks()
        verifyErrors()
      }

      AgeRangeEnum.values.foreach { range => {
        val ageRangeValue = range.toString
        s"${ageRangeValue} is selected for partner" in {
          implicit val doc: Document = getTemplate(new WhatsYourAgeForm(true, applicationMessagesApi).form.fill(Some(ageRangeValue)), true)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks(List(s"${whatsYourAgeKey}-${ageRangeValue}"))
          verifyErrors()
        }
      }}

      "form is submitted without data for partner" in {
        val form = new WhatsYourAgeForm(true, applicationMessagesApi).form.bind(
          Map(
            whatsYourAgeKey -> ""
          )
        )
        implicit val doc: Document = getTemplate(form, true)

        verifyPageContent()
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map(whatsYourAgeKey -> "You must tell the calculator what your partner’s age is")
        )
      }
    }
  }
}
