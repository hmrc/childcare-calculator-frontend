package uk.gov.hmrc.childcarecalculatorfrontend.utils

import org.scalatest.{EitherValues, MustMatchers, WordSpec}
import play.api.data.validation.ValidationError
import play.api.libs.json.{JsError, JsPath, Json}

class MapFormatsSpec extends WordSpec with MustMatchers with EitherValues with MapFormats {

  "map reads" must {

    "successfully read json into a `Map[Int, String]`" in {

      val json = Json.obj(
        "0" -> "foo",
        "1" -> "bar"
      )

      val result = Json.fromJson[Map[Int, String]](json).asEither

      result.right.value must contain(0 -> "foo")
      result.right.value must contain(1 -> "bar")
    }

    "fail to read json into a `Map[Int, String]` when keys are not valid `Int`s" in {

      val json = Json.obj(
        "foo" -> "foo",
        "1" -> "bar"
      )

      val result = Json.fromJson[Map[Int, String]](json).asEither

      result.left.value must contain (JsPath -> Seq(ValidationError("Failed to convert map keys into ints")))
    }
  }

  "map writes" must {

    "write a `Map[Int, String]` into JSON" in {

      val json = Json.obj(
        "0" -> "foo",
        "1" -> "bar"
      )

      val model = Map(
        0 -> "foo",
        1 -> "bar"
      )

      Json.toJson(model) mustEqual json
    }
  }
}
