/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend

trait CCRoutes {

  val rootPath: String = "/childcare-calc"

  private def path(endPoint: String): String = s"${rootPath}${endPoint}"

  // TODO: Delete when all pages are ready
  val underConstrctionPath: String = path("/under-construction")

  val technicalDifficultiesPath: String = path("/error")
  val whatYouNeedPath: String = path("/what-you-need")
  val locationPath: String = path("/location")
  val childAgedTwoPath: String = path("/child-aged-two")
  val childAgedTwoEditPath: String = path("/child-aged-two/edit")
  val childAgedThreeOrFourPath: String = path("/child-aged-three-or-four")
  val childAgedThreeOrFourEditPath: String = path("/child-aged-three-or-four/edit")
  val expectChildcareCostsPath: String = path("/expect-childcare-costs")
  val expectChildcareCostsEditPath: String = path("/expect-childcare-costs/edit")
  val freeHoursInfoPath: String = path("/free-hours-info")
  val freeHoursResultsPath: String = path("/free-hours-results")
  val livingWithPartnerPath: String = path("/do-you-have-a-partner")
  val paidEmploymentPath: String = path("/paid-employment")
  val whoIsInPaidEmploymentPath: String = path("/who-paid-employment")
  val hoursParentPath: String = path("/hours-worked/parent")
  val hoursPartnerPath: String = path("/hours-worked/partner")
  val vouchersPath: String = path("/vouchers")
  val getBenefitsPath: String = path("/get-benefits")
  val whoGetsBenefitsPath: String = path("/who-gets-benefits")
  val parentBenefitsPath: String = path("/benefits/parent")
  val partnerBenefitsPath: String = path("/benefits/partner")
  val whatsYourAgePath: String = path("/whats-your-age")
  val parentMinimumEarningsPath: String = path("/minimum-earnings/parent")
  val partnerMinimumEarningsPath: String = path("/minimum-earnings/partner")

}
