/*
 * Copyright 2022 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichBenefitsPartnerGetForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{NewCheckboxViewBehaviours, NewViewBehaviours}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichBenefitsPartnerGet

class WhichBenefitsPartnerGetViewSpec extends NewViewBehaviours with NewCheckboxViewBehaviours[String] {

  override val form = WhichBenefitsPartnerGetForm()
  val mockView = application.injector.instanceOf[whichBenefitsPartnerGet]
  val messageKeyPrefix = "whichBenefitsPartnerGet"
  val fieldKey = "value"
  val errorMessage = "error.invalid"

  val values: Seq[(String, String)] = WhichBenefitsPartnerGetForm.options

  def createView(form: Form[Set[String]] = form): Html =
    mockView(frontendAppConfig, form, NormalMode)(fakeRequest, messages, lang)

  "WhichBenefitsPartnerGet view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like checkboxPage()
  }


  "WhichBenefitsPartnerGet view " must {
    s"display correct content when loaded" in {
      val view = mockView(frontendAppConfig, form, NormalMode)(fakeRequest, messages, lang)
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.income.benefits"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.income.support"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.jobseeker.allowance"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.employer.support.allowance"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.pension.credit"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.disability.benefits"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.disability.benefit.living.allowance"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.disability.benefit.attendance.allowance"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.disability.benefit.personal.allowance"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.higher.rate.benefits"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.higher.rate.benefit.living.allowance"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.higher.rate.benefit.attendance.allowance"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.higher.rate.benefit.personal.payment"))
      assertContainsText(asDocument(view), messages("whichBenefitsList.typeof.higher.rate.benefit.independent.payment"))
    }
  }
}
