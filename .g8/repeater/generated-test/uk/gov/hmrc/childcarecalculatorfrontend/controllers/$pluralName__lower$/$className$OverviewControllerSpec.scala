package uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$

import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.ControllerSpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.{routes => baseRoutes}
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.$className$OverviewViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$pluralName;format="lower"$.$className;format="decap"$Overview

class $className$OverviewControllerSpec extends ControllerSpecBase {

  def onwardRoute = baseRoutes.WhatToTellTheCalculatorController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new $className$OverviewController(frontendAppConfig, messagesApi, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction)

  def viewAsString(viewModel: $className$OverviewViewModel) = $className;format="decap"$Overview(frontendAppConfig, viewModel, NormalMode)(fakeRequest, messages).toString

  "$className$ Overview Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)
      val viewModel = $className$OverviewViewModel(Seq(), routes.Add$className$Controller.onPageLoad(NormalMode), onwardRoute)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString(viewModel)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(baseRoutes.SessionExpiredController.onPageLoad().url)
    }
  }
}
