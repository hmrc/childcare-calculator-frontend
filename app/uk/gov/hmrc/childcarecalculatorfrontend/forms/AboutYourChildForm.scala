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

import java.time.LocalDate
import play.api.data.Form
import play.api.data.Forms.{mapping, of}
import play.api.i18n.Messages
import uk.gov.hmrc.childcarecalculatorfrontend.forms.formatters.DateFormatter
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild

object AboutYourChildForm extends Mappings {

  private val dateKey = "aboutYourChild.dob"
  private val maxYearsInPast = 18
  private val maxLength = 35
  private val minDate = LocalDate.now.minusYears(maxYearsInPast).minusDays(1)
  private val maxDate = LocalDate.now.plusDays(1)

  def apply(index: Int = 0, total: Int = 1, children: Option[Map[Int, AboutYourChild]] = None)(implicit messages: Messages): Form[AboutYourChild] = {
    if(total > 1) {
      multipleChildrenForm(index, children)
    } else {
      Form(
        mapping(
          "name" ->
            string("aboutYourChild.name.error.required")
              .verifying(maxLength(maxLength, "aboutYourChild.name.error.maxLength"))
              .verifying("aboutYourChild.name.error.duplicate", isDuplicateValue(_, index, children)),
          dateKey -> of(DateFormatter(
            dateKey,
            optMinDate = Some(minDate),
            optMaxDate = Some(maxDate)
          )
          ))((name, date) => AboutYourChild(name, date))(model => Some(model.name, model.dob))
      )
    }
  }

  private def multipleChildrenForm(index: Int, children: Option[Map[Int, AboutYourChild]])(implicit messages: Messages): Form[AboutYourChild] = {
    val indexMessage = messages(s"nth.$index")

    Form(
      mapping(
        "name" ->
          string("aboutYourChild.nth.name.error.required", indexMessage)
            .verifying(maxLength(maxLength, "aboutYourChild.nth.name.error.maxLength", indexMessage))
            .verifying("aboutYourChild.name.error.duplicate", isDuplicateValue(_, index, children)),
        dateKey -> of(DateFormatter(
          dateKey,
          optMinDate = Some(LocalDate.now.minusYears(maxYearsInPast).minusDays(1)),
          optMaxDate = Some(LocalDate.now.plusDays(1)),
          args = Seq(indexMessage)
        )
        ))((name, date) => AboutYourChild(name, date))(model => Some(model.name, model.dob))
      )
  }

  private def isDuplicateValue(x: String, index: Int, children: Option[Map[Int, AboutYourChild]]): Boolean = children match {
    case None => true
    case Some(child) =>
      val filtered = child.view.filterKeys(_ != index)
      !filtered.values.exists(_.name == x)
  }


}
