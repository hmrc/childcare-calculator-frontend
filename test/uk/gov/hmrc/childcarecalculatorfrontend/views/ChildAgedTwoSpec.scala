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
import play.api.i18n.Messages.Implicits._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.{childAgedTwo, whatYouNeed}
import uk.gov.hmrc.childcarecalculatorfrontend.{CCRoutes, FakeCCApplication, TemplatesValidator}
import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildAgedTwoForm

class ChildAgedTwoSpec extends TemplatesValidator with FakeCCApplication with CCRoutes {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "Do you have a child aged 2?"),
    ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = "Some 2 year olds in England could be entitled to 15 hours of free early education and childcare a week in term time (570 hours a year)."),
    ElementDetails(attribute = Some("for"), attributeValue = Some("childAgedTwo-true"), value = "Yes"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("childAgedTwo-false"), value = "No"),
    ElementDetails(attribute = Some("type"), attributeValue = Some("submit"), checkAttribute = Some("title"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = childAgedTwoPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = locationPath)
  )

  def getTemplate(form: Form[Option[Boolean]]): Document = {
    val template = childAgedTwo(form)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "calling ChildAgedTwo template" should {

    "render template" in {
      val template = childAgedTwo.render(new ChildAgedTwoForm(applicationMessagesApi).form, request, applicationMessages)
      template.contentType shouldBe "text/html"

      val template1 = childAgedTwo.f(new ChildAgedTwoForm(applicationMessagesApi).form)(request, applicationMessages)
      template1.contentType shouldBe "text/html"
    }

    "display correct content" when {
      "nothing is selected initially" in {
        implicit val doc: Document = getTemplate(new ChildAgedTwoForm(applicationMessagesApi).form)

        verifyPageContent()
        verifyPageLinks()
        verifyErrors()
      }

      "true is selected" in {
        implicit val doc: Document = getTemplate(new ChildAgedTwoForm(applicationMessagesApi).form.fill(Some(true)))

        verifyPageContent()
        verifyPageLinks()
        verifyChecks(Some(List(s"${childAgedTwoKey}-true")))
        verifyErrors()
      }

      "false is selected" in {
        implicit val doc: Document = getTemplate(new ChildAgedTwoForm(applicationMessagesApi).form.fill(Some(false)))

        verifyPageContent()
        verifyPageLinks()
        verifyChecks(Some(List(s"${childAgedTwoKey}-false")))
        verifyErrors()
      }

      "form is submitted without data" in {
        val form = new ChildAgedTwoForm(applicationMessagesApi).form.bind(
          Map(
            childAgedTwoKey -> ""
          )
        )
        implicit val doc: Document = getTemplate(form)

        verifyPageContent()
        verifyPageLinks()
        verifyErrors(
          errorTitle = Some("There is a problem"),
          errorHeading = Some("Check you have answered the question correctly"),
          errors = Map("childAgedTwo" -> applicationMessages.messages("child.aged.two.yes.no.not.selected.error"))
        )
      }

    }

  }

}
