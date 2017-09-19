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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.CreditsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.CreditsEnum
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.credits
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

class CreditsSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "Do you get tax credits or Universal Credit?"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("credits-taxcredits"), value = "Tax credits (includes Working and Child Tax Credit)"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("credits-universalcredit"), value = "Universal Credit"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("credits-none"), value = "None of these"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = creditsPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = maximumEarningsParentPath)
  )

  def getTemplate(form: Form[Option[String]]): Document = {
    val template = credits(form)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "calling credits template" should {

    "render template" in {
      val template = credits.render(new CreditsForm(applicationMessagesApi).form, request, applicationMessages)
      template.contentType shouldBe "text/html"

      val template1 = credits.f(new CreditsForm(applicationMessagesApi).form)(request, applicationMessages)
      template1.contentType shouldBe "text/html"
    }

    "display correct content" when {
      "nothing is selected initially" in {
        implicit val doc: Document = getTemplate(new CreditsForm(applicationMessagesApi).form.fill(None))

        verifyPageContent()
        verifyPageLinks()
        verifyChecks()
        verifyErrors()
      }

      CreditsEnum.values.foreach { credits => {
        val creditsValue = credits.toString
        s"${creditsValue} is selected" in {
          implicit val doc: Document = getTemplate(new CreditsForm(applicationMessagesApi).form.fill(Some(creditsValue)))

          verifyPageContent()
          verifyPageLinks()
          verifyChecks(List(s"${creditsKey}-${creditsValue}"))
          verifyErrors()
        }
      }}

      "form is submitted without data" in {
        val form = new CreditsForm(applicationMessagesApi).form.bind(
          Map(
            creditsKey -> ""
          )
        )
        implicit val doc: Document = getTemplate(form)

        verifyPageContent()
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map(creditsKey -> "You must tell the calculator if you get tax credits or Universal Credit")
        )
      }
    }
  }
}
