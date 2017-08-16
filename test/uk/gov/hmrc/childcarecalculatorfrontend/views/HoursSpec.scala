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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.HoursForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.hours
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

class HoursSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = hoursPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = paidEmploymentPath)
  )

  def getTemplate(form: Form[Option[BigDecimal]], hasPartner: Boolean): Document = {
    val template = hours(form, hasPartner)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  val testCases = Table(
    ("Has partner", "Page title", "Error message"),
    (false, "How many hours a week do you usually work?", "hours.a.week.empty"),
    (true, "How many hours a week does your partner usually work?", "hours.a.week.empty")
  )

  forAll(testCases) { case (hasPartner, pageTitle, errorMessage) =>
    val dynamicContent = List(
      ElementDetails(id = Some("page-title"), value = pageTitle)
    )
    s"if user has partner = ${hasPartner}" should {

      "render template" in {
        val template = hours.render(new HoursForm(hasPartner, applicationMessagesApi).form, hasPartner, request, applicationMessages)
        template.contentType shouldBe "text/html"

        val template1 = hours.f(new HoursForm(hasPartner, applicationMessagesApi).form, hasPartner)(request, applicationMessages)
        template1.contentType shouldBe "text/html"
      }

      "nothing is selected initially" in {
        implicit val doc: Document = getTemplate(new HoursForm(hasPartner, applicationMessagesApi).form, hasPartner)

        verifyPageContent(dynamicContent)
        verifyPageLinks()
        verifyChecks()
        verifyErrors()
      }

//      "true is selected" in {
//        implicit val doc: Document = getTemplate(new HoursForm(hasPartner, applicationMessagesApi).form.fill(Some(99.5)), hasPartner)
//
//        verifyPageContent(dynamicContent ++
//          List(
//            ElementDetails(id = Some("span"), value = applicationMessages.messages(s"hours.a.week.hint.text.couple"))
//          )
//        )
//        verifyPageLinks()
//        verifyChecks()
//        verifyErrors()
//      }
//
//      "false is selected" in {
//        implicit val doc: Document = getTemplate(new HoursForm(hasPartner, applicationMessagesApi).form.fill(Some(12)), hasPartner)
//
//        verifyPageContent(dynamicContent ++
//          List(
//            ElementDetails(id = Some("span"), value = applicationMessages.messages(s"hours.a.week.hint.text.single"))
//          )
//        )
//        verifyPageLinks()
//        verifyChecks()
//        verifyErrors()
//      }

      "form is submitted without data" in {
        val form = new HoursForm(hasPartner, applicationMessagesApi).form.bind(
          Map(
            hoursKey -> ""
          )
        )
        implicit val doc: Document = getTemplate(form, hasPartner)

        verifyPageContent(dynamicContent)
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map(hoursKey -> applicationMessages.messages(errorMessage))
        )
        applicationMessages.messages(errorMessage) should not be errorMessage
      }

    }

  }
}
