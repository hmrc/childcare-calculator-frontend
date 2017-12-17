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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.PartnerStatutoryPayPerWeekForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, StatutoryPayTypeEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.IntViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.partnerStatutoryPayPerWeek

class PartnerStatutoryPayPerWeekViewSpec extends IntViewBehaviours {

  val messageKeyPrefix = "partnerStatutoryPayPerWeek"

  val statutoryType = "maternity"

  def createView = () => partnerStatutoryPayPerWeek(frontendAppConfig, PartnerStatutoryPayPerWeekForm(), NormalMode, statutoryType)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Int]) => partnerStatutoryPayPerWeek(frontendAppConfig, form, NormalMode, statutoryType)(fakeRequest, messages)

  val form = PartnerStatutoryPayPerWeekForm()

  "PartnerStatutoryPayPerWeek view" must {
    behave like normalPageWithTitleAsString(
      createView,
      messageKeyPrefix,
      title = messages(s"$messageKeyPrefix.title", statutoryType),
      heading = Some(messages(s"$messageKeyPrefix.title", statutoryType))
    )

    behave like pageWithBackLink(createView)

    behave like intPage(
      createViewUsingForm,
      messageKeyPrefix,
      routes.PartnerStatutoryPayPerWeekController.onSubmit(NormalMode).url,
      messageDynamicValue = Some(statutoryType.toString))
  }
}
