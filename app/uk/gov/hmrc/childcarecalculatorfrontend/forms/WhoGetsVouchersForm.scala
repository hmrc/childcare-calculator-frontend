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
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CCConstants

/**
 * Created by user on 31/08/17.
 */
@Singleton
class WhoGetsVouchersForm @Inject()(val messagesApi: MessagesApi) extends I18nSupport with CCConstants {

  type WhoGetsVouchersFormType = Option[String]

  val form = Form[WhoGetsVouchersFormType](
    single(
      whoGetsVouchersKey -> optional(text).verifying(
        Messages("who.gets.vouchers.not.selected.error"),
        youOrPartner =>
          YouPartnerBothEnum.values.exists(_.toString == youOrPartner.getOrElse(""))
      )
    )
  )
}
