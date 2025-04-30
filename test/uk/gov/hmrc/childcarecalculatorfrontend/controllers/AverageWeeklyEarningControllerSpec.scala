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

import org.scalatestplus.mockito.MockitoSugar
import play.api.data.Form
import play.api.libs.json.JsString
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, YouPartnerBothEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{CacheMap, Utils}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.{
  partnerAverageWeeklyEarnings,
  yourAndPartnerAverageWeeklyEarnings,
  yourAverageWeeklyEarnings
}

class AverageWeeklyEarningControllerSpec extends ControllerSpecBase with MockitoSugar {

  val mockUtils         = mock[Utils]
  val partnerView       = application.injector.instanceOf[partnerAverageWeeklyEarnings]
  val youAndPartnerView = application.injector.instanceOf[yourAndPartnerAverageWeeklyEarnings]
  val yourView          = application.injector.instanceOf[yourAverageWeeklyEarnings]

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) = new AverageWeeklyEarningController(
    mcc,
    dataRetrievalAction,
    new DataRequiredAction,
    yourView,
    partnerView,
    youAndPartnerView
  )

  def averageWeeklyEarningRoute = routes.AverageWeeklyEarningController.onPageLoad(NormalMode)
  def viewAsYourString(form: Form[Boolean] = BooleanForm())    = yourView()(fakeRequest, messages).toString
  def viewAsPartnerString(form: Form[Boolean] = BooleanForm()) = partnerView()(fakeRequest, messages).toString
  def viewAsBothString(form: Form[Boolean] = BooleanForm())    = youAndPartnerView()(fakeRequest, messages).toString

  "AverageWeeklyEarning Controller" must {
    "return OK and the correct view in case of you in paid work" in {

      val validData       = Map(WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.YOU.toString))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsYourString()
    }
    "return OK and the correct view in case of BOTH in paid work" in {

      val validData       = Map(WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.BOTH.toString))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsBothString()
    }
    "return OK and the correct view in case of partner in paid work" in {

      val validData       = Map(WhoIsInPaidEmploymentId.toString -> JsString(YouPartnerBothEnum.PARTNER.toString))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsPartnerString()
    }
  }

}
