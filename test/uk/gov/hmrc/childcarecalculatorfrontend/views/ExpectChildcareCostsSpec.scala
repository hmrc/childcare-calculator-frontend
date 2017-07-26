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
import play.api.data.Form
import play.api.i18n.Messages.Implicits._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ExpectChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.expectChildcareCosts
import uk.gov.hmrc.childcarecalculatorfrontend.{CCRoutes, FakeCCApplication, TemplatesValidator}

class ExpectChildcareCostsSpec extends TemplatesValidator with FakeCCApplication with CCRoutes {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "Do you have or expect to have childcare costs with an approved provider?"),
    ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = "Support is only available for childcare with an approved provider. For example, a registered childminder, nursery or an Ofsted-registered childminding agency."),
    ElementDetails(attribute = Some("for"), attributeValue = Some("expectChildcareCosts-true"), value = "Yes"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("expectChildcareCosts-false"), value = "No"),
    ElementDetails(attribute = Some("type"), attributeValue = Some("submit"), checkAttribute = Some("title"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = expectChildcareCostsPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = childAgedThreeOrFourPath)
  )

  def getTemplate(form: Form[Option[Boolean]]): Document = {
    val template = expectChildcareCosts(form)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "calling ExpectChildcareCosts template" should {

    "render template" in {
      val template = expectChildcareCosts.render(new ExpectChildcareCostsForm(applicationMessagesApi).form, request, applicationMessages)
      template.contentType shouldBe "text/html"

      val template1 = expectChildcareCosts.f(new ExpectChildcareCostsForm(applicationMessagesApi).form)(request, applicationMessages)
      template1.contentType shouldBe "text/html"
    }

    "display correct content" when {
      "nothing is selected initially" in {
        implicit val doc: Document = getTemplate(new ExpectChildcareCostsForm(applicationMessagesApi).form)

        verifyPageContent()
        verifyPageLinks()
        verifyErrors()
      }

      "true is selected" in {
        implicit val doc: Document = getTemplate(new ExpectChildcareCostsForm(applicationMessagesApi).form.fill(Some(true)))

        verifyPageContent()
        verifyPageLinks()
        verifyChecks(Some(List(s"${expectChildcareCostsKey}-true")))
        verifyErrors()
      }

      "false is selected" in {
        implicit val doc: Document = getTemplate(new ExpectChildcareCostsForm(applicationMessagesApi).form.fill(Some(false)))

        verifyPageContent()
        verifyPageLinks()
        verifyChecks(Some(List(s"${expectChildcareCostsKey}-false")))
        verifyErrors()
      }

      "form is submitted without data" in {
        val form = new ExpectChildcareCostsForm(applicationMessagesApi).form.bind(
          Map(
            expectChildcareCostsKey -> ""
          )
        )
        implicit val doc: Document = getTemplate(form)

        verifyPageContent()
        verifyPageLinks()
        verifyErrors(
          errorTitle = Some("There is a problem"),
          errorHeading = Some("Check you have answered the question correctly"),
          errors = Map("expectChildcareCosts" -> applicationMessages.messages("expect.childcare.costs.yes.no.not.selected.error"))
        )
      }

    }

  }

}
