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
import uk.gov.hmrc.childcarecalculatorfrontend.TemplatesValidator.ElementDetails
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{HoursForm, SelfEmployedOrApprenticeForm}
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentStatusEnum
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.{hours, selfEmployedOrApprentice}
import uk.gov.hmrc.childcarecalculatorfrontend.{TestDataForViews, FakeCCApplication, TemplatesValidator}

class SelfEmployedOrApprenticeSpec extends TemplatesValidator with FakeCCApplication with TestDataForViews{

  lazy val backUrlForParent: Call = Call("GET", parentMinimumEarningsPath)
  lazy val backUrlForPartner: Call = Call("GET", partnerMinimumEarningsPath)

  lazy val pageTitleContentParent = "Are you self-employed or an apprentice?"
  lazy val pageTitleContentPartner = "Is your partner an apprentice or self-employed?"

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some(nextButtonId), value = nextButtonLabel),
    ElementDetails(id = Some(backButtonId), value = backButtonLabel)
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = locationPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = whatYouNeedPath)
  )

  def getTemplate(form: Form[Option[String]], isPartner: Boolean = false, backUrl: Call): Document = {
    val template = selfEmployedOrApprentice(form, isPartner, backUrl)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  private def getNewForm(isPartner: Boolean = false) = {
    new SelfEmployedOrApprenticeForm(isPartner, applicationMessagesApi)
  }

  val testCases = Table(
    ("Is partner", "Submission path", "Page title"),
    (false, selfEmployedTimescaleParentPath, pageTitleContentParent),
    (true, selfEmployedTimescalePartnerPath, pageTitleContentPartner)
  )

  forAll(testCases) {
   case (isPartner, submissionPath, pageTitle) =>

    val dynamicContent = List(
      ElementDetails(id = Some(pageTitleId), value = pageTitle)
    )

    val dynamicLinks = List(
      ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = submissionPath),
      ElementDetails(id = Some(backButtonId), checkAttribute = Some(attributeHref), value = whatYouNeedPath)
    )

    s"if user is partner = $isPartner" should {

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

        s"form is submitted with data form invalid range ('${applicationMessages.messages("hours.a.week.not.selected.error")}')" when {

          val invalidValues: List[String] = List("0.9", "99.6")
          invalidValues.foreach { hours =>
            s"$hours is given" in {
              val form = new HoursForm(applicationMessagesApi).form.bind(
                Map(
                  hoursKey -> hours
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



 /* "calling location template" should {

    "render template for parent" in {
      val template = selfEmployedOrApprentice.render(getNewForm().form, false, backUrlForParent, request, applicationMessages)
      template.contentType shouldBe "text/html"

      val template1 = selfEmployedOrApprentice.render(getNewForm().form, false, backUrlForParent)(request, applicationMessages)
      template1.contentType shouldBe "text/html"
    }

    "display correct content for parent" when {
      "nothing is selected initially" in {
        implicit val doc: Document = getTemplate(getNewForm().form.fill(None), isPartner = false, backUrlForParent)

        verifyPageContent()
        verifyPageLinks()
        verifyChecks()
        verifyErrors()
      }

      EmploymentStatusEnum.values.foreach { empStatus => {
        val empStatusValue = empStatus.toString
        s"$empStatusValue is selected" in {
          implicit val doc: Document = getTemplate(getNewForm().form.fill(Some(empStatusValue)), isPartner = false, backUrlForParent)

          verifyPageContent()
          verifyPageLinks()
          verifyChecks(List(s"$selfEmployedOrApprenticeKey-$empStatusValue"))
          verifyErrors()
        }
      }}

      "form is submitted without data" in {
        val form = getNewForm().form.bind(
          Map(
            selfEmployedOrApprenticeKey -> ""
          )
        )
        implicit val doc: Document = getTemplate(form, isPartner = false, backUrlForParent)

        verifyPageContent()
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map(selfEmployedOrApprenticeKey -> "You must tell the calculator where you live")
        )
      }
    }
  }*/
}
