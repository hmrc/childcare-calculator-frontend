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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.{Form, FormError}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.behaviours.CheckboxBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildAgeGroup

class ChildrenAgeGroupsFormSpec extends CheckboxBehaviours[ChildAgeGroup] {

  override val form: Form[Set[ChildAgeGroup]] = ChildrenAgeGroupsForm()

  override val validOptions: Set[ChildAgeGroup] = ChildAgeGroup.inverseMappping.keySet

  override val fieldName = "childrenAgeGroups"

  "ChildrenAgeGroupsForm form" must {
    behave like aCheckboxForm(invalid = "childrenAgeGroups.error.select")

    behave like aMandatoryCheckboxForm(required = "childrenAgeGroups.error.select")

    "fail to bind when multiple options are selected along with 'No'" in {
      val data = Map(
        s"$fieldName[0]" -> ChildAgeGroup.noneOfThese,
        s"$fieldName[1]" -> ChildAgeGroup.twoYears
      )
      form.bind(data).errors mustBe Seq(FormError(fieldName, "childrenAgeGroups.error.exclusive"))
    }
  }
}
