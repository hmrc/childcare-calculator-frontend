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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import play.api.mvc.Call

class Utils {

  def getCall[A](optionalElement: Option[A])(f: PartialFunction[A, Call]): Call =
    optionalElement.flatMap(f.lift).getOrElse(SessionExpiredRouter.route(getClass.getName, "getCall"))

  def formatBigDecimal(value: BigDecimal): String = {
    val valueFormatter = new java.text.DecimalFormat("##,###")
    valueFormatter.format(value)
  }

}
