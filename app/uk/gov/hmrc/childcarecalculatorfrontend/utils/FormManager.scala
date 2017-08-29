/*
 * Copyright 2017 HM Revenue & Customs
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

import play.api.data.{FormError, Form}

trait FormManager {
  def overrideFormErrorKey[A](form: Form[A], newMessageKeys: Map[String, String], forceOverride: Boolean = false): Form[A] = {
    val modified = form.errors.foldLeft(Seq[FormError]())((acc, error) => {
      val theKey = newMessageKeys.get(error.message)
      if ((error.key.isEmpty || forceOverride) && theKey.isDefined) {
        acc :+ error.copy(key = theKey.get)
      } else {
        acc :+ error
      }
    })
    form.copy[A](form.mapping, form.data, modified, form.value)
  }
}
