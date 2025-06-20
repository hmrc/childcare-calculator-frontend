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

package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.tfc

import uk.gov.hmrc.childcarecalculatorfrontend.models.ParentsBenefits

sealed trait Household
case class SingleHousehold(parent: Parent)                 extends Household
case class JointHousehold(parent: Parent, partner: Parent) extends Household

case class Parent(
    earnsAboveMinEarnings: Boolean,
    earnsAboveMaxEarnings: Boolean,
    selfEmployed: Boolean,
    apprentice: Boolean,
    benefits: Set[ParentsBenefits]
)
