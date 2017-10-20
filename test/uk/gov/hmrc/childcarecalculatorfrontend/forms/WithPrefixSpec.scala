package uk.gov.hmrc.childcarecalculatorfrontend.forms

import org.scalatest.{MustMatchers, WordSpec}
import play.api.data.FormError

class WithPrefixSpec extends WordSpec with MustMatchers {

  ".withPrefix" must {

    "add prefix to an existing key" in {
      val error = FormError("key", "error").withPrefix("prefix")
      error.key mustEqual "prefix.key"
      error.message mustEqual "error"
    }

    "not add an empty prefix" in {
      val error = FormError("key", "error").withPrefix("")
      error.key mustEqual "key"
      error.message mustEqual "error"
    }

    "use only a prefix if the key is empty" in {
      val error = FormError("", "error").withPrefix("prefix")
      error.key mustEqual "prefix"
      error.message mustEqual "error"
    }
  }
}
