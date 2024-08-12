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

package uk.gov.hmrc.childcarecalculatorfrontend.connectors

import play.api.libs.json.Json

import javax.inject.Inject
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.models.SchemeResults
import uk.gov.hmrc.childcarecalculatorfrontend.models.integration.Household
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.client.HttpClientV2

import scala.concurrent.{ExecutionContext, Future}

class EligibilityConnector @Inject()(appConfig: FrontendAppConfig, http: HttpClientV2)(implicit ec: ExecutionContext) {

  def getEligibility(eligibilityInput: Household)(implicit headerCarrier: HeaderCarrier): Future[SchemeResults] = {
    http.post(url"${appConfig.eligibilityUrl}")
      .withBody(Json.toJson(eligibilityInput))
      .execute[SchemeResults]
  }

}
