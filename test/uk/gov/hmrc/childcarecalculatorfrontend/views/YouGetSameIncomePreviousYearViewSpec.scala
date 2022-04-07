/*
 * Copyright 2022 HM Revenue & Customs
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

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewYesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.youGetSameIncomePreviousYear

import scala.collection.immutable.ListMap

class YouGetSameIncomePreviousYearViewSpec extends NewYesNoViewBehaviours with GuiceOneAppPerSuite {

  override val form = BooleanForm()

  val view = app.injector.instanceOf[youGetSameIncomePreviousYear]
  val taxYearInfo = new TaxYearInfo

  val messageKeyPrefix = "youGetSameIncomePreviousYear"

  def createView = () => view(frontendAppConfig, BooleanForm(), NormalMode, taxYearInfo)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[Boolean]) => view(frontendAppConfig, form, NormalMode, taxYearInfo)(fakeRequest, messages)

  "YouGetSameIncomePreviousYear view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like yesNoPage(createViewUsingForm, messageKeyPrefix, routes.YouGetSameIncomePreviousYearController.onSubmit(NormalMode).url)

    "contain your income info" in {
      val view1 = () => view(frontendAppConfig, BooleanForm(), NormalMode, taxYearInfo, Some(ListMap("Income" -> "£250")))(fakeRequest, messages)
      val doc = asDocument(view1())
      assertContainsText(doc, "£250")
    }

    "contain tax year info" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages(s"$messageKeyPrefix.startEndDate", taxYearInfo.previousTaxYearStart, taxYearInfo.previousTaxYearEnd))
    }

    "contain info summary" in {
      val doc = asDocument(createView())
      assertContainsText(doc, messages(s"$messageKeyPrefix.info.summary"))
    }
  }
}
