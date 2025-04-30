/*
 * Copyright 2023 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.DoYouGetAnyBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefits._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, ParentsBenefits}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{NewCheckboxViewBehaviours, NewViewBehaviours}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.doYouGetAnyBenefits

class DoYouGetAnyBenefitsViewSpec extends NewViewBehaviours with NewCheckboxViewBehaviours[ParentsBenefits] {

  override val form    = DoYouGetAnyBenefitsForm()
  val testView         = application.injector.instanceOf[doYouGetAnyBenefits]
  val messageKeyPrefix = "doYouGetAnyBenefits"
  val fieldKey: String = DoYouGetAnyBenefitsForm.formId
  val errorMessage     = s"$messageKeyPrefix.error.select"

  override val values: Seq[(String, String)] =
    Seq(
      (s"$messageKeyPrefix.$CarersAllowance", CarersAllowance.toString),
      (s"$messageKeyPrefix.$CarersCredit", CarersCredit.toString),
      (
        s"$messageKeyPrefix.$ContributionBasedEmploymentAndSupportAllowance",
        ContributionBasedEmploymentAndSupportAllowance.toString
      ),
      (s"$messageKeyPrefix.$IncapacityBenefit", IncapacityBenefit.toString),
      (
        s"$messageKeyPrefix.$NICreditsForIncapacityOrLimitedCapabilityForWork",
        NICreditsForIncapacityOrLimitedCapabilityForWork.toString
      ),
      (s"$messageKeyPrefix.$SevereDisablementAllowance", SevereDisablementAllowance.toString),
      (s"$messageKeyPrefix.or", "divider"),
      (s"$messageKeyPrefix.$NoneOfThese", NoneOfThese.toString)
    )

  override def createView(form: Form[Set[ParentsBenefits]] = form): Html =
    testView(frontendAppConfig, form, NormalMode)(fakeRequest, messages)

  "DoYouGetAnyBenefits view" must {
    behave.like(normalPage(createView, messageKeyPrefix))

    behave.like(pageWithBackLink(createView))

    behave.like(checkboxPage())

    "display correct content when loaded" in {
      val view = createView()
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.select.all"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.or"))
    }
  }

}
