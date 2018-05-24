/*
 * Copyright 2018 HM Revenue & Customs
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

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.YesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.youStatutoryPay

class YouStatutoryPayViewSpec extends YesNoViewBehaviours {

  val taxYearInfo = new TaxYearInfo

  val messageKeyPrefix = "youStatutoryPay"

  def createView = () => youStatutoryPay(frontendAppConfig, BooleanForm(), NormalMode, taxYearInfo)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => youStatutoryPay(frontendAppConfig, form, NormalMode, taxYearInfo)(fakeRequest, messages)

  "youStatutoryPay view" must {

    "have the correct banner title" in {
      val doc = asDocument(createView())
      val nav = doc.getElementById("proposition-menu")
      val span = nav.children.first
      span.text mustBe messagesApi("site.service_name")
    }

    "display the correct browser title" in {
      val doc = asDocument(createView())
      assertEqualsValue(doc, "title", messagesApi(s"$messageKeyPrefix.title", taxYearInfo.previousTaxYearStart) + " - " + messagesApi("site.service_name")+" - GOV.UK")
    }

    "display the correct page title" in {
      val doc = asDocument(createView())
      assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", taxYearInfo.previousTaxYearStart)
    }

    "display the correct guidance" in {
      val doc = asDocument(createView())
      for (key <- Seq("guidance", "guidance_extra")) assertContainsText(doc, messages(s"statutoryPay.$key"))
    }

    "display a beta banner" in {
      val doc = asDocument(createView())
      assertRenderedByCssSelector(doc, ".beta-banner")
    }

    "Display HMRC branding" in {
      val doc = asDocument(createView())
      assertRenderedByCssSelector(doc, ".organisation-logo")
    }

    behave like pageWithBackLink(createView)

    behave like yesNoPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.YouStatutoryPayController.onSubmit(NormalMode).url,
      Some(messages(s"$messageKeyPrefix.heading", taxYearInfo.previousTaxYearStart.toString))
    )
  }
}
