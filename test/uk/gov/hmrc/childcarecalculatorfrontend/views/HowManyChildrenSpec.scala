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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.HowManyChildrenForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.howManyChildren
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

class HowManyChildrenSpec extends TemplatesValidator with FakeCCApplication {

  val backUrl: Call = Call("GET", underConstructionPath)

  override val contentData: List[ElementDetails] = List(
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = underConstructionPath)
  )

  def getTemplate(form: Form[Option[Int]]): Document = {
    val template = howManyChildren(form, backUrl)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  "render template successfully" in {
    val template = howManyChildren.render(new HowManyChildrenForm(applicationMessagesApi).form, backUrl, request, applicationMessages)
    template.contentType shouldBe "text/html"

    val template1 = howManyChildren.f(new HowManyChildrenForm(applicationMessagesApi).form, backUrl)(request, applicationMessages)
    template1.contentType shouldBe "text/html"
  }

  "load template successfully" when {
    "nothing is selected initially" in {
      implicit val doc: Document = getTemplate(new HowManyChildrenForm(applicationMessagesApi).form)
      verifyErrors()
    }

    "valid value is given" in {
      implicit val doc: Document = getTemplate(new HowManyChildrenForm(applicationMessagesApi).form.fill(Some(4)))
      verifyErrors()
    }
  }
  "display correct error message" when {
    s"form is submitted without data ('${applicationMessages.messages("number.of.children.not.selected.error")}')" in {
      val form = new HowManyChildrenForm(applicationMessagesApi).form.bind(
        Map(
          howManyChildrenKey -> ""
        )
      )
      implicit val doc: Document = getTemplate(form)

      verifyErrors(
        errors = Map(howManyChildrenKey -> applicationMessages.messages("number.of.children.not.selected.error"))
      )
      applicationMessages.messages("number.of.children.not.selected.error") should not be "number.of.children.not.selected.error"
    }
  }

  s"form is submitted with data form invalid type ('${applicationMessages.messages("error.real")}')" ignore {

    val invalidValues: List[String] = List("abcs", "37,55", "[*]")
    invalidValues.foreach { children =>
      s"${children} is given" in {
        val form = new HowManyChildrenForm(applicationMessagesApi).form.bind(
          Map(
            howManyChildrenKey -> children
          )
        )
        implicit val doc: Document = getTemplate(form)

        verifyErrors(
          errors = Map(howManyChildrenKey -> applicationMessages.messages("error.real"))
        )
        applicationMessages.messages("error.real") should not be "error.real"
      }
    }
  }

}
