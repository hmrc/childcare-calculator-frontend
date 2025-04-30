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
import org.scalatest.OptionValues
import org.scalatestplus.play.PlaySpec
import play.api.data.{Form, FormError}

class LocalDateMappingSpec extends PlaySpec with OptionValues {

  import play.api.data.Forms._
  val form = Form(single("date" -> localDateMapping))

  ".bind" must {

    "successfully bind a local date from valid data" in {

      val data: Map[String, String] = Map(
        "date.day"   -> "1",
        "date.month" -> "2",
        "date.year"  -> "2017"
      )

      form.bind(data).value.value mustEqual LocalDate.of(2017, 2, 1)
    }

    "fail to bind from an invalid date" in {

      val data: Map[String, String] = Map(
        "date.day"   -> "31",
        "date.month" -> "2",
        "date.year"  -> "2017"
      )

      form.bind(data).errors must contain(FormError("date", "error.invalidDate"))
    }

    "fail to bind when missing a day" in {

      val data: Map[String, String] = Map(
        "date.month" -> "2",
        "date.year"  -> "2017"
      )

      form.bind(data).errors must contain(FormError("date.day", "error.required"))
    }

    "fail to bind when missing a month" in {

      val data: Map[String, String] = Map(
        "date.day"  -> "1",
        "date.year" -> "2017"
      )

      form.bind(data).errors must contain(FormError("date.month", "error.required"))
    }

    "fail to bind when missing a year" in {

      val data: Map[String, String] = Map(
        "date.day"   -> "1",
        "date.month" -> "2"
      )

      form.bind(data).errors must contain(FormError("date.year", "error.required"))
    }
  }

  ".unbind" must {

    "unbind to the correct data" in {

      val data: Map[String, String] = Map(
        "date.day"   -> "1",
        "date.month" -> "2",
        "date.year"  -> "2017"
      )

      form.fill(LocalDate.of(2017, 2, 1)).data mustEqual data
    }
  }

}
