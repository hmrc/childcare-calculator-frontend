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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import org.scalatest.OptionValues
import play.api.data.Form
import play.api.libs.json.Json.JsValueWrapper
import play.api.libs.json.{JsBoolean, JsNumber, JsValue, Json}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichDisabilityBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{AboutYourChild, DisabilityBenefits}
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichDisabilityBenefits

import java.time.LocalDate
import scala.util.Random

class WhichDisabilityBenefitsControllerSpec extends ControllerSpecBase with OptionValues {

  val view = application.injector.instanceOf[whichDisabilityBenefits]

  "WhichDisabilityBenefits Controller" must {

    val cases: LazyList[(Int, String)] = {
      val indices = LazyList.from(Random.nextInt(15))
      val names   = LazyList.continually(Random.alphanumeric.take(5).mkString)
      indices.zip(names).take(3)
    }.distinct

    cases.foreach { case (index, name) =>

      s"return OK and the correct view for a GET, for index: $index, name: $name" in {
        val result = controller(getRequiredData(cases: _*)).onPageLoad(index)(fakeRequest)
        status(result) mustEqual OK
        contentAsString(result) mustEqual viewAsString(index = index, name = name)
      }

      s"populate the view correctly on a GET when the question has previously been answered, for index: $index, name: $name" in {
        val validData = requiredData(cases) + (
          WhichDisabilityBenefitsId.toString -> Json.obj(
            cases.map { case (i, _) =>
              i.toString -> (Seq(DisabilityBenefits(0).toString): JsValueWrapper)
            }: _*
          )
        )
        val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

        val result = controller(getRelevantData).onPageLoad(index)(fakeRequest)

        contentAsString(result) mustEqual viewAsString(
          WhichDisabilityBenefitsForm(name).fill(Set(DisabilityBenefits(0))),
          index,
          name
        )
      }

      s"redirect to the next page when valid data is submitted, for index: $index, name: $name" in {
        val postRequest =
          fakeRequest.withFormUrlEncodedBody("value[0]" -> DisabilityBenefits(0).toString).withMethod("POST")

        val result = controller(getRequiredData(cases: _*)).onSubmit(index)(postRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value[0]", "invalid value")).withMethod("POST")
      val boundForm   = WhichDisabilityBenefitsForm("Foo").bind(Map("value[0]" -> "invalid value"))

      val result = controller(getRequiredData(0 -> "Foo")).onSubmit(0)(postRequest)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(0)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a POST if no existing cacheMap is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> DisabilityBenefits(0).toString).withMethod("POST")
      val result      = controller(dontGetAnyData).onSubmit(0)(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a GET if required cacheMap is missing" in {
      val result = controller().onPageLoad(0)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a POST if required data is missing" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> DisabilityBenefits(0).toString).withMethod("POST")
      val result      = controller().onSubmit(0)(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a GET if index is negative" in {
      val result = controller(getRequiredData(0 -> "Foo")).onPageLoad(-1)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a POST if index is negative" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> DisabilityBenefits(0).toString).withMethod("POST")
      val result      = controller(getRequiredData(0 -> "Foo")).onSubmit(-1)(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a GET if index is out of bounds" in {
      val result = controller(getRequiredData(0 -> "Foo")).onPageLoad(1)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a POST if index is out of bounds" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody("value" -> DisabilityBenefits(0).toString).withMethod("POST")
      val result      = controller(getRequiredData(0 -> "Foo")).onSubmit(1)(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }
  }

  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new WhichDisabilityBenefitsController(
      frontendAppConfig,
      mcc,
      FakeDataCacheService,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction,
      view
    )

  def viewAsString(form: Form[Set[DisabilityBenefits.Value]]): String =
    viewAsString(form, 0, "Foo")

  def viewAsString(
      index: Int,
      name: String
  ): String = viewAsString(WhichDisabilityBenefitsForm(name), index, name)

  def viewAsString(
      form: Form[Set[DisabilityBenefits.Value]],
      index: Int,
      name: String
  ): String =
    view(frontendAppConfig, form, index, name)(fakeRequest, messages).toString

  def requiredData(cases: Seq[(Int, String)]): Map[String, JsValue] =
    if (cases.size == 1) {
      Map(
        NoOfChildrenId.toString               -> JsNumber(1),
        ChildrenDisabilityBenefitsId.toString -> JsBoolean(true),
        AboutYourChildId.toString -> Json.obj(
          cases.head._1.toString -> (Json.toJson(AboutYourChild(cases.head._2, LocalDate.now)): JsValueWrapper)
        )
      )
    } else {
      Map(
        WhichChildrenDisabilityId.toString -> Json.toJson(cases.map(_._1)),
        AboutYourChildId.toString -> Json.obj(
          cases.map { case (index, name) =>
            index.toString -> (Json.toJson(AboutYourChild(name, LocalDate.now)): JsValueWrapper)
          }: _*
        )
      )
    }

  def getRequiredData(cases: (Int, String)*) =
    new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData(cases))))

}
