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
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.VouchersForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoUnsureEnum
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.vouchers
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table

class VouchersSpec extends TemplatesValidator with FakeCCApplication {
  val backUrl: Call = Call("GET", hoursParentPath)

  override val contentData: List[ElementDetails] = List(
    ElementDetails(attribute = Some("for"), attributeValue = Some("vouchers-yes"), value = "Yes"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("vouchers-no"), value = "No"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("vouchers-notsure"), value = "Not sure"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = vouchersPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = hoursParentPath)
  )

  def getTemplate(form: Form[Option[String]], hasPartner: Boolean): Document = {
    val template = vouchers(form, hasPartner, backUrl)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  val users = Table(
    ("Has partner", "Page title", "Error message key"),
    (false, "Does your employer offer childcare vouchers?", "vouchers.not.selected.error.single"),
    (true, "Do either of your employers offer childcare vouchers?", "vouchers.not.selected.error.couple")
  )

  forAll(users) { case (hasPartner, pageTitle, errorMessageKey) => {

    val dynamicContent: List[ElementDetails] = List(
      ElementDetails(id = Some("page-title"), value = pageTitle)
    )

    s"calling vouchers template if user has partner = ${hasPartner}" should {

      "render template successfully" in {
        val template = vouchers.render(new VouchersForm(hasPartner, applicationMessagesApi).form, hasPartner, backUrl, request, applicationMessages)
        template.contentType shouldBe "text/html"

        val template1 = vouchers.f(new VouchersForm(hasPartner, applicationMessagesApi).form, hasPartner, backUrl)(request, applicationMessages)
        template1.contentType shouldBe "text/html"
      }

      "display correct content" when {
        "nothing is selected initially" in {
          implicit val doc: Document = getTemplate(new VouchersForm(hasPartner, applicationMessagesApi).form.fill(None), hasPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks()
          verifyChecks()
          verifyErrors()
        }

        YesNoUnsureEnum.values.foreach { yesNoUnsure => {
          val yesNoUnsureValue = yesNoUnsure.toString
          s"${yesNoUnsureValue} is selected" in {
            implicit val doc: Document = getTemplate(new VouchersForm(hasPartner, applicationMessagesApi).form.fill(Some(yesNoUnsureValue)), hasPartner)

            verifyPageContent(dynamicContent)
            verifyPageLinks()
            verifyChecks(List(s"${vouchersKey}-${yesNoUnsureValue}"))
            verifyErrors()
          }
        }}

        s"display error '${applicationMessages.messages(errorMessageKey)}'" when {
          val invalidData: List[String] = List("", "123", "abcd", "[*]")
          invalidData.foreach { invalidValue =>
            s"form is submitted with invalid data '${invalidValue}'" in {
              val form = new VouchersForm(hasPartner, applicationMessagesApi).form.bind(
                Map(
                  vouchersKey -> invalidValue
                )
              )
              implicit val doc: Document = getTemplate(form, hasPartner)

              verifyPageContent(dynamicContent)
              verifyPageLinks()
              verifyChecks()
              verifyErrors(
                errors = Map(vouchersKey -> applicationMessages.messages(errorMessageKey))
              )
              applicationMessages.messages(errorMessageKey) should not be errorMessageKey
            }
          }
        }
      }
    }
  }}

}
