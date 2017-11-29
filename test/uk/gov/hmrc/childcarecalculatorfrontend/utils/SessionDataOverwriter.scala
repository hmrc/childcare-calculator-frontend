/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import org.scalatestplus.play.PlaySpec
import play.api.libs.json.{JsNumber, JsString, JsValue}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{ChildcareCostsId, NoOfChildrenId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoNotYetEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoNotYetEnum.YesNoNotYetEnum
import uk.gov.hmrc.http.cache.client.CacheMap

class SessionDataOverwriter extends PlaySpec {

  "Session Data Ovewriter" must {
    "check 'do you have childcare costs?' section" when {
      "do not overwrite when selected anything but NOTYET" in {
        val data = new CacheMap("id", Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString)))
        val result = SessionDataOverwrite.overwrite(data)
        result.data(ChildcareCostsId.toString).as[YesNoNotYetEnum] mustBe YesNoNotYetEnum.YES
      }

      "overwrite when selected NOTYET to YES" in {
        val data = new CacheMap("id", Map(ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.NOTYET.toString)))
        val result = SessionDataOverwrite.overwrite(data)
        result.data(ChildcareCostsId.toString).as[YesNoNotYetEnum] mustBe YesNoNotYetEnum.YES
      }
    }
  }

  object SessionDataOverwrite extends SubCascadeUpsert {
    def overwrite(userSelections: CacheMap): CacheMap = {
      val element: JsValue = userSelections.data(ChildcareCostsId.toString)
      if (element.as[YesNoNotYetEnum] == YesNoNotYetEnum.NOTYET) {
        store(ChildcareCostsId.toString, YesNoNotYetEnum.YES, userSelections)
      }
      else {
        userSelections
      }
    }
  }

}
