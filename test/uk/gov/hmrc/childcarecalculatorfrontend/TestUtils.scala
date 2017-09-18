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

package uk.gov.hmrc.childcarecalculatorfrontend

import org.mockito.Mockito._
import org.mockito.ArgumentCaptor
import uk.gov.hmrc.childcarecalculatorfrontend.models.PageObjects
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier
import play.api.libs.json.Format

trait TestUtils {

  implicit val headNapper = ArgumentCaptor.forClass(classOf[HeaderCarrier])
  implicit val pageObjectsNapper = ArgumentCaptor.forClass(classOf[Format[PageObjects]])

  def verifyAndReturnStoredPageObjects(keyStore: KeystoreService): PageObjects = {
    val captor = ArgumentCaptor.forClass(classOf[PageObjects])
    verify(keyStore)
      .cache(captor.capture)(headNapper.capture, pageObjectsNapper.capture)
    captor.getValue
  }

}
