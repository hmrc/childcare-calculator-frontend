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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import org.joda.time.LocalDate
import play.api.data.Form
import play.api.libs.json.{JsNumber, JsString, JsValue, Json}
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.ExpectedChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{AboutYourChildId, ChildcareCostsId, ChildcarePayFrequencyId, ExpectedChildcareCostsId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildcarePayFrequency._
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoNotYetEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, ChildcarePayFrequency, NormalMode, YesNoNotYetEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.expectedChildcareCosts
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext.Implicits.global

class ExpectedChildcareCostsControllerSpec extends ControllerSpecBase {

  private val testDate: LocalDate = LocalDate.parse("2019-01-01")

  def onwardRoute: Call = routes.WhatToTellTheCalculatorController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): ExpectedChildcareCostsController =
    new ExpectedChildcareCostsController(frontendAppConfig, mcc, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction)

  def viewAsString(
                    form: Form[BigDecimal] = ExpectedChildcareCostsForm(WEEKLY, "Foo"),
                    hasCosts: YesNoNotYetEnum.Value,
                    id: Int = 0,
                    frequency: ChildcarePayFrequency.Value = WEEKLY,
                    name: String = "Foo"
                  ): String =
    expectedChildcareCosts(frontendAppConfig, form, hasCosts, id, frequency, name, NormalMode)(fakeRequest, messages).toString

  val testNumber: Int = 123

  def requiredData(hasCosts: YesNoNotYetEnum.Value): Map[String, JsValue] = Map(
    AboutYourChildId.toString -> Json.obj(
      "0" -> Json.toJson(AboutYourChild("Foo", testDate)),
      "1" -> Json.toJson(AboutYourChild("Bar", testDate))
    ),
    ChildcarePayFrequencyId.toString -> Json.obj(
      "0" -> JsString(WEEKLY.toString),
      "1" -> JsString(MONTHLY.toString)
    ),
    ChildcareCostsId.toString -> JsString(hasCosts.toString)
  )

  def getRequiredData(hasCosts: YesNoNotYetEnum.Value): DataRetrievalAction =
    new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData(hasCosts))), Some(testDate))

  def getRequiredData: DataRetrievalAction =
    getRequiredData(YesNoNotYetEnum.YES)

  "ExpectedChildcareCosts Controller" must {

    Seq(
      (YES, 0, WEEKLY, "Foo"),
      (NOTYET, 1, MONTHLY, "Bar")
    ).foreach {
      case (hasCosts, id, frequency, name) =>

        s"return OK and the correct view for a GET, for id: $id" in {
          val result = controller(getRequiredData(hasCosts)).onPageLoad(NormalMode, id)(fakeRequest)
          status(result) mustBe OK
          contentAsString(result) mustBe viewAsString(ExpectedChildcareCostsForm(frequency, name), hasCosts, id, frequency, name)
        }

        s"populate the view correctly on a GET when the question has previously been answered, for id: $id" in {
          val validData = requiredData(hasCosts) + (ExpectedChildcareCostsId.toString -> Json.obj(
            id.toString -> JsNumber(testNumber)
          ))
          val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)), Some(testDate))
          val result = controller(getRelevantData).onPageLoad(NormalMode, id)(fakeRequest)
          contentAsString(result) mustBe viewAsString(ExpectedChildcareCostsForm(frequency, name).fill(testNumber), hasCosts, id, frequency, name)
        }

        s"return a Bad Request and errors when invalid data is submitted, for id: $id" in {
          val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
          val boundForm = ExpectedChildcareCostsForm(frequency, name).bind(Map("value" -> "invalid value"))
          val result = controller(getRequiredData(hasCosts)).onSubmit(NormalMode, id)(postRequest)
          status(result) mustBe BAD_REQUEST
          contentAsString(result) mustBe viewAsString(boundForm, hasCosts, id, frequency, name)
        }
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testNumber.toString))
      val result = controller(getRequiredData).onSubmit(NormalMode, 0)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode, 0)(fakeRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testNumber.toString))
      val result = controller(dontGetAnyData).onSubmit(NormalMode, 0)(postRequest)
      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a GET if no answer exists for `AboutYourChild`" in {
      val data = Map(
        ChildcarePayFrequency.toString -> Json.obj(
          "0" -> JsString("weekly"),
          "1" -> JsString("monthly")
        ),
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString)
      )
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)), Some(testDate))
      val result = controller(getData).onPageLoad(NormalMode, 0)(fakeRequest)
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no answers exists for `AboutYourChild`" in {
      val data = Map(
        ChildcarePayFrequency.toString -> Json.obj(
          "0" -> JsString("weekly"),
          "1" -> JsString("monthly")
        ),
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString)
      )
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testNumber.toString))
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)), Some(testDate))
      val result = controller(getData).onSubmit(NormalMode, 0)(postRequest)
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a GET if no answer exists for `ChildcarePayFrequency`" in {
      val data = Map(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", testDate)),
          "1" -> Json.toJson(AboutYourChild("Bar", testDate))
        ),
        ChildcarePayFrequency.toString -> Json.obj(
          "1" -> JsString("monthly")
        ),
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString)
      )
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)), Some(testDate))
      val result = controller(getData).onPageLoad(NormalMode, 0)(fakeRequest)
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no answer exists for `ChildcarePayFrequency`" in {
      val data = Map(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", testDate)),
          "1" -> Json.toJson(AboutYourChild("Bar", testDate))
        ),
        ChildcarePayFrequency.toString -> Json.obj(
          "1" -> JsString("monthly")
        ),
        ChildcareCostsId.toString -> JsString(YesNoNotYetEnum.YES.toString)
      )
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testNumber.toString))
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)), Some(testDate))
      val result = controller(getData).onSubmit(NormalMode, 0)(postRequest)
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a GET if no answer exists for `ChildcareCosts`" in {
      val data = Map(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", testDate)),
          "1" -> Json.toJson(AboutYourChild("Bar", testDate))
        ),
        ChildcarePayFrequencyId.toString -> Json.obj(
          "0" -> JsString(WEEKLY.toString),
          "1" -> JsString(MONTHLY.toString)
        )
      )
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)), Some(testDate))
      val result = controller(getData).onPageLoad(NormalMode, 0)(fakeRequest)
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no answer exists for `ChildcareCosts`" in {
      val data = Map(
        AboutYourChildId.toString -> Json.obj(
          "0" -> Json.toJson(AboutYourChild("Foo", testDate)),
          "1" -> Json.toJson(AboutYourChild("Bar", testDate))
        ),
        ChildcarePayFrequencyId.toString -> Json.obj(
          "0" -> JsString(WEEKLY.toString),
          "1" -> JsString(MONTHLY.toString)
        )
      )
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testNumber.toString))
      val getData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, data)), Some(testDate))
      val result = controller(getData).onSubmit(NormalMode, 0)(postRequest)
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
