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

import play.api.data.{Form, FormError}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentIncomeCY
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class FormErrorHelper extends Mappings {


  val decimalRegex = """\d+(\.\d{1,2})?""".r.toString()

  def produceError(key: String, error: String, args: Any*) = Left(Seq(FormError(key, error, args)))

  def validateInRange(value: BigDecimal, minValue: BigDecimal, maxValue: BigDecimal): Boolean = {
    value >= minValue && value <= maxValue
  }

  def valueNonEmpty(message: String): Constraint[String] = Constraint[String]("error.required") { o =>
    if (o != null && o.trim.nonEmpty) Valid else Invalid(ValidationError(message))
  }

  def validateDecimalInRange(message: String, minValue: BigDecimal, maxValue: BigDecimal): Constraint[String] = Constraint[String]("error.required") { o =>
    if (o.matches(decimalRegex) && validateInRange(BigDecimal(o), minValue, maxValue)) Valid else Invalid(ValidationError(message))
  }

  def validateDecimal(message: String): Constraint[String] = Constraint[String]("error.required") { o =>
    if (o.matches(decimalRegex)) Valid else Invalid(ValidationError(message))
  }

  def getTaxCodeLetter(value: String): String = {
    val intRegex = """[0-9]""".r.toString()
    val lastTwoChar = value.substring(value.length - two)
    val lastOneChar = value.substring(value.length - one)
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
    }
  }

  def returnOnFirstFailure[T](constraints: Constraint[T]*) = Constraint { field: T =>
    constraints.toList dropWhile (_ (field) == Valid) match {
      case Nil => Valid
      case constraint :: _ => constraint(field)
    }
  }

  def validateMaxIncomeEarnings(maximumEarnings: Option[Boolean],
                                errorKeyInvalidMaxEarnings: String,
                                errorKeyInvalid: String,
                                boundForm: Form[BigDecimal]) = {

    val maxValueFalseMaxEarnings = BigDecimal(100000)
    val maxValueTrueMaxEarnings = BigDecimal(1000000)

    maximumEarnings match {
      case Some(maxEarnings) if !boundForm.hasErrors => {
        val inputtedEmploymentIncomeValue = boundForm.value.getOrElse(BigDecimal(0))
        if (inputtedEmploymentIncomeValue >= maxValueFalseMaxEarnings && !maxEarnings) {
          boundForm.withError("value", errorKeyInvalidMaxEarnings)
        }
        else if (inputtedEmploymentIncomeValue >= maxValueTrueMaxEarnings && maxEarnings) {
          boundForm.withError("value", errorKeyInvalid)
        }
        else {
          boundForm
        }
      }
      case _ => {
        boundForm
      }
    }
  }

  def validateBothMaxIncomeEarnings(maximumEarnings: Option[Boolean],
                                    errorKeyInvalidParentMaxEarnings: String,
                                    errorKeyInvalidPartnerMaxEarnings: String,
                                    errorParentKeyInvalid: String,
                                    errorPartnerKeyInvalid: String,
                                    boundForm: Form[EmploymentIncomeCY]): Form[EmploymentIncomeCY] = {

    val maxValueFalseMaxEarnings = BigDecimal(100000)
    val maxValueTrueMaxEarnings = BigDecimal(1000000)

    println(" ************* ::::: form  &&&&&&&&&&&&&&&&&&" +boundForm.hasErrors)
    println(" ************* ::::: form  &&&&&&&&&&&&&&&&&&" +maximumEarnings)

    maximumEarnings match {
      case Some(maxEarnings) if !boundForm.hasErrors => {
        val valueTest = 0
        val parentEmpIncomeValue = boundForm("parentEmploymentIncomeCY").value.getOrElse("0")
        val partnerEmpIncomeValue = boundForm("partnerEmploymentIncomeCY").value.getOrElse("0")


        if ((parentEmpIncomeValue.toInt >= maxValueFalseMaxEarnings) && (partnerEmpIncomeValue.toInt >= maxValueFalseMaxEarnings) && !maxEarnings) {
          println(" first if")
          boundForm.withError("parentEmploymentIncomeCY", errorKeyInvalidParentMaxEarnings).withError("partnerEmploymentIncomeCY", errorKeyInvalidPartnerMaxEarnings)

        }else if (parentEmpIncomeValue.toInt >= maxValueFalseMaxEarnings && !maxEarnings) {
          println(" In first else if")
            boundForm.withError("parentEmploymentIncomeCY", errorKeyInvalidParentMaxEarnings)

        } else if (partnerEmpIncomeValue.toInt >= maxValueFalseMaxEarnings && !maxEarnings) {
          println(" In second else if")
          boundForm.withError("partnerEmploymentIncomeCY", errorKeyInvalidPartnerMaxEarnings)
        }

        else {
          println(" In else")
          boundForm
        }
      }
      case _ => {
        println(" In default case")
        boundForm
      }


    }
  }
}
