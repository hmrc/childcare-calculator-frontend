package uk.gov.hmrc.childcarecalculatorfrontend.utils

import org.mockito.Mockito.spy
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.http.cache.client.CacheMap

class SessionExpiredRouterSpec extends SpecBase {
  "Session Expired Router" should {
    "Route to session expired controller" in {
      val answers = spy(userAnswers())
      val result = SessionExpiredRouter.route("test","test",Some(answers))

      result mustBe routes.SessionExpiredController.onPageLoad()
    }

    "Be able to cope with no UserAnswers" in {
      val result = SessionExpiredRouter.route("test","test",None)

      result mustBe routes.SessionExpiredController.onPageLoad()
    }

    "Be able to cope with UserAnswers with populated cache map" in {
      val answers = spy(userAnswers())
      val result = SessionExpiredRouter.route("test","test",Some(answers))

      result mustBe routes.SessionExpiredController.onPageLoad()
    }

    "Be able to cope with UserAnswers with no cache map" in {
      val result = SessionExpiredRouter.route("test","test",Some(new UserAnswers(null)))

      result mustBe routes.SessionExpiredController.onPageLoad()
    }
  }

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))
}
