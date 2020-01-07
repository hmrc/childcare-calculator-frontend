/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.models.AgeEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.InputOption

object YourPartnersAgeForm extends FormErrorHelper {

  def YourPartnersAgeFormatter = new Formatter[String] {
    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], String] = data.get(key) match {
      case Some(s) if optionIsValid(s) => Right(s)
      case None => produceError(key, yourPartnersAgeErrorKey)
      case _ => produceError(key, unknownErrorKey)
    }

    def unbind(key: String, value: String) = Map(key -> value)
  }

  def apply(): Form[String] = 
    Form(single("value" -> of(YourPartnersAgeFormatter)))

  def options = Seq(
    InputOption("yourPartnersAge", AgeEnum.UNDER18.toString),
    InputOption("yourPartnersAge", AgeEnum.EIGHTEENTOTWENTY.toString),
    InputOption("yourPartnersAge", AgeEnum.TWENTYONETOTWENTYFOUR.toString),
    InputOption("yourPartnersAge", AgeEnum.OVERTWENTYFOUR.toString)
  )

  def optionIsValid(value: String): Boolean = options.exists(o => o.value == value)
}
