/*
 * Copyright 2021 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.YesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.yourOtherIncomeLY

class YourOtherIncomeLYViewSpec extends YesNoViewBehaviours {

  val view = app.injector.instanceOf[yourOtherIncomeLY]
  val taxYearInfo = new TaxYearInfo

  val messageKeyPrefix = "yourOtherIncomeLY"

  def createView = () => view(frontendAppConfig, BooleanForm(), NormalMode, taxYearInfo)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => view(frontendAppConfig, form, NormalMode, taxYearInfo)(fakeRequest, messages)

  "YourOtherIncomeLY view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.YourOtherIncomeLYController.onSubmit(NormalMode).url)

    "contain tax year info" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages(s"$messageKeyPrefix.year.start.end.date", taxYearInfo.previousTaxYearStart, taxYearInfo.previousTaxYearEnd))
    }
  }
}
