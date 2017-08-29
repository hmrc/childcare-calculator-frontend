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
import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichBenefitsDoYouGetForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.Benefits
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.benefits

/**
 * Created by user on 24/08/17.
 */
class BenefitsSpec extends TemplatesValidator with FakeCCApplication {

  val backURL = Call("GET", whoGetsBenefitsPath)

  override val contentData: List[ElementDetails] = List(
    ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = "Select all that apply."),
    ElementDetails(attribute = Some("for"), attributeValue = Some(s"${WhichBenefitsDoYouGetKey}-income"), value = "Income benefits"),
    ElementDetails(attribute = Some("for"), attributeValue = Some(s"${WhichBenefitsDoYouGetKey}-disability"), value = "Disability benefits"),
    ElementDetails(attribute = Some("for"), attributeValue = Some(s"${WhichBenefitsDoYouGetKey}-higherRateDisability"), value = "Higher rate disability benefits"),
    ElementDetails(attribute = Some("for"), attributeValue = Some(s"${WhichBenefitsDoYouGetKey}-carersAllowance"), value = "Carer’s Allowance"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = whoGetsBenefitsPath)
  )

  def getTemplate(form: Form[Benefits], isPartner: Boolean): Document = {
    val template = benefits(form, isPartner, backURL)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  val isPartnerTestCase = Table(
    ("isPartner", "errorMessage", "pageTitle", "submitURL"),
    (false, "which.benefits.do.you.get.not.selected.parent.error", "Which benefits do you get?", parentBenefitsPath),
    (true, "which.benefits.do.you.get.not.selected.partner.error", "Which benefits does your partner get?", partnerBenefitsPath)
  )
  forAll(isPartnerTestCase) { case(isPartner, errorMessage, pageTitle, submitURL) =>
    s"calling benefits template when isPartner = ${isPartner}" should {

      "render template" in {
        val template = benefits.render(new WhichBenefitsDoYouGetForm(isPartner, applicationMessagesApi).form, isPartner, backURL, request, applicationMessages)
        template.contentType shouldBe "text/html"

        val template1 = benefits.f(new WhichBenefitsDoYouGetForm(isPartner, applicationMessagesApi).form, isPartner, backURL)(request, applicationMessages)
        template1.contentType shouldBe "text/html"
      }

      val dynamicContent = List(
        ElementDetails(id = Some("page-title"), value = pageTitle)
      )

      val dynamicLinks = List(
        ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = submitURL)
      )

      "display correct content" when {
        "nothing is selected initially" in {
          implicit val doc: Document = getTemplate(new WhichBenefitsDoYouGetForm(isPartner, applicationMessagesApi).form.fill(Benefits()), isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks()
          verifyErrors()
        }

        "all values are selected" in {
          implicit val doc: Document = getTemplate(new WhichBenefitsDoYouGetForm(isPartner, applicationMessagesApi).form.fill(Benefits(true, true, true, true)), isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks(
            List(
              s"${WhichBenefitsDoYouGetKey}-income",
              s"${WhichBenefitsDoYouGetKey}-disability",
              s"${WhichBenefitsDoYouGetKey}-higherRateDisability",
              s"${WhichBenefitsDoYouGetKey}-carersAllowance"
            )
          )
          verifyErrors()
        }


        s"display ${applicationMessages.messages(errorMessage)} form is submitted without data" in {
          val form = new WhichBenefitsDoYouGetForm(isPartner, applicationMessagesApi).form.bind(
            Map(
              s"${WhichBenefitsDoYouGetKey}-income" -> "false",
              s"${WhichBenefitsDoYouGetKey}-disability" -> "false",
              s"${WhichBenefitsDoYouGetKey}-higherRateDisability" -> "false",
              s"${WhichBenefitsDoYouGetKey}-carersAllowance" -> "false"
            )
          )
          implicit val doc: Document = getTemplate(form, isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks()
          verifyErrors(
            errors = Map("" -> applicationMessages.messages(errorMessage)),
            validDateInlineErrors = false
          )
          applicationMessages.messages(errorMessage) should not be errorMessage
        }
      }
    }
  }
}
