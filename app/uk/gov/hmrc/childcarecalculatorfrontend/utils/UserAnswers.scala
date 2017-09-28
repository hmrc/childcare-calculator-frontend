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

import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._

class UserAnswers(val cacheMap: CacheMap) {
  def approvedProvider: Option[String] = cacheMap.getEntry[String](ApprovedProviderId.toString)

  def location: Option[String] = cacheMap.getEntry[String](LocationId.toString)

  def expectChildcareCosts: Option[String] = cacheMap.getEntry[String](ExpectChildcareCostsId.toString)

  def doYouLiveWithPartner: Option[Boolean] = cacheMap.getEntry[Boolean](DoYouLiveWithPartnerId.toString)

  def childAgedTwo: Option[Boolean] = cacheMap.getEntry[Boolean](ChildAgedTwoId.toString)

  def childAgedThreeOrFour: Option[Boolean] = cacheMap.getEntry[Boolean](ChildAgedThreeOrFourId.toString)
}
