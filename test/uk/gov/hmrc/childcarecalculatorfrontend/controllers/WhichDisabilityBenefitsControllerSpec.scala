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
import play.api.libs.json.JsValue
import play.api.mvc.Call
import play.api.test.Helpers.*
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.*
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichDisabilityBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.AboutYourChild
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.DisabilityBenefit
import uk.gov.hmrc.childcarecalculatorfrontend.services.FakeDataCacheService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichDisabilityBenefits

import java.time.LocalDate
import scala.util.Random

class WhichDisabilityBenefitsControllerSpec extends ControllerSpecBase with OptionValues {

  val view: whichDisabilityBenefits = inject[whichDisabilityBenefits]

  def onwardRoute: Call = routes.WhatToTellTheCalculatorController.onPageLoad

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new WhichDisabilityBenefitsController(
      mcc,
      FakeDataCacheService,
      new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction,
      new DataRequiredAction,
      view
    )

  def viewAsString(form: Form[Set[DisabilityBenefit]]): String =
    viewAsString(form, 0, "Foo")

  def viewAsString(
      index: Int,
      name: String
  ): String = viewAsString(WhichDisabilityBenefitsForm(name), index, name)

  def viewAsString(
      form: Form[Set[DisabilityBenefit]],
      index: Int,
      name: String
  ): String =
    view(form, index, name)(using fakeRequest, messages).toString

  def requiredData(cases: Map[Int, String]): Map[String, JsValue] =
    if (cases.size == 1) {
      val (index, name) = cases.head

      Map(
        NoOfChildrenId.withValue(1),
        ChildrenDisabilityBenefitsId.withValue(true),
        AboutYourChildId.withValue(Map(index -> AboutYourChild(name, LocalDate.of(2026, 7, 27))))
      )
    } else {
      Map(
        WhichChildrenDisabilityId.withValue(cases.keySet),
        AboutYourChildId.withValue(
          cases.map { case (index, name) =>
            index -> AboutYourChild(name, LocalDate.of(2026, 7, 27))
          }
        )
      )
    }

  def getRequiredData(cases: Map[Int, String]) =
    new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, requiredData(cases))))

  "WhichDisabilityBenefits Controller" must {

    val cases: Map[Int, String] = {
      val indices = LazyList.from(Random.nextInt(15))
      val names   = LazyList.continually(Random.alphanumeric.take(5).mkString)
      indices.zip(names).take(3)
    }.distinct.toMap

    cases.foreach { case (index, name) =>

      s"return OK and the correct view for a GET, for index: $index, name: $name" in {
        val result = controller(getRequiredData(cases)).onPageLoad(index)(fakeRequest)
        status(result) mustEqual OK
        contentAsString(result) mustEqual viewAsString(index = index, name = name)
      }

      s"populate the view correctly on a GET when the question has previously been answered, for index: $index, name: $name" in {
        val validData = requiredData(cases) + WhichDisabilityBenefitsId.withValue(
          cases.map(_._1 -> Set(DisabilityBenefit.DisabilityBenefits))
        )
        val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

        val result = controller(getRelevantData).onPageLoad(index)(fakeRequest)

        contentAsString(result) mustEqual viewAsString(
          WhichDisabilityBenefitsForm(name).fill(Set(DisabilityBenefit.DisabilityBenefits)),
          index,
          name
        )
      }

      s"redirect to the next page when valid data is submitted, for index: $index, name: $name" in {
        val postRequest =
          fakeRequest
            .withFormUrlEncodedBody("value[0]" -> DisabilityBenefit.DisabilityBenefits.toString)
            .withMethod("POST")

        val result = controller(getRequiredData(cases)).onSubmit(index)(postRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual onwardRoute.url
      }
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value[0]", "invalid value")).withMethod("POST")
      val boundForm   = WhichDisabilityBenefitsForm("Foo").bind(Map("value[0]" -> "invalid value"))

      val result = controller(getRequiredData(Map(0 -> "Foo"))).onSubmit(0)(postRequest)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(0)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a POST if no existing cacheMap is found" in {
      val postRequest =
        fakeRequest.withFormUrlEncodedBody("value" -> DisabilityBenefit.DisabilityBenefits.toString).withMethod("POST")
      val result = controller(dontGetAnyData).onSubmit(0)(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a GET if required cacheMap is missing" in {
      val result = controller().onPageLoad(0)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a POST if required data is missing" in {
      val postRequest =
        fakeRequest.withFormUrlEncodedBody("value" -> DisabilityBenefit.DisabilityBenefits.toString).withMethod("POST")
      val result = controller().onSubmit(0)(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a GET if index is negative" in {
      val result = controller(getRequiredData(Map(0 -> "Foo"))).onPageLoad(-1)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a POST if index is negative" in {
      val postRequest =
        fakeRequest.withFormUrlEncodedBody("value" -> DisabilityBenefit.DisabilityBenefits.toString).withMethod("POST")
      val result = controller(getRequiredData(Map(0 -> "Foo"))).onSubmit(-1)(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a GET if index is out of bounds" in {
      val result = controller(getRequiredData(Map(0 -> "Foo"))).onPageLoad(1)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }

    "redirect to Session Expired for a POST if index is out of bounds" in {
      val postRequest =
        fakeRequest.withFormUrlEncodedBody("value" -> DisabilityBenefit.DisabilityBenefits.toString).withMethod("POST")
      val result = controller(getRequiredData(Map(0 -> "Foo"))).onSubmit(1)(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual routes.SessionExpiredController.onPageLoad.url
    }
  }

}
