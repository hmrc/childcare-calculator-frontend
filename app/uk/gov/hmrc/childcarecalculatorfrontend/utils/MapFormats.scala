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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import play.api.libs.json._

import scala.util.{Failure, Success, Try}

trait MapFormats {

  implicit def mapReads[V](implicit rds: Reads[Map[String, V]]): Reads[Map[Int, V]] =
    Reads[Map[Int, V]] { json =>
      Json.fromJson[Map[String, V]](json).flatMap { data =>
        Try(data.map { case (k, v) =>
          (k.toInt, v)
        }) match {
          case Success(v) =>
            JsSuccess(v)
          case Failure(e) =>
            JsError("Failed to convert map keys into ints")
        }
      }
    }

  implicit def mapWrites[V](implicit wrts: Writes[Map[String, V]]): Writes[Map[Int, V]] =
    Writes[Map[Int, V]] { map =>
      val newMap = map.map { case (k, v) =>
        (k.toString, v)
      }

      Json.toJson(newMap)
    }

}
