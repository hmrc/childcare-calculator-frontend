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

import org.joda.time.LocalDate
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.i18n.Messages
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild

object AboutYourChildForm extends Mappings {

  val requiredKey = "aboutYourChild.error.dob.blank"
  val invalidKey = "aboutYourChild.error.dob.invalid"

  def apply(index: Int = 0, total: Int = 1, children: Option[Map[Int, AboutYourChild]] = None)(implicit messages: Messages): Form[AboutYourChild] =
    if(total > 1) {
      multipleChildrenForm(index, children)
    } else {
      Form(
        mapping(
          "name" ->
            string("aboutYourChild.error.name")
              .verifying(maxLength(35, "aboutYourChild.error.maxLength"))
              .verifying("aboutYourChild.error.duplicateName", isDuplicateValue(_, index, children)),

          "dob" ->
            localDateMapping(
              "day" -> int(requiredKey, invalidKey),
              "month" -> int(requiredKey, invalidKey),
              "year" -> int(requiredKey, invalidKey)
            )
              .verifying("aboutYourChild.error.past", _.isAfter(LocalDate.now.minusYears(20)))
              .verifying("aboutYourChild.error.future", _.isBefore(LocalDate.now.plusYears(1)))
              .replaceError(FormError("", "error.invalidDate"), FormError("", invalidKey))
              .replaceError(FormError("day", requiredKey), FormError("", requiredKey))
              .replaceError(FormError("month", requiredKey), FormError("", requiredKey))
              .replaceError(FormError("year", requiredKey), FormError("", requiredKey))
              .replaceError(FormError("day", invalidKey), FormError("", invalidKey))
              .replaceError(FormError("month", invalidKey), FormError("", invalidKey))
              .replaceError(FormError("year", invalidKey), FormError("", invalidKey))
        )(AboutYourChild.apply)(AboutYourChild.unapply)
      )
    }

  private def multipleChildrenForm(index: Int = 0, children: Option[Map[Int, AboutYourChild]] = None)
                                      (implicit messages: Messages): Form[AboutYourChild] = {
    val indexMessage = messages(s"nth.$index")
    val requiredNthKey = "aboutYourChild.error.dob.blank.nth"
    Form(
      mapping(
        "name" ->
          string("aboutYourChild.error.name.nth", indexMessage)
            .verifying(maxLength(35, "aboutYourChild.error.maxLength.nth", indexMessage))
            .verifying("aboutYourChild.error.duplicateName", isDuplicateValue(_, index, children)),
        "dob" ->
          localDateMapping(
            "day" -> int(requiredKey, invalidKey),
            "month" -> int(requiredKey, invalidKey),
            "year" -> int(requiredKey, invalidKey)
          )
            .verifying("aboutYourChild.error.past", _.isAfter(LocalDate.now.minusYears(20)))
            .verifying("aboutYourChild.error.future", _.isBefore(LocalDate.now.plusYears(1)))
            .replaceError(FormError("", "error.invalidDate"), FormError("", invalidKey))
            .replaceError(FormError("day", requiredKey), FormError("", requiredNthKey, Seq(indexMessage)))
            .replaceError(FormError("month", requiredKey), FormError("", requiredNthKey, Seq(indexMessage)))
            .replaceError(FormError("year", requiredKey), FormError("", requiredNthKey, Seq(indexMessage)))
            .replaceError(FormError("day", invalidKey, indexMessage), FormError("", invalidKey))
            .replaceError(FormError("month", invalidKey, indexMessage), FormError("", invalidKey))
            .replaceError(FormError("year", invalidKey, indexMessage), FormError("", invalidKey))
      )(AboutYourChild.apply)(AboutYourChild.unapply)
    )
  }

  private def isDuplicateValue(x: String, index: Int, children: Option[Map[Int, AboutYourChild]]): Boolean = children match {
    case None => true
    case Some(child) => {
      val filtered = child.filterKeys(_ != index)
      !(filtered.values.exists(_.name == x))
    }
  }
}
