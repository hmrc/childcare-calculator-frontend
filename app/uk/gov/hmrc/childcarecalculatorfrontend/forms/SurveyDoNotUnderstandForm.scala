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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formatter

object SurveyDoNotUnderstandForm extends FormErrorHelper {

  def surveyDoNotUnderstandFormatter(errorKeyBlank: String, errorKeyInvalid: String) = new Formatter[String] {



    def bind(key: String, data: Map[String, String]) = {
      data.get(key) match {
        case None => produceError(key, errorKeyBlank)
        case Some("") => produceError(key, errorKeyBlank)
        case Some(s) => Right(s)
        case _ => produceError(key, errorKeyInvalid)
      }
    }

    def unbind(key: String, value: String) = Map(key -> value.toString)
  }

  def apply(errorKeyBlank: String = "surveyDoNotUnderstand.error.notCompleted", errorKeyInvalid: String = "error.bigDecimal"): Form[String] =
    Form(single("value" -> of(surveyDoNotUnderstandFormatter(errorKeyBlank, errorKeyInvalid))))
}
