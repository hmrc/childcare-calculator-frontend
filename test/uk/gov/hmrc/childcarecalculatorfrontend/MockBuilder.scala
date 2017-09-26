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

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito.stubbing.OngoingStubbing
import play.api.libs.json.{Format, Reads}
import uk.gov.hmrc.childcarecalculatorfrontend.models.PageObjects
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.play.http.HeaderCarrier

import scala.concurrent.Future

trait MockBuilder {

  def createMockToFetchPageObjects(keyStore: KeystoreService,
                                   modelToFetch: Option[PageObjects] = None): OngoingStubbing[Future[Option[PageObjects]]] = {
    when(
      keyStore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
    ).thenReturn(
      Future.successful(modelToFetch)
    )
  }

  def createMockToStorePageObjects(keyStore: KeystoreService,
                                   modelToStore: Option[PageObjects] = None): OngoingStubbing[Future[Option[PageObjects]]] = {
    when(
      keyStore.cache[PageObjects](any[PageObjects])(any[HeaderCarrier], any[Format[PageObjects]])
    ).thenReturn(
      Future.successful(modelToStore)
    )
  }

  def createMockToThrowRuntimeException(keyStore: KeystoreService): OngoingStubbing[Future[Option[PageObjects]]] = {
    when(
      keyStore.fetch[PageObjects]()(any[HeaderCarrier], any[Reads[PageObjects]])
    ).thenReturn(
      Future.failed(new RuntimeException)
    )
  }
}
