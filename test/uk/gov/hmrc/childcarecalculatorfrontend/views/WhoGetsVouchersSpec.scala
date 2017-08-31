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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoGetsVouchersForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoGetsVouchers

/**
 * Created by user on 31/08/17.
 */
class WhoGetsVouchersSpec extends TemplatesValidator with FakeCCApplication {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("page-title"), value = "Which of you is offered childcare vouchers?"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("whoGetsVouchers-you"), value = "You"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("whoGetsVouchers-partner"), value = "Partner"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("whoGetsVouchers-both"), value = "Both"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = whoGetsVouchersPath),
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = vouchersPath)
  )

  def getTemplate(form: Form[Option[String]]): Document = {
    val template = whoGetsVouchers(form)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "calling location template" should {

    "render template" in {
      val template = whoGetsVouchers.render(new WhoGetsVouchersForm(applicationMessagesApi).form, request, applicationMessages)
      template.contentType shouldBe "text/html"

      val template1 = whoGetsVouchers.f(new WhoGetsVouchersForm(applicationMessagesApi).form)(request, applicationMessages)
      template1.contentType shouldBe "text/html"
    }

    "display correct content" when {
      "nothing is selected initially" in {
        implicit val doc: Document = getTemplate(new WhoGetsVouchersForm(applicationMessagesApi).form.fill(None))

        verifyPageContent()
        verifyPageLinks()
        verifyChecks()
        verifyErrors()
      }

      YouPartnerBothEnum.values.foreach { who => {
        val whoValue = who.toString
        s"${whoValue} is selected" in {
          implicit val doc: Document = getTemplate(new WhoGetsVouchersForm(applicationMessagesApi).form.fill(Some(whoValue)))

          verifyPageContent()
          verifyPageLinks()
          verifyChecks(List(s"${whoGetsVouchersKey}-${whoValue}"))
          verifyErrors()
        }
      }}

      "form is submitted without data" in {
        val form = new WhoGetsVouchersForm(applicationMessagesApi).form.bind(
          Map(
            whichOfYouInPaidEmploymentKey -> ""
          )
        )
        implicit val doc: Document = getTemplate(form)

        verifyPageContent()
        verifyPageLinks()
        verifyChecks()
        verifyErrors(
          errors = Map(whoGetsVouchersKey -> "You must tell the calculator which of you is offered childcare vouchers")
        )
      }
    }
  }
}
