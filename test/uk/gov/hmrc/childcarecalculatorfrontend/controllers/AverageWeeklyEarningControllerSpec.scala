/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.libs.json.{JsString, JsValue}
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.{
  partnerAverageWeeklyEarnings,
  yourAndPartnerAverageWeeklyEarnings,
  yourAverageWeeklyEarnings
}

class AverageWeeklyEarningControllerSpec extends ControllerSpecBase {

  val partnerView            = application.injector.instanceOf[partnerAverageWeeklyEarnings]
  val youAndPartnerView      = application.injector.instanceOf[yourAndPartnerAverageWeeklyEarnings]
  val yourView               = application.injector.instanceOf[yourAverageWeeklyEarnings]
  val location               = Location.ENGLAND
  val locationMap            = LocationId.toString -> JsString(location.toString)
  val cacheMapWithLocation   = new CacheMap("id", Map(LocationId.toString -> JsString(location.toString)))
  val getDataWithLocationSet = new FakeDataRetrievalAction(Some(cacheMapWithLocation))

  def controller(dataRetrievalAction: DataRetrievalAction = getDataWithLocationSet) =
    new AverageWeeklyEarningController(
      mcc,
      dataRetrievalAction,
      new DataRequiredAction,
      yourView,
      partnerView,
      youAndPartnerView
    )

  def averageWeeklyEarningRoute = routes.AverageWeeklyEarningController.onPageLoad()
  def viewAsYourString          = yourView(location)(fakeRequest, messages).toString
  def viewAsPartnerString       = partnerView(location)(fakeRequest, messages).toString
  def viewAsBothString          = youAndPartnerView(location)(fakeRequest, messages).toString

  "AverageWeeklyEarning Controller" must {
    "return OK and the correct view in case of you in paid work" in {
      val validData = Map(WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.YOU.toString), locationMap)
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsYourString
    }
    "return OK and the correct view in case of BOTH in paid work" in {

      val validData = Map(WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.BOTH.toString), locationMap)
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsBothString
    }
    "return OK and the correct view in case of partner in paid work" in {

      val validData =
        Map(WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.PARTNER.toString), locationMap)
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad()(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsPartnerString
    }

    "redirect to location page when who is paid employment has been answered and there is no location in user data" in {
      val validData       = Map(WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.PARTNER.toString))
      val missingLocation = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(missingLocation).onPageLoad()(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.LocationController.onPageLoad().url)
    }
  }

}
