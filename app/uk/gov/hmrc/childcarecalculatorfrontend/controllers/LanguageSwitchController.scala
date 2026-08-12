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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import com.google.inject.Inject
import play.api.Configuration
import play.api.i18n.{I18nSupport, Lang, MessagesApi}
import play.api.mvc.*
import uk.gov.hmrc.childcarecalculatorfrontend.config.FrontendAppConfig

import javax.inject.Singleton

// TODO, upstream this into play-language
@Singleton
class LanguageSwitchController @Inject() (
    configuration: Configuration,
    appConfig: FrontendAppConfig
)(
    using override val messagesApi: MessagesApi
) extends InjectedController
    with I18nSupport {

  private def fallbackURL: String = routes.WhatToTellTheCalculatorController.onPageLoad.url

  private def languageMap: Map[String, Lang] = appConfig.languageMap

  def switchToLanguage(language: String): Action[AnyContent] = Action { request =>
    val enabled = isWelshEnabled
    val lang = if (enabled) {
      languageMap.getOrElse(language, Lang.defaultLang)
    } else {
      Lang("en")
    }
    val redirectURL = request.headers.get(REFERER).getOrElse(fallbackURL)
    Redirect(redirectURL).withLang(Lang.apply(lang.code)).flashing(Flash(Map("switching-language" -> "true")))
  }

  private def isWelshEnabled: Boolean =
    configuration.getOptional[Boolean]("microservice.services.features.welsh-translation").getOrElse(true)

}
