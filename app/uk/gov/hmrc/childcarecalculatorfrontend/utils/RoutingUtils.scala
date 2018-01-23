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

import javax.inject.Inject

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants.{Both, Partner, You}

class RoutingUtils @Inject()(utils: Utils) {
  def basedOnEquality(answer: Option[Boolean])(trueRoute: => Call)(falseRoute: => Call) = {
    utils.getCall(answer) {
      case true => trueRoute
      case false => falseRoute
    }
  }

  def basedOnYouPartnerBoth(answer: Option[String], youRoute: Call, partnerRoute: Call, bothRoute: Call) = {
    utils.getCall(answer) {
      case You => youRoute
      case Partner => partnerRoute
      case Both => bothRoute
    }
  }
}
