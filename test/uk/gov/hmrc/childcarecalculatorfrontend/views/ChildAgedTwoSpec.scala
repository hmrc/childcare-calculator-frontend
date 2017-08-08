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
import play.api.i18n.Messages.Implicits._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childAgedTwo
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}
import play.api.data.Form
import play.api.i18n.Messages
import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildAgedTwoForm

class ChildAgedTwoSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "Do you have a child aged 2?"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("childAgedTwo-true"), value = "Yes"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("childAgedTwo-false"), value = "No"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = childAgedTwoPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = locationPath)
  )

  val backUrl: Call = Call("GET", locationPath)

  val infoContent = Table(
    ("Location", "Info text"),
    (LocationEnum.ENGLAND, "Some 2 year olds in England could be entitled to 15 hours of free early education and childcare a week in term time (570 hours a year)."),
    (LocationEnum.SCOTLAND, "Some 2 year olds in Scotland could be entitled to 16 hours of free early learning and childcare a week in term time (600 hours a year)."),
    (LocationEnum.WALES, "Some 2 year olds in Wales could be entitled to 12 and a half hours of free early education a week in term time.")
  )

  def getTemplate(form: Form[Option[Boolean]], location: LocationEnum): Document = {
    val template = childAgedTwo(form, backUrl, location)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "calling ChildAgedTwo template" should {

    "render template" in {
      val template = childAgedTwo.render(new ChildAgedTwoForm(applicationMessagesApi).form, backUrl, LocationEnum.ENGLAND, request, applicationMessages)
      template.contentType shouldBe "text/html"

      val template1 = childAgedTwo.f(new ChildAgedTwoForm(applicationMessagesApi).form, backUrl, LocationEnum.ENGLAND)(request, applicationMessages)
      template1.contentType shouldBe "text/html"
    }

    forAll(infoContent) { case (location, infoText) => {
      val dynamicContent = List(
        ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = infoText)
      )

      s"display correct content for location ${location.toString}" when {
        "nothing is selected initially" in {
          implicit val doc: Document = getTemplate(new ChildAgedTwoForm(applicationMessagesApi).form, location)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks()
          verifyErrors()
        }

        "true is selected" in {
          implicit val doc: Document = getTemplate(new ChildAgedTwoForm(applicationMessagesApi).form.fill(Some(true)), location)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks(List(s"${childAgedTwoKey}-true"))
          verifyErrors()
        }

        "false is selected" in {
          implicit val doc: Document = getTemplate(new ChildAgedTwoForm(applicationMessagesApi).form.fill(Some(false)), location)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks(List(s"${childAgedTwoKey}-false"))
          verifyErrors()
        }

        "form is submitted without data" in {
          val form = new ChildAgedTwoForm(applicationMessagesApi).form.bind(
            Map(
              childAgedTwoKey -> ""
            )
          )
          implicit val doc: Document = getTemplate(form, location)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks()
          verifyErrors(
            errors = Map("childAgedTwo" -> applicationMessages.messages("child.aged.two.yes.no.not.selected.error"))
          )
        }

        "form is submitted with invalid data" in {
          val form = new ChildAgedTwoForm(applicationMessagesApi).form.bind(
            Map(
              childAgedTwoKey -> "abcd"
            )
          )
          implicit val doc: Document = getTemplate(form, location)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks()
          verifyErrors(
            errors = Map("childAgedTwo" -> applicationMessages.messages("error.boolean"))
          )
        }
      }}
    }

  }

}
