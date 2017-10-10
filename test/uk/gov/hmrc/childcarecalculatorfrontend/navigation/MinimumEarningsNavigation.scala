package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.YourMinimumEarningsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.{Navigator, SpecBase}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.http.cache.client.CacheMap


class MinimumEarningsNavigation extends SpecBase with MockitoSugar{

  def userAnswers(answers: (String, JsValue)*): UserAnswers =
    new UserAnswers(CacheMap("", Map(answers: _*)))

  val navigator = new Navigator(new Schemes())

  "Partner Minimum Earnings Navigation" when {

    "in Normal mode" must {

      "redirect to your maximum earnings page if partner earns more than NMW" in {
        val answers = spy(userAnswers())
        when(answers.partnerMinimumEarnings) thenReturn Some(true)

        navigator.nextPage(YourMinimumEarningsId, NormalMode)(answers) mustBe
          routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
      }

      "redirect to your self employed or apprentice page if " in {

      }

      "" in {

      }

      "" in {

      }


    }
  }

}
