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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.MaximumEarningsForm
import uk.gov.hmrc.childcarecalculatorfrontend.utils.HelperManager
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.maximumEarnings
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

/**
 * Created by user on 07/09/17.
 */
class MaximumEarningsSpec extends TemplatesValidator with  FakeCCApplication with HelperManager {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(attribute = Some("for"), attributeValue = Some("maximumEarnings-true"), value = "Yes"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("maximumEarnings-false"), value = "No"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = minimumEarningsParentPath)
  )

  val backURL = Call("GET", minimumEarningsParentPath)


  def getTemplate(form: Form[Option[Boolean]], youPartnerBoth : String): Document = {
    val template = maximumEarnings(form, youPartnerBoth, backURL)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  val isPartnerTestCase = Table(
    ("youPartnerBoth", "errorMessage", "pageTitle", "hintText", "submitURL"),
    ("YOU", "maximum.earning.error.YOU", "Will you earn more than £100,000 a year?",
      "This is with any pension and gift aid contributions taken off.", maximumEarningsParentPath),
    ("PARTNER", "maximum.earning.error.PARTNER", "Will your partner earn more than £100,000 a year?",
      "This is with any pension and gift aid contributions taken off.", maximumEarningsPartnerPath),
    ("BOTH", "maximum.earning.error.BOTH", "Will either of you earn more than £100,000 a year?",
      "This is with any pension and gift aid contributions taken off.", maximumEarningsPath)
  )


  forAll(isPartnerTestCase) { case (youPartnerBoth, errorMessage, pageTitle, hintText, submitURL) =>
    s"calling maximum earning template when its = ${youPartnerBoth}" should {
      "render template" in {
        val template = maximumEarnings.render(new MaximumEarningsForm(youPartnerBoth, applicationMessagesApi).form,
          youPartnerBoth,
          backURL,
          request,
          applicationMessages)
        template.contentType shouldBe "text/html"

        val template1 = maximumEarnings.f(new MaximumEarningsForm(youPartnerBoth, applicationMessagesApi).form,
          youPartnerBoth, backURL)(request, applicationMessages)
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
          implicit val doc: Document = getTemplate(new MaximumEarningsForm(youPartnerBoth, applicationMessagesApi).form.fill(None),
                                                   youPartnerBoth)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks()
          verifyErrors()
        }

     "true is selected" in {
          implicit val doc: Document = getTemplate(new MaximumEarningsForm(youPartnerBoth, applicationMessagesApi).form.fill(Some(true)),
                                                   youPartnerBoth)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks(
            List("maximumEarnings-true")
          )
          verifyErrors()
        }

        "false is selected" in {
          implicit val doc: Document = getTemplate(new MaximumEarningsForm(youPartnerBoth, applicationMessagesApi).form.fill(Some(false)),
                                                   youPartnerBoth)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks(
            List("maximumEarnings-false")
          )
          verifyErrors()
        }

        s"display ${applicationMessages.messages(errorMessage)} form is submitted without data" in {
          val form = new MaximumEarningsForm(youPartnerBoth, applicationMessagesApi).form.bind(
            Map(
              "maximumEarnings" -> ""
            )
          )
          implicit val doc: Document = getTemplate(form, youPartnerBoth)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks()
          verifyErrors(
            errors = Map("maximumEarnings" -> applicationMessages.messages(errorMessage)),
            validDateInlineErrors = false
          )
          applicationMessages.messages(errorMessage) should not be errorMessage
        }
      }
    }
  }
}
