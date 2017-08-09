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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import javax.inject.{Inject, Singleton}

import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Messages, I18nSupport, MessagesApi}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CCConstants

@Singleton
class PaidEmploymentForm @Inject()(hasPartner: Boolean = false, val messagesApi: MessagesApi) extends I18nSupport with CCConstants {

  val familyStatus: String = getFamilyStatus(hasPartner)

  type InEmploymentFormType = Option[Boolean]

  val form = Form[InEmploymentFormType](
    single(
      paidEmploymentKey -> optional(boolean).verifying(
        Messages(s"paid.employment.not.selected.error.${familyStatus}"),
        _.isDefined
      )
    )
  )
}
