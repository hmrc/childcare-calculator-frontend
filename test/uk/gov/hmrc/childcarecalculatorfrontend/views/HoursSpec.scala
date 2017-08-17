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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.HoursForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.hours
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

class HoursSpec extends TemplatesValidator with FakeCCApplication {

  val backUrl: Call = Call("GET", whoIsInPaidEmploymentPath)

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = whoIsInPaidEmploymentPath)
  )


  def getTemplate(form: Form[Option[BigDecimal]], isPartner: Boolean): Document = {
    val template = hours(form, isPartner, backUrl)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  val testCases = Table(
    ("Is partner", "Submission path", "Page title", "Hint text"),
    (false, hoursParentPath, "How many hours a week do you usually work?", "This is the hours worked in all your paid jobs, ’zero hours’ contracts and self-employment. If you’re on maternity, paternity, adoption or sick leave, it’s your usual hours before you went off work."),
    (true, hoursPartnerPath, "How many hours a week does your partner usually work?", "This is the hours worked in all their paid jobs, ’zero hours’ contracts and self-employment. If they’re on maternity, paternity, adoption or sick leave, it’s their usual hours before they went off work.")
  )

  forAll(testCases) { case (isPartner, submissionPath, pageTitle, hintText) =>
    val dynamicContent = List(
      ElementDetails(id = Some("page-title"), value = pageTitle),
      ElementDetails(elementClass = Some("form-hint"), tagIndex = Some(0), value = hintText)
    )

    val dynamicLinks = List(
      ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = submissionPath)
    )

    s"if user is partner = ${isPartner}" should {

      "render template successfully" in {
        val template = hours.render(new HoursForm(applicationMessagesApi).form, isPartner, backUrl, request, applicationMessages)
        template.contentType shouldBe "text/html"

        val template1 = hours.f(new HoursForm(applicationMessagesApi).form, isPartner, backUrl)(request, applicationMessages)
        template1.contentType shouldBe "text/html"
      }

      "load template successfully" when {
        "nothing is selected initially" in {
          implicit val doc: Document = getTemplate(new HoursForm(applicationMessagesApi).form, isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyErrors()
        }

        "valid value is given" in {
          implicit val doc: Document = getTemplate(new HoursForm(applicationMessagesApi).form.fill(Some(37.5)), isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyErrors()
        }
      }

      "display correct error message" when {
        s"form is submitted without data ('${applicationMessages.messages("hours.a.week.not.selected.error")}')" in {
          val form = new HoursForm(applicationMessagesApi).form.bind(
            Map(
              hoursKey -> ""
            )
          )
          implicit val doc: Document = getTemplate(form, isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyErrors(
            errors = Map(hoursKey -> applicationMessages.messages("hours.a.week.not.selected.error"))
          )
          applicationMessages.messages("hours.a.week.not.selected.error") should not be "hours.a.week.not.selected.error"
        }

        s"form is submitted with data form invalid range ('${applicationMessages.messages("hours.a.week.invalid.error")}')" when {

          val invalidValues: List[String] = List("0.9", "37.55", "99.6")
          invalidValues.foreach { hours =>
            s"${hours} is given" in {
              val form = new HoursForm(applicationMessagesApi).form.bind(
                Map(
                  hoursKey -> hours
                )
              )
              implicit val doc: Document = getTemplate(form, isPartner)

              verifyPageContent(dynamicContent)
              verifyPageLinks(dynamicLinks)
              verifyErrors(
                errors = Map(hoursKey -> applicationMessages.messages("hours.a.week.invalid.error"))
              )
              applicationMessages.messages("hours.a.week.invalid.error") should not be "hours.a.week.invalid.error"
            }
          }
        }

        s"form is submitted with data form invalid type ('${applicationMessages.messages("error.real")}')" when {

          val invalidValues: List[String] = List("abcs", "37,55", "[*]")
          invalidValues.foreach { hours =>
            s"${hours} is given" in {
              val form = new HoursForm(applicationMessagesApi).form.bind(
                Map(
                  hoursKey -> hours
                )
              )
              implicit val doc: Document = getTemplate(form, isPartner)

              verifyPageContent(dynamicContent)
              verifyPageLinks(dynamicLinks)
              verifyErrors(
                errors = Map(hoursKey -> applicationMessages.messages("error.real"))
              )
              applicationMessages.messages("error.real") should not be "error.real"
            }
          }
        }

      }

    }

  }
}
