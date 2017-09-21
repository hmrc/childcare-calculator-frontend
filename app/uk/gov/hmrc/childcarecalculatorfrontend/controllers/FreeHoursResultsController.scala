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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.{Inject, Singleton}

import org.joda.time.LocalDate
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.EligibilityConnector
import uk.gov.hmrc.childcarecalculatorfrontend.models.AgeRangeEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentStatusEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.LocationEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.PeriodEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models.YesNoUnsureEnum._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.services.KeystoreService
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.freeHoursResults

import scala.concurrent.Future

@Singleton
class FreeHoursResultsController @Inject()(val messagesApi: MessagesApi) extends I18nSupport with BaseController {

  val keystore: KeystoreService = KeystoreService
  // TODO: Delete it once we get real results page
//  val connector: EligibilityConnector = EligibilityConnector

  def onPageLoad: Action[AnyContent] = withSession { implicit request =>
    keystore.fetch[PageObjects]().map {
      case Some(pageObjects) =>

        // TODO: This is just for test purposes to make sure connection is fine
        // TODO: It should be deleted and connection to eligibility should be done before final results using real data
        // TODO: Delete it once we get real results page
//        val testObject = Household(
//          credits = None,
//          location = LocationEnum.ENGLAND,
//          children = List(
//            Child(
//              id = 1,
//              name = "Child 1",
//              dob = Some(LocalDate.parse("2011-01-01")),
//              disability = Some(
//                Disability(
//                  disabled = false,
//                  severelyDisabled = false,
//                  blind = false
//                )
//              ),
//              childcareCost = Some(
//                ChildCareCost(
//                  amount = Some(200),
//                  period = Some(PeriodEnum.MONTHLY)
//                )
//              ),
//              education = None
//            )
//          ),
//          parent = Claimant(
//            ageRange = Some(AgeRangeEnum.OVERTWENTYFOUR),
//            benefits = Some(
//              Benefits(
//                disabilityBenefits = false,
//                highRateDisabilityBenefits = false,
//                incomeBenefits = false,
//                carersAllowance = false
//              )
//            ),
//            lastYearlyIncome = Some(
//              Income(
//                employmentIncome = Some(25000),
//                pension = None,
//                otherIncome = None,
//                benefits = None,
//                statutoryIncome = None
//              )
//            ),
//            currentYearlyIncome = Some(
//              Income(
//                employmentIncome = Some(25000),
//                pension = None,
//                otherIncome = None,
//                benefits = None,
//                statutoryIncome = None
//              )
//            ),
//            hours = Some(37.5),
//            minimumEarnings = Some(
//              MinimumEarnings(
//                amount = 130,
//                employmentStatus = None,
//                selfEmployedIn12Months = None
//              )
//            ),
//            escVouchers = Some(YesNoUnsureEnum.YES)
//          ),
//          partner = None
//        )
////        connector.getEligibility(testObject).map { result =>
////          Logger.warn("----------- Test result: " + result)
////        }
        Ok(freeHoursResults(pageObjects.household.childAgedThreeOrFour.getOrElse(false), pageObjects.household.location))
      case _ =>
        Logger.warn("PageObjects object is missing in FreeHoursResultsController.onPageLoad")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())

    } recover {
      case ex: Exception =>
        Logger.warn(s"Exception from FreeHoursResultsController.onPageLoad: ${ex.getMessage}")
        Redirect(routes.ChildCareBaseController.onTechnicalDifficulties())
    }
  }
}
