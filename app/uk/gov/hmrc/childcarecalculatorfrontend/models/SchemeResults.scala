/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.childcarecalculatorfrontend.models.SchemeEnum.SchemeEnum

case class EscClaimantEligibility(
                                   parent: Boolean = false,
                                   partner: Boolean = false
                                 )

object EscClaimantEligibility {
  implicit val escClaimantEligibilityFormat: OFormat[EscClaimantEligibility] = Json.format[EscClaimantEligibility]
}

case class TaxCreditsEligibility(
                                  wtcEligibility: Boolean = false,
                                  ctcEligibility: Boolean = false
                                 )

object TaxCreditsEligibility {
  implicit val taxCreditsEligibilityFormat: OFormat[TaxCreditsEligibility] = Json.format[TaxCreditsEligibility]
}

case class Scheme(name: SchemeEnum,
                  amount: BigDecimal ,
                  escClaimantEligibility: Option[EscClaimantEligibility] = None,
                  taxCreditsEligibility: Option[TaxCreditsEligibility] = None
                 ) {
  val missingEscClaimantEligibility: Boolean = name == SchemeEnum.ESCELIGIBILITY && escClaimantEligibility.isEmpty
  val missingTaxCreditsEligibility: Boolean = name == SchemeEnum.TCELIGIBILITY && taxCreditsEligibility.isEmpty
  require(!missingEscClaimantEligibility,"Missing values for escClaimantEligibility")
  require(!missingTaxCreditsEligibility,"Missing values for taxCreditsEligibility")
}

object Scheme {
  implicit val schemeFormat: OFormat[Scheme] = Json.format[Scheme]
}

case class SchemeResults (
                           schemes: List[Scheme],
                           tfcRollout: Boolean = false,
                           thirtyHrsRollout: Boolean = false
                         )

object SchemeResults {
  implicit val schemeResultsFormat: OFormat[SchemeResults] = Json.format[SchemeResults]
}
