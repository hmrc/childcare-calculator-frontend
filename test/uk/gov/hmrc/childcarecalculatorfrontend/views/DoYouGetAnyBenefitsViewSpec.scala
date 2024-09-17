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
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewYesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode,Location}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.doYouGetAnyBenefits

class DoYouGetAnyBenefitsViewSpec extends NewYesNoViewBehaviours {

  override val form = BooleanForm()

  val messageKeyPrefix = "doYouGetAnyBenefits"
  val view = application.injector.instanceOf[doYouGetAnyBenefits]


  def createView(location:Location.Value) = () => view(frontendAppConfig, BooleanForm(), NormalMode, location)(fakeRequest, messages)

  def createViewUsingForm(location:Location.Value) = (form: Form[Boolean]) => view(frontendAppConfig, form, NormalMode, location)(fakeRequest, messages)

  "DoYouGetAnyBenefits view for non-scottish users" must {

    val location: Location.Value = Location.ENGLAND
    behave like normalPage(createView(location), messageKeyPrefix, "li.incomeSupport", "li.jsa", "li.esa", "li.pensionCredit",
      "li.disabilityAllowance", "li.attendanceAllowance", "li.independencePayment", "li.carersAllowance")

    behave like pageWithBackLink(createView(location))

    behave like yesNoPage(createViewUsingForm(location), messageKeyPrefix, routes.DoYouGetAnyBenefitsController.onSubmit(NormalMode).url)
  }

  "DoYouGetAnyScottishBenefits view for scottish users" must {

    val location: Location.Value = Location.SCOTLAND
    behave like normalPage(createView(location), messageKeyPrefix, "li.incomeSupport", "li.jsa", "li.esa", "li.pensionCredit",
      "li.disabilityAllowance", "li.attendanceAllowance", "li.independencePayment", "li.scottishCarersAllowance")

    behave like pageWithBackLink(createView(location))

    behave like yesNoPage(createViewUsingForm(location), messageKeyPrefix, routes.DoYouGetAnyBenefitsController.onSubmit(NormalMode).url)
  }

}
