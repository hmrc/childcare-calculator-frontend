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

package uk.gov.hmrc.childcarecalculatorfrontend.connectors

import uk.gov.hmrc.play.config.ServicesConfig
import uk.gov.hmrc.childcarecalculatorfrontend.config.{FrontendAppConfig, WSHttp}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Household, SchemeResults}
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

object EligibilityConnector extends EligibilityConnector with ServicesConfig {
  override def httpPost: WSHttp = WSHttp
}

trait EligibilityConnector {

  def httpPost: WSHttp

  def postUrl(key: String): String = s"""${key}"""

  def getEligibility(eligibilityInput: Household)(implicit headerCarrier: HeaderCarrier): Future[SchemeResults] = {
    httpPost.POST[Household, SchemeResults](postUrl(FrontendAppConfig.eligibilityUrl), eligibilityInput)
  }

}
