package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.SubNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.Identifier
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

trait ResultsNavigator extends SubNavigator {

  protected def schemes: Schemes
  protected def resultLocation: Call

  protected def resultsMap: PartialFunction[Identifier, UserAnswers => Call] = Map.empty

  override def nextPage(id: Identifier, mode: Mode): Option[UserAnswers => Call] = {
    resultsMap.lift(id).map {
      route =>
        (userAnswers: UserAnswers) =>
          if (schemes.allSchemesDetermined(userAnswers)) {
            resultLocation
          } else {
            route(userAnswers)
          }
    } orElse super.nextPage(id, mode)
  }
}
