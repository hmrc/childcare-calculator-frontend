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
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildAgeGroup._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{ChildAgeGroup, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{NewCheckboxViewBehaviours, NewViewBehaviours}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.childrenAgeGroups

class ChildrenAgeGroupsViewSpec extends NewViewBehaviours with NewCheckboxViewBehaviours[ChildAgeGroup] {

  override val form: Form[Set[ChildAgeGroup]] = ChildrenAgeGroupsForm()
  val mockView: childrenAgeGroups = application.injector.instanceOf[childrenAgeGroups]
  val messageKeyPrefix = "childrenAgeGroups"
  val fieldKey: String = ChildrenAgeGroupsForm.formId
  val errorMessage = s"$messageKeyPrefix.error.select"

  override val values: Seq[(String, String)] =
    Seq(
      (s"$messageKeyPrefix.$nineTo23Months", nineTo23Months),
      (s"$messageKeyPrefix.$twoYears", twoYears),
      (s"$messageKeyPrefix.$threeYears", threeYears),
      (s"$messageKeyPrefix.$fourYears", fourYears),
      (s"$messageKeyPrefix.or", "divider"),
      (s"$messageKeyPrefix.$noneOfThese", noneOfThese)
    )

  override def createView(form: Form[Set[ChildAgeGroup]] = form): Html =
    mockView(form, NormalMode)(fakeRequest, messages)

  "ChildrenAgeGroupsView" must {
    behave like normalPage(() => createView(), messageKeyPrefix)

    behave like pageWithBackLink(() => createView())

    behave like checkboxPage()

    "display correct content when loaded" in {
      val view = mockView(form, NormalMode)(fakeRequest, messages)
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.hint"))
      assertContainsText(asDocument(view), messages(s"$messageKeyPrefix.or"))
    }
  }
}
