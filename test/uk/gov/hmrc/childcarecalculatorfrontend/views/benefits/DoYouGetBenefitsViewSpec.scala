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

package uk.gov.hmrc.childcarecalculatorfrontend.views.benefits

import play.api.data.Form
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.benefits.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewYesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.benefits.doYouGetBenefits

class DoYouGetBenefitsViewSpec extends NewYesNoViewBehaviours {

  override val form: Form[Boolean] = BooleanForm()

  val messageKeyPrefix = "doYouGetBenefits"
  val view: doYouGetBenefits = application.injector.instanceOf[doYouGetBenefits]

  def createView: () => HtmlFormat.Appendable = () => view(BooleanForm())(fakeRequest, messages)

  def createViewUsingForm: Form[Boolean] => HtmlFormat.Appendable = (form: Form[Boolean]) => view(form)(fakeRequest, messages)

  "DoYouGetBenefits view" must {
    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.DoYouGetBenefitsController.onSubmit().url)
  }

}
