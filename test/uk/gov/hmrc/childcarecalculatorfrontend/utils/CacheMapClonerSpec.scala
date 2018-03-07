/*
 * Copyright 2018 HM Revenue & Customs
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

import play.api.libs.json.{JsBoolean, Json}
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.http.cache.client.CacheMap

class CacheMapClonerSpec extends SpecBase {
  "Cache map cloner" should {
    "mirror a cachemap property as boolean" in {
      val data = new CacheMap("id",Map("property1" -> JsBoolean(true)))

      val result = CacheMapCloner.cloneSection(data,Map("property1"->"property2"))

      result.getEntry[Boolean]("property2") mustBe result.getEntry[Boolean]("property1")
    }

    "mirror two boolean property" in {
      val data = new CacheMap("id", Map("property1" -> JsBoolean(true), "property2" -> JsBoolean(true)))

      val result = CacheMapCloner.cloneSection(data, Map("property1"->"property3", "property2"->"property4"))

      result.getEntry[Boolean]("property1") mustBe result.getEntry[Boolean]("property3")
      result.getEntry[Boolean]("property2") mustBe result.getEntry[Boolean]("property4")
    }
  }
}

object CacheMapCloner {
  def cloneSection(data: CacheMap, sectionToClone: Map[String,String]) : CacheMap = {
    sectionToClone.foldLeft(data)((clonedData,sectionToClone) => {
      clonedData.copy(data = clonedData.data + (sectionToClone._2 -> clonedData.data.get(sectionToClone._1).get))
    })
  }
}
