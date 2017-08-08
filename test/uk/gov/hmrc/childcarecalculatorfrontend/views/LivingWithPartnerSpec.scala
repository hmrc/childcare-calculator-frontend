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
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}
import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.forms.LivingWithPartnerForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.livingWithPartner

class LivingWithPartnerSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "Do you have a partner that you live with?"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("livingWithPartner-true"), value = "Yes"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("livingWithPartner-false"), value = "No"),
    ElementDetails(id = Some("back-button"), value = "Back"),
    ElementDetails(id = Some("next-button"), value = "Continue")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = livingWithPartnerPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = freeHoursResultsPath)
  )

  val location = "england"

  def getTemplate(form: Form[Option[Boolean]]): Document = {
    val template = livingWithPartner(form)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "calling LivingWithPartner template" should {

    "render template" in {
      val template = livingWithPartner.render(new LivingWithPartnerForm(applicationMessagesApi).form, request, applicationMessages)
      template.contentType shouldBe "text/html"

      val template1 = livingWithPartner.f(new LivingWithPartnerForm(applicationMessagesApi).form)(request, applicationMessages)
      template1.contentType shouldBe "text/html"
    }

    "display correct content" when {
      "nothing is selected initially" in {
        implicit val doc: Document = getTemplate(new LivingWithPartnerForm(applicationMessagesApi).form)

        verifyPageContent()
        verifyPageLinks()
        verifyChecks()
        verifyErrors()
      }

      "true is selected" in {
        implicit val doc: Document = getTemplate(new LivingWithPartnerForm(applicationMessagesApi).form.fill(Some(true)))

        verifyPageContent()
        verifyPageLinks()
        verifyChecks(List(s"${livingWithPartnerKey}-true"))
        verifyErrors()
      }

      "false is selected" in {
        implicit val doc: Document = getTemplate(new LivingWithPartnerForm(applicationMessagesApi).form.fill(Some(false)))

        verifyPageContent()
        verifyPageLinks()
        verifyChecks(List(s"${livingWithPartnerKey}-false"))
        verifyErrors()
      }

      "form is submitted without data" in {
        val form = new LivingWithPartnerForm(applicationMessagesApi).form.bind(
          Map(
            livingWithPartnerKey -> ""
          )
        )
        implicit val doc: Document = getTemplate(form)

        verifyPageContent()
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map("livingWithPartner" -> applicationMessages.messages("living.with.partner.yes.no.not.selected.error"))
        )
      }

      "form is submitted with invalid data" in {
        val form = new LivingWithPartnerForm(applicationMessagesApi).form.bind(
          Map(
            livingWithPartnerKey -> "abcd"
          )
        )
        implicit val doc: Document = getTemplate(form)

        verifyPageContent()
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map("livingWithPartner" -> applicationMessages.messages("error.boolean"))
        )
      }
    }
  }
}
