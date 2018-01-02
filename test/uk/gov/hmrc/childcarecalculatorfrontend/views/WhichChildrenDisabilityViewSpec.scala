/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichChildrenDisabilityForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{CheckboxViewBehaviours, ViewBehaviours}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichChildrenDisability

class WhichChildrenDisabilityViewSpec extends ViewBehaviours with CheckboxViewBehaviours[Int] {

  val messageKeyPrefix = "whichChildrenDisability"

  val fieldKey = "value"
  val errorMessage = "error.invalid"

  val values: Map[String, Int] = Map(
    "Foo" -> 0,
    "Bar" -> 1
  )

  val strValues: Map[String, String] = values.map {
    case (k, v) => (k, v.toString)
  }

  def form: Form[Set[Int]] = WhichChildrenDisabilityForm(0, 1)

  def createView(form: Form[Set[Int]] = form): Html =
    whichChildrenDisability(frontendAppConfig, form, strValues, NormalMode)(fakeRequest, messages)

  "WhichChildrenDisability view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    behave like checkboxPage()
  }
}
