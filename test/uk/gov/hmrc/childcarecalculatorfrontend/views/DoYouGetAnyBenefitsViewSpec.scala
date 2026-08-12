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
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefit
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefit.*
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{NewCheckboxViewBehaviours, NewViewBehaviours}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.doYouGetAnyBenefits

class DoYouGetAnyBenefitsViewSpec extends NewViewBehaviours with NewCheckboxViewBehaviours[ParentsBenefit] {

  override val form: Form[Set[ParentsBenefit]] = DoYouGetAnyBenefitsForm()
  val view: doYouGetAnyBenefits                = inject[doYouGetAnyBenefits]
  override val messageKeyPrefix                = "doYouGetAnyBenefits"
  override val fieldKey: String                = DoYouGetAnyBenefitsForm.formId
  override val errorMessage                    = s"$messageKeyPrefix.error.select"

  override val values: Seq[(String, ParentsBenefit)] =
    Seq(
      (s"$messageKeyPrefix.$CarersAllowance", CarersAllowance),
      (s"$messageKeyPrefix.$CarersCredit", CarersCredit),
      (
        s"$messageKeyPrefix.$ContributionBasedEmploymentAndSupportAllowance",
        ContributionBasedEmploymentAndSupportAllowance
      ),
      (s"$messageKeyPrefix.$IncapacityBenefit", IncapacityBenefit),
      (
        s"$messageKeyPrefix.$NICreditsForIncapacityOrLimitedCapabilityForWork",
        NICreditsForIncapacityOrLimitedCapabilityForWork
      ),
      (s"$messageKeyPrefix.$SevereDisablementAllowance", SevereDisablementAllowance),
      (s"$messageKeyPrefix.$NoneOfThese", NoneOfThese)
    )

  override def render(form: Form[Set[ParentsBenefit]] = form): Html =
    view(form)(using fakeRequest, messages)

  "DoYouGetAnyBenefits view" must {
    behave.like(normalPage(render, messageKeyPrefix))

    behave.like(pageWithBackLink(render))

    behave.like(checkboxPage(divider = true))

    "display correct content when loaded" in {
      val view = render()
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.select.all"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.or"))
    }
  }

}
