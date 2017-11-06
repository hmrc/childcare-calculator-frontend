package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.mockito.MockitoSugar
import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class ResultsNavigatorSpec extends WordSpec with MustMatchers with OptionValues with MockitoSugar {

  ".nextPage" must {

    "return `resultLocation` for a route in `resultsMap` when all schemes are determined" in {
      val schemes: Schemes = mock[Schemes]
      when(schemes.allSchemesDetermined(any())).thenReturn(true)
      val result = navigator(schemes).nextPage(LocationId, NormalMode).value(mock[UserAnswers])
      result mustEqual resultPage
    }

    "prefer `resultsMap` over `routeMap`" in {
      val schemes: Schemes = mock[Schemes]
      when(schemes.allSchemesDetermined(any())).thenReturn(false)
      val result = navigator(schemes).nextPage(LocationId, NormalMode).value(mock[UserAnswers])
      result mustEqual routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
    }

    "fall back to `routeMap`" in {
      val schemes: Schemes = mock[Schemes]
      when(schemes.allSchemesDetermined(any())).thenReturn(true)
      val result = navigator(schemes).nextPage(ChildAgedTwoId, NormalMode).value(mock[UserAnswers])
      result mustEqual routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode)
    }
  }

  lazy val resultPage: Call = routes.FreeHoursResultController.onPageLoad()

  def navigator(s: Schemes): SubNavigator = new SubNavigator with ResultsNavigator {

    override protected val schemes: Schemes = s
    override protected val resultLocation: Call = resultPage

    override protected val routeMap: PartialFunction[Identifier, UserAnswers => Call] = Map(
      LocationId -> (_ => routes.ChildAgedTwoController.onPageLoad(NormalMode)),
      ChildAgedTwoId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode))
    )

    override protected val resultsMap: PartialFunction[Identifier, UserAnswers => Call] = Map(
      LocationId -> (_ => routes.ChildAgedThreeOrFourController.onPageLoad(NormalMode))
    )
  }
}
