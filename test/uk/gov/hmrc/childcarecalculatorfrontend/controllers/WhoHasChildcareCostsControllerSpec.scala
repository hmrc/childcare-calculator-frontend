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
import org.scalatest.OptionValues
import play.api.data.Form
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.DataGenerator._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoHasChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{AboutYourChildId, WhichChildrenBlindId, WhichChildrenDisabilityId, WhoHasChildcareCostsId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoHasChildcareCosts
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext.Implicits.global

class WhoHasChildcareCostsControllerSpec extends ControllerSpecBase with OptionValues {

  val view = application.injector.instanceOf[whoHasChildcareCosts]

  private val testDate: LocalDate           = LocalDate.parse("2019-01-01")
  private val ageOf19: LocalDate            = ageOf19YearsAgo(testDate)
  private val ageOf16Before31Aug: LocalDate = ageOf16WithBirthdayBefore31stAugust(testDate)
  private val ageOfExactly15: LocalDate     = ageExactly15Relative(testDate)

  "WhoHasChildcareCosts Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller(getRequiredData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual viewAsString()
    }

    "return OK and only display the children that are under 16 and exact 16 with DOB before 31st august and disabled" in {
      val children = Map(
        "Over16" -> "0",
        "Under16_1" -> "1",
        "Under16_2" -> "2",
        "exact16WithBirthdayBefore31stAugust" -> "3"
      )

      val dataWithOneChildOver16 = requiredData(children) + (AboutYourChildId.toString -> Json.obj(
        "0" -> Json.toJson(AboutYourChild("Over16", ageOf19)),
        "1" -> Json.toJson(AboutYourChild("Under16_1", ageOfExactly15)),
        "2" -> Json.toJson(AboutYourChild("Under16_2", ageOfExactly15)),
        "3" -> Json.toJson(AboutYourChild("exact16WithBirthdayBefore31stAugust", ageOf16Before31Aug)))) +
        (WhichChildrenBlindId.toString -> Json.toJson(Seq(2))) +
        (WhichChildrenDisabilityId.toString -> Json.toJson(Seq(0, 3)))

      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, dataWithOneChildOver16)), Some(testDate))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustEqual viewAsString(WhoHasChildcareCostsForm().fill(Set(0)),
        Map("Under16_1" -> "1", "Under16_2" -> "2","exact16WithBirthdayBefore31stAugust" ->"3"))
    }

    Seq(
      Map("Foo" -> "0", "Bar" -> "1"),
      Map("Spoon" -> "2", "Fork" -> "3")
    ).zipWithIndex.foreach {
      case(values, i) =>

        val value = values.values.toSeq.head

        s"populate the view correctly on a GET when the question has previously been answered $i" in {
          val validData = requiredData(values) + (
            WhoHasChildcareCostsId.toString -> Json.toJson(Seq(value.toInt))
            )
          val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)), Some(testDate))

          val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

          contentAsString(result) mustEqual viewAsString(WhoHasChildcareCostsForm().fill(Set(value.toInt)), values)
        }

        s"redirect to the next page when valid data is submitted $i" in {
          val postRequest = fakeRequest.withFormUrlEncodedBody("value[0]" -> value)

          val result = controller(getRequiredData(values)).onSubmit(NormalMode)(postRequest)

          status(result) mustEqual SEE_OTHER
          redirectLocation(result).value mustEqual onwardRoute.url
        }

    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value[0]", "invalid value"))
      val boundForm = WhoHasChildcareCostsForm().bind(Map("value[0]" -> "invalid value"))

      val result = controller(getRequiredData).onSubmit(NormalMode)(postRequest)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> "0")
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a GET if required data is missing" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }

    "redirect to Session Expired for a POST if required data is missing" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> "0")
      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad().url
    }
  }
  def onwardRoute: Call = {
    routes.WhatToTellTheCalculatorController.onPageLoad()
  }

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap): WhoHasChildcareCostsController =
    new WhoHasChildcareCostsController(frontendAppConfig, mcc,
      FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction, view)

  val defaultValues: Map[String, String] = Map("Foo" -> "0", "Bar" ->"1")

  def viewAsString(
                    form: Form[_] = WhoHasChildcareCostsForm(0, 1),
                    values: Map[String, String] = defaultValues
                  ): String =
    view(frontendAppConfig, form, NormalMode, values)(fakeRequest, messages).toString


  def requiredData(values: Map[String, String]): Map[String, JsValue] = Map(
    AboutYourChildId.toString -> Json.obj(
      values.map {
        case (name, v) =>
          v -> (Json.toJson(AboutYourChild(name, LocalDate.now)): JsValueWrapper)
      }.toSeq: _*
    )
  )

  def getRequiredData(values: Map[String, String]): DataRetrievalAction =
    new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData(values))), Some(testDate))

  def getRequiredData: DataRetrievalAction = getRequiredData(defaultValues)


}
