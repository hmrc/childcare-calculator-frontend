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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{LocationForm, PaidEmploymentForm}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.{location, paidEmployment}
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table

class PaidEmploymentSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(attribute = Some("for"), attributeValue = Some("paidEmployment-true"), value = "Yes"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("paidEmployment-false"), value = "No"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = paidEmploymentPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = livingWithPartnerPath)
  )

  def getTemplate(form: Form[Option[Boolean]], hasPartner: Boolean): Document = {
    val template = paidEmployment(form, hasPartner)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  val testCases = Table(
    ("Has partner", "Page title", "Error message"),
    (false, "Are you in paid employment or self-employed?", "paid.employment.not.selected.error.single"),
    (true, "Are you or your partner in paid employment or self-employed?", "paid.employment.not.selected.error.couple")
  )

  forAll(testCases) { case (hasPartner, pageTitle, errorMessage) =>
    val dynamicContent = List(
      ElementDetails(id = Some("page-title"), value = pageTitle)
    )
    s"if user has partner = ${hasPartner}" should {

      "render template" in {
        val template = paidEmployment.render(new PaidEmploymentForm(hasPartner, applicationMessagesApi).form, hasPartner, request, applicationMessages)
        template.contentType shouldBe "text/html"

        val template1 = paidEmployment.f(new PaidEmploymentForm(hasPartner, applicationMessagesApi).form, hasPartner)(request, applicationMessages)
        template1.contentType shouldBe "text/html"
      }

      "nothing is selected initially" in {
        implicit val doc: Document = getTemplate(new PaidEmploymentForm(hasPartner, applicationMessagesApi).form, hasPartner)

        verifyPageContent(dynamicContent)
        verifyPageLinks()
        verifyChecks()
        verifyErrors()
      }

      "true is selected" in {
        implicit val doc: Document = getTemplate(new PaidEmploymentForm(hasPartner, applicationMessagesApi).form.fill(Some(true)), hasPartner)

        verifyPageContent(dynamicContent)
        verifyPageLinks()
        verifyChecks(List(s"${paidEmploymentKey}-true"))
        verifyErrors()
      }

      "false is selected" in {
        implicit val doc: Document = getTemplate(new PaidEmploymentForm(hasPartner, applicationMessagesApi).form.fill(Some(false)), hasPartner)

        verifyPageContent(dynamicContent)
        verifyPageLinks()
        verifyChecks(List(s"${paidEmploymentKey}-false"))
        verifyErrors()
      }

      "form is submitted without data" in {
        val form = new PaidEmploymentForm(hasPartner, applicationMessagesApi).form.bind(
          Map(
            paidEmploymentKey -> ""
          )
        )
        implicit val doc: Document = getTemplate(form, hasPartner)

        verifyPageContent(dynamicContent)
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map(paidEmploymentKey -> applicationMessages.messages(errorMessage))
        )
        applicationMessages.messages(errorMessage) should not be errorMessage
      }

      "form is submitted with invalid data" in {
        val form = new PaidEmploymentForm(hasPartner, applicationMessagesApi).form.bind(
          Map(
            paidEmploymentKey -> "abcd"
          )
        )
        implicit val doc: Document = getTemplate(form, hasPartner)

        verifyPageContent(dynamicContent)
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map(paidEmploymentKey -> applicationMessages.messages("error.boolean"))
        )
        applicationMessages.messages("error.boolean") should not be "error.boolean"
      }
    }
  }
}
