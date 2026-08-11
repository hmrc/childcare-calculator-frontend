/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.models.enums

import uk.gov.hmrc.childcarecalculatorfrontend.utils.EnumFormat

enum YesNoNotYet(override val toString: String) {
  case Yes    extends YesNoNotYet("yes")
  case No     extends YesNoNotYet("no")
  case NotYet extends YesNoNotYet("notYet")
}

object YesNoNotYet extends EnumFormat[YesNoNotYet] {

  def fromBoolean(boolean: Boolean): YesNoNotYet =
    if (boolean) {
      Yes
    } else {
      No
    }

  def fromOptionalBoolean(optionalBoolean: Option[Boolean]): YesNoNotYet =
    optionalBoolean match {
      case Some(boolean) => fromBoolean(boolean)
      case None          => NotYet
    }

}
