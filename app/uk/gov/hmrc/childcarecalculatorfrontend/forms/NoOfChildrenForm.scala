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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import javax.inject.Inject
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._


class NoOfChildrenForm @Inject()(appConfig: FrontendAppConfig) extends FormErrorHelper {

  def noOfChildrenFormatter(errorKeyBlank: String, errorKeyNonNumeric: String): Formatter[Int] = new Formatter[Int] {
    val intRegex: String = "([0-9]{1,3})".r.toString()
    val minAmountChildren: Double = appConfig.minAmountChildren
    val maxAmountChildren: Double = appConfig.maxAmountChildren

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], Int] = {
      data.get(key) match {
        case None => produceError(key, errorKeyBlank)
        case Some("") => produceError(key, errorKeyBlank)

        case Some(s) if s.matches(intRegex) =>
          val value = s.toInt
          if (value >= minAmountChildren && value <= maxAmountChildren) {
            Right(value)
          } else {
            produceError(key, noOfChildrenErrorKey)
          }
        case _ => produceError(key, errorKeyNonNumeric)
      }
    }

    def unbind(key: String, value: Int) = Map(key -> value.toString)
  }

  def apply(errorKeyBlank: String = noOfChildrenRequiredErrorKey,
            errorKeyNonNumeric: String = noOfChildrenNotInteger): Form[Int] =
    Form(single("value" -> of(noOfChildrenFormatter(errorKeyBlank, errorKeyNonNumeric))))
}
