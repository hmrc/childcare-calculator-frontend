/*
 * Copyright 2019 HM Revenue & Customs
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
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichBenefitsYouGetForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{CheckboxViewBehaviours, ViewBehaviours}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichBenefitsYouGet

class WhichBenefitsYouGetViewSpec extends ViewBehaviours with CheckboxViewBehaviours[String] {

  val messageKeyPrefix = "whichBenefitsYouGet"
  val fieldKey = "value"
  val errorMessage = "error.invalid"

  val values: Map[String, String] = WhichBenefitsYouGetForm.options

  def form: Form[Set[String]] = WhichBenefitsYouGetForm()

  def createView(form: Form[Set[String]] = form): Html =
    whichBenefitsYouGet(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "WhichBenefitsYouGet view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like checkboxPage()
  }


  "whichBenefitsYouGet view " must {
    s"display correct content when loaded" in {
      val view = whichBenefitsYouGet(frontendAppConfig, form, NormalMode)(fakeRequest, messages)
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.income.benefits"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.income.support"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.jobseeker.allowance"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.employer.support.allowance"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.pension.credit"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.disability.benefits"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.disability.benefit.living.allowance"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.disability.benefit.attendance.allowance"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.disability.benefit.personal.allowance"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.higher.rate.benefits"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.higher.rate.benefit.living.allowance"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.higher.rate.benefit.attendance.allowance"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.higher.rate.benefit.personal.payment"))
      assertContainsText(asDocument(view), messagesApi("whichBenefitsList.typeof.higher.rate.benefit.independent.payment"))
    }
  }
}
