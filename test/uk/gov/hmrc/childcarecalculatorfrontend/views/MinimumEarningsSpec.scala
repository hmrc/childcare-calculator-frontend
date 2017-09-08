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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.MinimumEarningsForm
import uk.gov.hmrc.childcarecalculatorfrontend.utils.HelperManager
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.minimumEarning
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

class MinimumEarningsSpec extends TemplatesValidator with  FakeCCApplication with HelperManager {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(attribute = Some("for"), attributeValue = Some("minimumEarnings-true"), value = "Yes"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("minimumEarnings-false"), value = "No"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = whatsYourAgePath)
  )

  val backURL = Call("GET", whatsYourAgePath)

  var amount = 120.30

  def getTemplate(form: Form[Option[Boolean]], isPartner: Boolean): Document = {
    val template = minimumEarning(form, isPartner, amount, backURL)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  val isPartnerTestCase = Table(
    ("isPartner", "errorMessage", "pageTitle", "hintText", "submitURL"),
    (false, "on.average.how.much.will.you.earn.parent.error", s"On average, will you earn £${amount} or more a week?", "This is the National Minimum Wage or National Living Wage a week for someone your age.", parentMinimumEarningsPath),
    (true, "on.average.how.much.will.you.earn.partner.error", s"On average, will your partner earn £${amount} or more a week?", "This is the National Minimum Wage or National Living Wage a week for someone your partner’s age.", partnerMinimumEarningsPath)
  )


  forAll(isPartnerTestCase) { case (isPartner, errorMessage, pageTitle, hintText, submitURL) =>
    s"calling benefits template when isPartner = ${isPartner}" should {
      "render template" in {
        val template = minimumEarning.render(new MinimumEarningsForm(isPartner, amount, applicationMessagesApi).form, isPartner, amount, backURL, request, applicationMessages)
        template.contentType shouldBe "text/html"

        val template1 = minimumEarning.f(new MinimumEarningsForm(isPartner, amount, applicationMessagesApi).form, isPartner, amount, backURL)(request, applicationMessages)
        template1.contentType shouldBe "text/html"
      }

      val dynamicContent = List(
        ElementDetails(id = Some("page-title"), value = pageTitle),
        ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = hintText)
      )

      val dynamicLinks = List(
        ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = submitURL)
      )

      "display correct content" when {
        "nothing is selected initially" in {
          implicit val doc: Document = getTemplate(new MinimumEarningsForm(isPartner, amount, applicationMessagesApi).form.fill(None), isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks()
          verifyErrors()
        }

        "true is selected" in {
          implicit val doc: Document = getTemplate(new MinimumEarningsForm(isPartner, amount, applicationMessagesApi).form.fill(Some(true)), isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks(
            List("minimumEarnings-true")
          )
          verifyErrors()
        }

        "false is selected" in {
          implicit val doc: Document = getTemplate(new MinimumEarningsForm(isPartner, amount, applicationMessagesApi).form.fill(Some(false)), isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks(
            List("minimumEarnings-false")
          )
          verifyErrors()
        }

        s"display ${applicationMessages.messages(errorMessage, amount)} form is submitted without data" in {
          val form = new MinimumEarningsForm(isPartner, amount, applicationMessagesApi).form.bind(
            Map(
              "minimumEarnings" -> ""
            )
          )
          implicit val doc: Document = getTemplate(form, isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks()
          verifyErrors(
            errors = Map("minimumEarnings" -> applicationMessages.messages(errorMessage, amount)),
            validDateInlineErrors = false
          )
          applicationMessages.messages(errorMessage) should not be errorMessage
        }
      }
    }
  }
}
