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
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Location
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewYesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.youAnyTheseBenefitsCY

class YouAnyTheseBenefitsCYViewSpec extends NewYesNoViewBehaviours {

  override val form: Form[Boolean] = BooleanForm()
  val view: youAnyTheseBenefitsCY  = inject[youAnyTheseBenefitsCY]
  val taxYearInfo                  = new TaxYearInfo

  val messageKeyPrefix = "youAnyTheseBenefitsCY"

  def render(form: Form[Boolean] = this.form, location: Location): Html =
    view(form, location)(using fakeRequest, messages)

  "YouAnyTheseBenefits view for non Scottish users" must {
    val england = Location.England

    behave.like(
      normalPage(
        () => render(location = england),
        messageKeyPrefix,
        "li.income_support",
        "li.jobseekers_allowance",
        "li.carers",
        "li.employment_support",
        "li.pensions",
        "li.disability"
      )
    )

    behave.like(pageWithBackLink(() => render(location = england)))

    behave.like(
      yesNoPage(
        form => render(form = form, location = england),
        messageKeyPrefix,
        routes.YouAnyTheseBenefitsCYController.onSubmit().url
      )
    )

    "contain tax year info" in {
      val doc = asDocument(render(location = england))
      assertContainsText(
        doc,
        messages(s"$messageKeyPrefix.tax_year", taxYearInfo.currentTaxYearStart, taxYearInfo.currentTaxYearEnd)
      )
    }
  }

  "YouAnyTheseBenefits view for Scottish users" must {
    val scotland = Location.Scotland

    behave.like(
      normalPage(
        () => render(location = scotland),
        messageKeyPrefix,
        "li.income_support",
        "li.jobseekers_allowance",
        "li.scottishCarersAllowance",
        "li.employment_support",
        "li.pensions",
        "li.disability"
      )
    )

    behave.like(pageWithBackLink(() => render(location = scotland)))

    behave.like(
      yesNoPage(
        form => render(form = form, location = scotland),
        messageKeyPrefix,
        routes.YouAnyTheseBenefitsCYController.onSubmit().url
      )
    )

    "contain tax year info" in {
      val doc = asDocument(render(location = scotland))
      assertContainsText(
        doc,
        messages(s"$messageKeyPrefix.tax_year", taxYearInfo.currentTaxYearStart, taxYearInfo.currentTaxYearEnd)
      )
    }
  }

}
