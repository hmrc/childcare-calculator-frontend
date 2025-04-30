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

import play.api.data.Form
import play.api.data.Forms.{set, single, text}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{ChildAgeGroup, NoneOfThese}

object ChildrenAgeGroupsForm {

  val formId: String = "childrenAgeGroups"

  def apply(): Form[Set[ChildAgeGroup]] = Form(
    single(
      formId -> set(text)
        .verifying("childrenAgeGroups.error.select", _.forall(ChildAgeGroup.mapping.keySet.contains _))
        // technically first .verifying not needed, just a sanity check to avoid exceptions from html tampering
        .transform[Set[ChildAgeGroup]](_.map(ChildAgeGroup.mapping), _.map(ChildAgeGroup.inverseMappping))
        .verifying("childrenAgeGroups.error.select", _.nonEmpty)
        .verifying("childrenAgeGroups.error.exclusive", set => !(set.contains(NoneOfThese) && set.size > 1))
    )
  )

}
