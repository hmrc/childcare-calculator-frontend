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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoGetsBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoGetsBenefits
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

class WhoGetsBenefitsSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "Which of you gets benefits?"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("whoGetsBenefits-you"), value = "You"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("whoGetsBenefits-partner"), value = "Partner"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("whoGetsBenefits-both"), value = "Both"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = whoGetsBeneftsPath),
    // TODO: Use correct url when benefits bage is ready
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = underConstrctionPath)
  )

  def getTemplate(form: Form[Option[String]]): Document = {
    val template = whoGetsBenefits(form)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "render template successfully" in {
    val template = whoGetsBenefits.render(new WhoGetsBenefitsForm(applicationMessagesApi).form, request, applicationMessages)
    template.contentType shouldBe "text/html"

    val template1 = whoGetsBenefits.f(new WhoGetsBenefitsForm(applicationMessagesApi).form)(request, applicationMessages)
    template1.contentType shouldBe "text/html"
  }

  "display correct content - nothing selected, no errors" when {
    "nothing is selected initially" in {
      implicit val doc: Document = getTemplate(new WhoGetsBenefitsForm(applicationMessagesApi).form.fill(None))

      verifyPageContent()
      verifyPageLinks()
      verifyChecks()
      verifyErrors()
    }
  }

  "display correct content - correct value is selected, no errors" when {
    YouPartnerBothEnum.values.foreach { who => {
      val whoValue = who.toString
      s"valid value '${whoValue}' is selected" in {
        implicit val doc: Document = getTemplate(new WhoGetsBenefitsForm(applicationMessagesApi).form.fill(Some(whoValue)))

        verifyPageContent()
        verifyPageLinks()
        verifyChecks(List(s"${whoGetsBeneftsKey}-${whoValue}"))
        verifyErrors()
      }
    }}
  }

  s"display correct content - nothing is selected, error '${applicationMessages.messages("who.gets.benefits.not.selected.error")}' is displayed" when {
    val invalidData: List[String] = List("", "abcd", "123", "[*]")
    invalidData.foreach { invalidValue =>
      s"invalid value '${invalidValue}' is selected" in {
        val form = new WhoGetsBenefitsForm(applicationMessagesApi).form.bind(
          Map(
            whoGetsBeneftsKey -> invalidValue
          )
        )
        implicit val doc: Document = getTemplate(form)

        verifyPageContent()
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map(whoGetsBeneftsKey -> applicationMessages.messages("who.gets.benefits.not.selected.error"))
        )
        applicationMessages.messages("who.gets.benefits.not.selected.error") should not be "who.gets.benefits.not.selected.error"
      }
    }

  }

}
