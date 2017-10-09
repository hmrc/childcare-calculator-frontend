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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import javax.inject.Inject

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class WhatIsYourTaxCodeForm @Inject()(appConfig: FrontendAppConfig) extends FormErrorHelper {

  def whatIsYourTaxCodeFormatter(errorKeyBlank: String, errorKeyInvalid: String) = new Formatter[String] {

    val taxCodeRegex: String = """[1-9][0-9]{2,3}[L-NSTBDWX0][RT01]?""".r.toString()

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = {
      data.get(key) match {
        case None => produceError(key, errorKeyBlank)
        case Some("") => produceError(key, errorKeyBlank)
        case Some(s) if s.toUpperCase.matches(taxCodeRegex) =>
          val length = s.length
          val lastTwoChar = s.substring(length - two)

          if (lastTwoChar.contains(taxCode0T) && length < taxCodeLength_five) {
            produceError(key, errorKeyInvalid)
          } else {
            appConfig.getConfig(s"taxCodeLetter.${getTaxCodeLetter(s)}") match {
              case Some(_) => Right(s)
              case None => produceError(key, errorKeyInvalid)
            }
          }
        case _ => produceError(key, errorKeyInvalid)
      }
    }

    def unbind(key: String, value: String) = Map(key -> value.toString)
  }

  def apply(errorKeyBlank: String = "taxCode.blank", errorKeyInvalid: String = "taxCode.invalid"): Form[String] =
    Form(single("value" -> of(whatIsYourTaxCodeFormatter(errorKeyBlank, errorKeyInvalid))))
}
