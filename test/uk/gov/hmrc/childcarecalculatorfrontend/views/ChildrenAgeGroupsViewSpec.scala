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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ChildrenAgeGroupsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildAgeGroup
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{NewCheckboxViewBehaviours, NewViewBehaviours}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childrenAgeGroups

class ChildrenAgeGroupsViewSpec extends NewViewBehaviours with NewCheckboxViewBehaviours[ChildAgeGroup] {

  override val form: Form[Set[ChildAgeGroup]] = ChildrenAgeGroupsForm()
  val view: childrenAgeGroups                 = inject[childrenAgeGroups]
  override val messageKeyPrefix               = "childrenAgeGroups"
  override val fieldKey: String               = ChildrenAgeGroupsForm.formId
  override val errorMessage                   = s"$messageKeyPrefix.error.select"

  override val values: Seq[(String, ChildAgeGroup)] =
    Seq(
      (s"$messageKeyPrefix.${ChildAgeGroup.NineTo23Months}", ChildAgeGroup.NineTo23Months),
      (s"$messageKeyPrefix.${ChildAgeGroup.TwoYears}", ChildAgeGroup.TwoYears),
      (s"$messageKeyPrefix.${ChildAgeGroup.ThreeYears}", ChildAgeGroup.ThreeYears),
      (s"$messageKeyPrefix.${ChildAgeGroup.FourYears}", ChildAgeGroup.FourYears),
      (s"$messageKeyPrefix.${ChildAgeGroup.NoneOfThese}", ChildAgeGroup.NoneOfThese)
    )

  override def render(form: Form[Set[ChildAgeGroup]] = form): Html =
    view(form)(using fakeRequest, messages)

  "ChildrenAgeGroupsView" must {
    behave.like(normalPage(render, messageKeyPrefix))

    behave.like(pageWithBackLink(render))

    behave.like(checkboxPage(divider = true))

    "display correct content when loaded" in {
      val page     = view(form)(using fakeRequest, messages)
      val document = asDocument(page)

      assertContainsText(document, messages(s"$messageKeyPrefix.hint"))
      assertContainsText(document, messages(s"$messageKeyPrefix.or"))
    }
  }

}
