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
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table
import play.api.data.Form
import play.api.i18n.Messages.Implicits._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.GetBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.getBenefits
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

class GetBenefitsSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = applicationMessages.messages(s"get.benefits.para")),
    ElementDetails(tagName = Some("li"), tagIndex = Some(0), value = applicationMessages.messages(s"get.benefits.para.list1")),
    ElementDetails(tagName = Some("li"), tagIndex = Some(1), value = applicationMessages.messages(s"get.benefits.para.list2")),
    ElementDetails(tagName = Some("li"), tagIndex = Some(2), value = applicationMessages.messages(s"get.benefits.para.list3")),
    ElementDetails(tagName = Some("li"), tagIndex = Some(3), value = applicationMessages.messages(s"get.benefits.para.list4")),
    ElementDetails(tagName = Some("li"), tagIndex = Some(4), value = applicationMessages.messages(s"get.benefits.para.list5")),
    ElementDetails(tagName = Some("li"), tagIndex = Some(5), value = applicationMessages.messages(s"get.benefits.para.list6")),
    ElementDetails(tagName = Some("li"), tagIndex = Some(6), value = applicationMessages.messages(s"get.benefits.para.list7")),
    ElementDetails(tagName = Some("li"), tagIndex = Some(7), value = applicationMessages.messages(s"get.benefits.para.list8")),
    ElementDetails(attribute = Some("for"), attributeValue = Some("getBenefits-true"), value = "Yes"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("getBenefits-false"), value = "No"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = getBenefitsPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = vouchersPath)
  )

  def getTemplate(form: Form[Option[Boolean]], hasPartner: Boolean): Document = {
    val template = getBenefits(form, hasPartner)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  val testCases = Table(
    ("Has partner", "Page title", "Error message"),
    (false, applicationMessages.messages("get.benefits.header.single"), "get.benefits.not.selected.error.single"),
    (true, applicationMessages.messages("get.benefits.header.couple"), "get.benefits.not.selected.error.couple")
  )

  forAll(testCases) { case (hasPartner, pageTitle, errorMessage) =>
    val dynamicContent = List(
      ElementDetails(id = Some("page-title"), value = pageTitle)
    )
    s"if user has partner = ${hasPartner}" should {

      "render template" in {
        val template = getBenefits.render(new GetBenefitsForm(hasPartner, applicationMessagesApi).form, hasPartner, request, applicationMessages)
        template.contentType shouldBe "text/html"

        val template1 = getBenefits.f(new GetBenefitsForm(hasPartner, applicationMessagesApi).form, hasPartner)(request, applicationMessages)
        template1.contentType shouldBe "text/html"
      }

      "nothing is selected initially" in {
        implicit val doc: Document = getTemplate(new GetBenefitsForm(hasPartner, applicationMessagesApi).form, hasPartner)

        verifyPageContent(dynamicContent)
        verifyPageLinks()
        verifyChecks()
        verifyErrors()
      }

      "true is selected" in {
        implicit val doc: Document = getTemplate(new GetBenefitsForm(hasPartner, applicationMessagesApi).form.fill(Some(true)), hasPartner)

        verifyPageContent(dynamicContent)
        verifyPageLinks()
        verifyChecks(List(s"${getBenefitsKey}-true"))
        verifyErrors()
      }

      "false is selected" in {
        implicit val doc: Document = getTemplate(new GetBenefitsForm(hasPartner, applicationMessagesApi).form.fill(Some(false)), hasPartner)

        verifyPageContent(dynamicContent)
        verifyPageLinks()
        verifyChecks(List(s"${getBenefitsKey}-false"))
        verifyErrors()
      }

      "form is submitted without data" in {
        val form = new GetBenefitsForm(hasPartner, applicationMessagesApi).form.bind(
          Map(
            getBenefitsKey -> ""
          )
        )
        implicit val doc: Document = getTemplate(form, hasPartner)

        verifyPageContent(dynamicContent)
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map(getBenefitsKey -> applicationMessages.messages(errorMessage))
        )
        applicationMessages.messages(errorMessage) should not be errorMessage
      }

      "form is submitted with invalid data" in {
        val form = new GetBenefitsForm(hasPartner, applicationMessagesApi).form.bind(
          Map(
            getBenefitsKey -> "abcd"
          )
        )
        implicit val doc: Document = getTemplate(form, hasPartner)

        verifyPageContent(dynamicContent)
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map(getBenefitsKey -> applicationMessages.messages("error.boolean"))
        )
        applicationMessages.messages("error.boolean") should not be "error.boolean"
      }
    }
  }
}
