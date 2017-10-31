package uk.gov.hmrc.childcarecalculatorfrontend.models.schemes

import javax.inject.Inject

import uk.gov.hmrc.childcarecalculatorfrontend.models.{Eligibility, NotDetermined}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers

class TaxCredits @Inject() () extends Scheme {
  override def eligibility(answers: UserAnswers): Eligibility = NotDetermined
}
