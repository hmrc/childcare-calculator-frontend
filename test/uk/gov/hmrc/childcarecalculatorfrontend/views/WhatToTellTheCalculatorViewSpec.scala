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

import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whatToTellTheCalculator

class WhatToTellTheCalculatorViewSpec extends NewViewBehaviours {

  val view = app.injector.instanceOf[whatToTellTheCalculator]

  def createView = () => view()(fakeRequest, messages)

  "whatToTellTheCalculator view" must {

    behave like normalPage(createView, "whatToTellTheCalculator", "guidanceA", "guidanceB",
      "li.workingHours", "li.workingHoursPartner", "guidanceC", "li.dob", "li.costs", "li.benefits", "tcUc", "p1.a", "p1.link", "p1.b", "p2.a", "p2.link")
  }
}