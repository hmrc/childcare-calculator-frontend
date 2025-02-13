/*
 * Copyright 2025 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefits

object DoYouGetAnyBenefitsForm {

  val formId: String = "doYouGetAnyBenefits"

  def apply(): Form[Set[ParentsBenefits]] = Form(
    single(
      formId -> set(text)
        .verifying("doYouGetAnyBenefits.error.select", _.forall(ParentsBenefits.mapping.keySet.contains _))
        .transform[Set[ParentsBenefits]](_.map(ParentsBenefits.mapping), _.map(ParentsBenefits.inverseMappping))
        .verifying("doYouGetAnyBenefits.error.select", _.nonEmpty)
        .verifying(
          "doYouGetAnyBenefits.error.select",
          set => !(set.contains(ParentsBenefits.NoneOfThese) && set.size > 1)
        )
    )
  )

}
