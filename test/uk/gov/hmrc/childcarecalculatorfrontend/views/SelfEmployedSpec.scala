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
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.SelfEmployedForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.selfEmployed


class SelfEmployedSpec extends TemplatesValidator with FakeCCApplication {

  val backURL = Call("GET", underConstrctionPath)//TODO Should be 'selfemployed or apprentice' path

  override val contentData: List[ElementDetails] = List(
    ElementDetails(attribute = Some("for"), attributeValue = Some("selfEmployed-true"), value = "Yes"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("selfEmployed-false"), value = "No"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = underConstrctionPath)//TODO Should be 'selfemployed or apprentice' path
  )

  def getTemplate(form: Form[Option[Boolean]], isPartner: Boolean): Document = {
    val template = selfEmployed(form, isPartner, backURL)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  val isPartnerTestCase = Table(
    ("isPartner", "errorMessage", "pageTitle", "submitURL"),
    (false, "self.employed.less.than.12.months.parent.error", "Have you been self-employed less than 12 months?", parentSelfEmployedPath),
    (true, "self.employed.less.than.12.months.partner.error", "Has your partner been self-employed for less than 12 months?", partnerSelfEmployedPath)
  )

  forAll(isPartnerTestCase) { case (isPartner, errorMessage, pageTitle, submitURL) =>
    s"calling SelfEmployed template when isPartner = ${isPartner}" should {
      "render template" in {
        val template = selfEmployed.render(new SelfEmployedForm(isPartner, applicationMessagesApi).form, isPartner, backURL, request, applicationMessages)
        template.contentType shouldBe "text/html"

        val template1 = selfEmployed.f(new SelfEmployedForm(isPartner, applicationMessagesApi).form, isPartner, backURL)(request, applicationMessages)
        template1.contentType shouldBe "text/html"
      }

      val dynamicContent = List(
        ElementDetails(id = Some("page-title"), value = pageTitle)
      )

      "display correct content" when {
        "nothing is selected initially" in {
          implicit val doc: Document = getTemplate(new SelfEmployedForm(isPartner, applicationMessagesApi).form.fill(None), isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks()
          verifyErrors()
        }

        "true is selected" in {
          implicit val doc: Document = getTemplate(new SelfEmployedForm(isPartner, applicationMessagesApi).form.fill(Some(true)), isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks(
            List(selfEmployedKey +"-true")
          )
          verifyErrors()
        }

        "false is selected" in {
          implicit val doc: Document = getTemplate(new SelfEmployedForm(isPartner, applicationMessagesApi).form.fill(Some(false)), isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks(
            List(selfEmployedKey +"-false")
          )
          verifyErrors()
        }

        s"display ${applicationMessages.messages(errorMessage)} form is submitted without data" in {
          val form = new SelfEmployedForm(isPartner, applicationMessagesApi).form.bind(
            Map(
              selfEmployedKey -> ""
            )
          )
          implicit val doc: Document = getTemplate(form, isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks()
          verifyErrors(
            errors = Map(selfEmployedKey -> applicationMessages.messages(errorMessage)),
            validDateInlineErrors = false
          )
          applicationMessages.messages(errorMessage) should not be errorMessage
        }
      }
    }
  }
}
