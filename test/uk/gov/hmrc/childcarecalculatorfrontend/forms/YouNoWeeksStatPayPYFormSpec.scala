package uk.gov.hmrc.childcarecalculatorfrontend.forms

class YouNoWeeksStatPayPYFormSpec extends FormSpec {

  val errorKeyBlank = "blank"
  val errorKeyDecimal = "decimal"
  val errorKeyNonNumeric = "non numeric"

  "YouNoWeeksStatPayPY Form" must {

    "bind zero" in {
      val form = YouNoWeeksStatPayPYForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric).bind(Map("value" -> "0"))
      form.get shouldBe 0
    }

    "bind positive numbers" in {
      val form = YouNoWeeksStatPayPYForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric).bind(Map("value" -> "1"))
      form.get shouldBe 1
    }

    "bind positive, comma separated numbers" in {
      val form = YouNoWeeksStatPayPYForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric).bind(Map("value" -> "10,000"))
      form.get shouldBe 10000
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(YouNoWeeksStatPayPYForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric), Map("value" -> "-1"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyNonNumeric)
      checkForError(YouNoWeeksStatPayPYForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric), Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(YouNoWeeksStatPayPYForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(YouNoWeeksStatPayPYForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric), emptyForm, expectedError)
    }

    "fail to bind decimal numbers" in {
      val expectedError = error("value", errorKeyDecimal)
      checkForError(YouNoWeeksStatPayPYForm(errorKeyBlank, errorKeyDecimal, errorKeyNonNumeric), Map("value" -> "123.45"), expectedError)
    }
  }
}
