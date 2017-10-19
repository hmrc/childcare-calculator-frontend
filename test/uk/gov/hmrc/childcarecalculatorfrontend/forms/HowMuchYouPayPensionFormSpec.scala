package uk.gov.hmrc.childcarecalculatorfrontend.forms

class HowMuchYouPayPensionFormSpec extends FormSpec {

  val errorKeyBlank = "blank"
  val errorKeyInvalid = "invalid"

  "HowMuchYouPayPension Form" must {

    "bind zero" in {
      val form = HowMuchYouPayPensionForm(errorKeyBlank, errorKeyInvalid).bind(Map("value" -> "0.0"))
      form.get shouldBe 0.0
    }

    "bind positive numbers" in {
      val form = HowMuchYouPayPensionForm(errorKeyBlank, errorKeyInvalid).bind(Map("value" -> "1.0"))
      form.get shouldBe 1.0
    }

    "bind positive decimal number" in {
      val form = HowMuchYouPayPensionForm(errorKeyBlank, errorKeyInvalid).bind(Map("value" -> "10.80"))
      form.get shouldBe 10.80
    }

    "fail to bind negative numbers" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(HowMuchYouPayPensionForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "-1"), expectedError)
    }

    "fail to bind non-numerics" in {
      val expectedError = error("value", errorKeyInvalid)
      checkForError(HowMuchYouPayPensionForm(errorKeyBlank, errorKeyInvalid), Map("value" -> "not a number"), expectedError)
    }

    "fail to bind a blank value" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(HowMuchYouPayPensionForm(errorKeyBlank, errorKeyInvalid), Map("value" -> ""), expectedError)
    }

    "fail to bind when value is omitted" in {
      val expectedError = error("value", errorKeyBlank)
      checkForError(HowMuchYouPayPensionForm(errorKeyBlank, errorKeyInvalid), emptyForm, expectedError)
    }

  }
}
