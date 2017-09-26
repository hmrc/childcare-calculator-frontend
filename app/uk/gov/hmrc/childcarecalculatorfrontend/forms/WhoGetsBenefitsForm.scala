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

import javax.inject.{Singleton, Inject}
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{Messages, I18nSupport, MessagesApi}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CCConstants

@Singleton
class WhoGetsBenefitsForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport with CCConstants {
  type WhoGetsBenefitsForm = Option[String]

  val form = Form[WhoGetsBenefitsForm](
    single(
      whoGetsBenefitsKey -> optional(text).verifying(
        Messages("who.gets.benefits.not.selected.error"),
        youOrPartner =>
          YouPartnerBothEnum.values.exists(_.toString == youOrPartner.getOrElse(""))
      )
    )
  )
}
