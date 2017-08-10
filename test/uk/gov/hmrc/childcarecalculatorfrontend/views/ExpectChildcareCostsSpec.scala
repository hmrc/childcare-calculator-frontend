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
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table
import play.api.data.Form
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ExpectChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.expectChildcareCosts
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

class ExpectChildcareCostsSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "Do you have or expect to have childcare costs with an approved provider?"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("expectChildcareCosts-true"), value = "Yes"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("expectChildcareCosts-false"), value = "No"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = expectChildcareCostsPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = childAgedThreeOrFourPath)
  )

  val backUrl: Call = Call("GET", childAgedThreeOrFourPath)

  val infoContent = Table(
    ("Location", "Info text"),
    (LocationEnum.ENGLAND, "Support is only available for childcare with an approved provider. For example, a registered childminder, nursery or an Ofsted-registered childminding agency."),
    (LocationEnum.SCOTLAND, "Support is only available for childcare with an approved provider. For example, a registered childminder, nursery or a Scottish Care Inspectorate-registered childminding agency."),
    (LocationEnum.WALES, "You can get support for childcare costs with an approved provider. For example, a registered childminder, nursery or a Care and Social Services Inspectorate Wales-registered childminding agency."),
    (LocationEnum.NORTHERNIRELAND, "You can get support for childcare costs with an approved provider. For example, a registered childminder, nursery or an early years team-registered childminding agency.")
  )

  def getTemplate(form: Form[Option[Boolean]], location: LocationEnum): Document = {
    val template = expectChildcareCosts(form, backUrl, location)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "calling ExpectChildcareCosts template" should {

    "render template" in {
      val template = expectChildcareCosts.render(new ExpectChildcareCostsForm(applicationMessagesApi).form, backUrl, LocationEnum.ENGLAND, request, applicationMessages)
      template.contentType shouldBe "text/html"

      val template1 = expectChildcareCosts.f(new ExpectChildcareCostsForm(applicationMessagesApi).form, backUrl, LocationEnum.ENGLAND)(request, applicationMessages)
      template1.contentType shouldBe "text/html"
    }

    forAll(infoContent) { case (location, infoText) => {
      val dynamicContent = List(
        ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = infoText)
      )

      s"display correct content for location ${location.toString}" when {

        "nothing is selected initially" in {
          implicit val doc: Document = getTemplate(new ExpectChildcareCostsForm(applicationMessagesApi).form, location)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks()
          verifyErrors()
        }

        "true is selected" in {
          implicit val doc: Document = getTemplate(new ExpectChildcareCostsForm(applicationMessagesApi).form.fill(Some(true)), location)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks(List(s"${expectChildcareCostsKey}-true"))
          verifyErrors()
        }

        "false is selected" in {
          implicit val doc: Document = getTemplate(new ExpectChildcareCostsForm(applicationMessagesApi).form.fill(Some(false)), location)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks(List(s"${expectChildcareCostsKey}-false"))
          verifyErrors()
        }

        "form is submitted without data" in {
          val form = new ExpectChildcareCostsForm(applicationMessagesApi).form.bind(
            Map(
              expectChildcareCostsKey -> ""
            )
          )
          implicit val doc: Document = getTemplate(form, location)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks()
          verifyErrors(
            errors = Map("expectChildcareCosts" -> applicationMessages.messages("expect.childcare.costs.yes.no.not.selected.error"))
          )
        }

        "form is submitted with invalid data" in {
          val form = new ExpectChildcareCostsForm(applicationMessagesApi).form.bind(
            Map(
              expectChildcareCostsKey -> "abcd"
            )
          )
          implicit val doc: Document = getTemplate(form, location)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks()
          verifyErrors(
            errors = Map("expectChildcareCosts" -> applicationMessages.messages("error.boolean"))
          )
        }
      }}

    }

  }

}
