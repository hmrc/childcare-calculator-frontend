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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import play.api.libs.json.{Format, Reads}
import uk.gov.hmrc.childcarecalculatorfrontend.config.CCSessionCache
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CCConstants
import uk.gov.hmrc.http.cache.client.SessionCache
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object KeystoreService extends KeystoreService {
  val sessionCache: SessionCache = CCSessionCache
}

trait KeystoreService extends CCConstants {
  val sessionCache: SessionCache

  def fetch[PageObjects]()(implicit hc: HeaderCarrier, rds: Reads[PageObjects]): Future[Option[PageObjects]] = {
    sessionCache.fetchAndGetEntry[PageObjects](pageObjectsKey)
  }

  def cache[PageObjects](data: PageObjects)(implicit hc: HeaderCarrier, formats: Format[PageObjects]): Future[Option[PageObjects]] = {
    sessionCache.cache[PageObjects](pageObjectsKey, data) map {
      _.getEntry[PageObjects](pageObjectsKey)
    }
  }

}
