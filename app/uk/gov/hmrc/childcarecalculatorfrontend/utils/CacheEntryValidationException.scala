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

import play.api.libs.json.{JsPath, JsValue, Json, JsonValidationError}

class CacheEntryValidationException(
    val key: String,
    val invalidJson: JsValue,
    val readingAs: Class[_],
    val errors: scala.collection.Seq[(JsPath, scala.collection.Seq[JsonValidationError])] // default Seq for Scala 2.13 is scala.collection.immutable.Seq - this keeps it the same as JsResult
) extends Exception {

  override def getMessage: String =
    s"Cache entry validation for key '$key' was '${Json.stringify(invalidJson)}'. Attempt to convert to ${readingAs.getName} gave errors: $errors"

}
