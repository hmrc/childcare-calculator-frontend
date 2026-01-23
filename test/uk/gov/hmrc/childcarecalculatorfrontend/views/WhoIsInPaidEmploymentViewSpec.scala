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

import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoIsInPaidEmploymentForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoIsInPaidEmployment

class WhoIsInPaidEmploymentViewSpec extends NewViewBehaviours with BeforeAndAfterEach {

  val view             = application.injector.instanceOf[whoIsInPaidEmployment]
  val messageKeyPrefix = "whoIsInPaidEmployment"

  def constructView(
      appConfig: FrontendAppConfig = frontendAppConfig,
      form: Form[String] = WhoIsInPaidEmploymentForm(),
      location: Location.Value = Location.ENGLAND
  ) = view(appConfig, form, location)(fakeRequest, messages)

  def createView = () => constructView()

  def createViewUsingForm = (form: Form[String]) => constructView(form = form)

  val appConfigBpllEnabled: FrontendAppConfig  = mock[FrontendAppConfig]
  val appConfigBpllDisabled: FrontendAppConfig = mock[FrontendAppConfig]

  override def beforeEach(): Unit = {
    when(appConfigBpllEnabled.bpplContentEnabled).thenReturn(true)
    when(appConfigBpllDisabled.bpplContentEnabled).thenReturn(false)
  }

  val bereavedPartnersPaternityLeave = "bereaved partner&#x27;s paternity leave"

  "WhoIsInPaidEmployment view" must {
    behave.like(normalPage(createView, messageKeyPrefix, "para1"))

    behave.like(pageWithBackLink(createView))

    "include bereaved partner's paternity leave on page" when {
      "the bpllContentEnabled flag is set to true" when {
        "the location is England" in {
          constructView(appConfigBpllEnabled, location = Location.ENGLAND).toString must include(
            bereavedPartnersPaternityLeave
          )
        }

        "the location is Scotland" in {
          constructView(appConfigBpllEnabled, location = Location.SCOTLAND).toString must include(
            bereavedPartnersPaternityLeave
          )
        }

        "the location is Wales" in {
          constructView(appConfigBpllEnabled, location = Location.WALES).toString must include(
            bereavedPartnersPaternityLeave
          )
        }
      }
    }

    "NOT include bereaved partner's paternity leave on page" when {
      "the bpllContentEnabled flag is set to false" when {
        "the location is England" in
          (constructView(appConfigBpllDisabled, location = Location.ENGLAND).toString must not)
            .include(bereavedPartnersPaternityLeave)

        "the location is Scotland" in
          (constructView(appConfigBpllDisabled, location = Location.SCOTLAND).toString must not)
            .include(bereavedPartnersPaternityLeave)

        "the location is Wales" in
          (constructView(appConfigBpllDisabled, location = Location.WALES).toString must not)
            .include(bereavedPartnersPaternityLeave)

        "the location is Northern Ireland" in
          (constructView(appConfigBpllDisabled, location = Location.NORTHERN_IRELAND).toString must not)
            .include(bereavedPartnersPaternityLeave)

      }

      "the bpllContentEnabledFlag is set to true" when {
        "the location is Northern Ireland" in
          (constructView(appConfigBpllEnabled, location = Location.NORTHERN_IRELAND).toString must not)
            .include(bereavedPartnersPaternityLeave)
      }
    }
  }

  "WhoIsInPaidEmployment view" when {
    "rendered" must {
      "contain radio buttons for the value" in {
        val doc = asDocument(createViewUsingForm(WhoIsInPaidEmploymentForm()))
        for (option <- WhoIsInPaidEmploymentForm.options)
          assertContainsRadioButton(doc, option.id, "value", option.value, false)
      }
    }

    for (option <- WhoIsInPaidEmploymentForm.options)
      s"rendered with a value of '${option.value}'" must {
        s"have the '${option.value}' radio button selected" in {
          val doc =
            asDocument(createViewUsingForm(WhoIsInPaidEmploymentForm().bind(Map("value" -> s"${option.value}"))))
          assertContainsRadioButton(doc, option.id, "value", option.value, true)

          for (unselectedOption <- WhoIsInPaidEmploymentForm.options.filterNot(o => o == option))
            assertContainsRadioButton(doc, unselectedOption.id, "value", unselectedOption.value, false)
        }
      }
  }

}
