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

import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.data.{Form, FormError}
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentIncomeCY
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

trait FormErrorHelper extends Mappings {

  private val requiredField: String = "error.required"

  val decimalRegex: String = """\d+(\.\d{1,2})?""".r.toString()

  def produceError(key: String, error: String, args: Any*): Left[Seq[FormError], Nothing] = Left(
    Seq(FormError(key, error, args))
  )

  def validateInRange(value: BigDecimal, minValue: BigDecimal, maxValue: BigDecimal): Boolean =
    value >= minValue && value <= maxValue

  def valueNonEmpty(message: String): Constraint[String] = Constraint[String](requiredField) { o =>
    if (o != null && o.trim.nonEmpty) Valid else Invalid(ValidationError(message))
  }

  def validateDecimalInRange(message: String, minValue: BigDecimal, maxValue: BigDecimal): Constraint[String] =
    Constraint[String](requiredField) { o =>
      if (o.matches(decimalRegex) && validateInRange(BigDecimal(o), minValue, maxValue)) Valid
      else Invalid(ValidationError(message))
    }

  def validateDecimal(message: String): Constraint[String] =
    Constraint[String](requiredField)(o => if (o.matches(decimalRegex)) Valid else Invalid(ValidationError(message)))

  def getTaxCodeLetter(value: String): String = {
    val intRegex      = """[0-9]""".r.toString()
    val lastTwoChar   = value.substring(value.length - two)
    val lastOneChar   = value.substring(value.length - one)
    val OneMiddleChar = value.substring(value.length - two, value.length - one)

    value.length match {
      case `taxCodeLength_six` => lastTwoChar
      case `taxCodeLength_five` =>
        if (OneMiddleChar.matches(intRegex)) {
          lastOneChar
        } else {
          lastTwoChar
        }
      case `taxCodeLength_four` => lastOneChar
      case _                    => value
    }
  }

  def returnOnFirstFailure[T](constraints: Constraint[T]*): Constraint[T] = Constraint { field: T =>
    constraints.toList.dropWhile(_(field) == Valid) match {
      case Nil             => Valid
      case constraint :: _ => constraint(field)
    }
  }

  def validateMaxIncomeEarnings(
      maximumEarnings: Option[Boolean],
      maxIncome: Double,
      errorKeyInvalidMaxEarnings: String,
      boundForm: Form[BigDecimal]
  ): Form[BigDecimal] =
    maximumEarnings match {
      case Some(maxEarnings) =>
        val inputtedEmploymentIncomeValue = boundForm.value.getOrElse(BigDecimal(0))

        if (inputtedEmploymentIncomeValue >= maxIncome && !maxEarnings) {
          boundForm.withError(defaultFormValueField, errorKeyInvalidMaxEarnings)
        } else {
          boundForm
        }
      case _ =>
        boundForm
    }

  def validateBothMaxIncomeEarnings(
      maximumEarnings: Option[Boolean],
      maxIncome: Double,
      boundForm: Form[EmploymentIncomeCY]
  ): Form[EmploymentIncomeCY] =
    maximumEarnings match {

      case Some(maxEarnings) =>

        val parentEmpIncomeValue  = boundForm(parentEmpIncomeCYFormField).value.getOrElse("0").toDouble
        val partnerEmpIncomeValue = boundForm(partnerEmpIncomeCYFormField).value.getOrElse("0").toDouble

        if ((parentEmpIncomeValue >= maxIncome) && (partnerEmpIncomeValue >= maxIncome) && !maxEarnings) {

          boundForm
            .withError(parentEmpIncomeCYFormField, parentEmploymentIncomeBothInvalidMaxEarningsErrorKey)
            .withError(partnerEmpIncomeCYFormField, partnerEmploymentIncomeBothInvalidMaxEarningsErrorKey)
        } else if (parentEmpIncomeValue >= maxIncome && !maxEarnings) {

          boundForm.withError(parentEmpIncomeCYFormField, parentEmploymentIncomeInvalidMaxEarningsErrorKey)
        } else if (partnerEmpIncomeValue >= maxIncome && !maxEarnings) {

          boundForm.withError(partnerEmpIncomeCYFormField, partnerEmploymentIncomeInvalidMaxEarningsErrorKey)
        } else {
          boundForm
        }

      case _ => boundForm
    }

}
