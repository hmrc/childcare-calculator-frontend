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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.forms.SurveyDoNotUnderstandForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewStringViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.surveyDoNotUnderstand

class SurveyDoNotUnderstandViewSpec extends NewStringViewBehaviours {

  val messageKeyPrefix = "surveyDoNotUnderstand"

  val view: surveyDoNotUnderstand = inject[surveyDoNotUnderstand]

  override val form: Form[String] = SurveyDoNotUnderstandForm()

  def render(form: Form[String] = this.form): Html = view(form)(using fakeRequest, messages)

  "SurveyDoNotUnderstand view" must {
    behave.like(normalPage(() => render(), messageKeyPrefix))

    behave.like(pageWithBackLink(() => render()))
  }

}
