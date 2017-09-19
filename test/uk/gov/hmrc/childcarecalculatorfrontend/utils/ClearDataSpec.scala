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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import uk.gov.hmrc.childcarecalculatorfrontend.models.{Claimant, YouPartnerBothEnum, LocationEnum, YesNoUnsureEnum}
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, ObjectBuilder}
import uk.gov.hmrc.play.test.UnitSpec

class ClearDataSpec extends UnitSpec with FakeCCApplication with ObjectBuilder with CCConstants{

  "ClearData" must {

    "clear data for livingWithPartnerController " when {

      "user changes the selection from yes to No for Do have a partner question" should {
        "reset data to default for all further partner pages" in {

          val parent = buildClaimant.copy(
            ageRange = None,
            benefits = Some(buildBenefits),
            lastYearlyIncome = None,
            currentYearlyIncome = None,
            hours = Some(20),
            minimumEarnings = None,
            escVouchers = Some(YesNoUnsureEnum.NOTSURE),
            maximumEarnings = None
          )
          val partner = parent.copy(benefits = Some(buildBenefits))

          val houseHoldModel = buildHousehold.copy(tcUcBenefits = None,
            location = LocationEnum.NORTHERNIRELAND,
            children = List(),
            parent = parent,
            partner = Some(partner))

          val model = buildPageObjects.copy(household = houseHoldModel,
            childAgedTwo = None,
            childAgedThreeOrFour = Some(false),
            expectChildcareCosts = Some(true),
            livingWithPartner = Some(true),
            paidOrSelfEmployed = Some(true),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
            getVouchers = Some(YesNoUnsureEnum.NOTSURE),
            whoGetsVouchers = None,
            getBenefits = Some(true),
            getMaximumEarnings = None)

          val expectedModel = model.copy(
            household = model.household.copy(partner = None, parent = model.household.parent.copy(benefits = None)),
            livingWithPartner = Some(false),
            paidOrSelfEmployed = None,
            whichOfYouInPaidEmployment = None,
            getVouchers = None,
            whoGetsVouchers = None,
            getBenefits = None,
            getMaximumEarnings = None
          )

          val result = ClearData.clearData(livingWithPartnerController, false, model)

          result shouldBe Some(expectedModel)


        }
      }

      "user changes the selection from No to yes for Do have a partner question " should {
        "data for all further parent pages must be reset to default" in {

          val parent = buildClaimant.copy(
            ageRange = None,
            benefits = None,
            lastYearlyIncome = None,
            currentYearlyIncome = None,
            hours = Some(20),
            minimumEarnings = None,
            escVouchers = Some(YesNoUnsureEnum.NOTSURE),
            maximumEarnings = None
          )

          val houseHoldModel = buildHousehold.copy(tcUcBenefits = None,
            location = LocationEnum.NORTHERNIRELAND,
            children = List(),
            parent = parent,
            partner = None)

          val model = buildPageObjects.copy(household = houseHoldModel,
            childAgedTwo = None,
            childAgedThreeOrFour = Some(false),
            expectChildcareCosts = Some(true),
            livingWithPartner = Some(false),
            paidOrSelfEmployed = Some(true),
            whichOfYouInPaidEmployment = Some(YouPartnerBothEnum.BOTH),
            getVouchers = Some(YesNoUnsureEnum.NOTSURE),
            whoGetsVouchers = None,
            getBenefits = Some(true),
            getMaximumEarnings = None)

          val expectedModel = model.copy(
            household = model.household.copy(parent = Claimant(),
                                             partner = Some(model.household.partner.fold(Claimant())(_.copy(benefits = None)))),
            livingWithPartner = Some(true),
            paidOrSelfEmployed = None,
            whichOfYouInPaidEmployment = None,
            getVouchers = None,
            whoGetsVouchers = None,
            getBenefits = None,
            getMaximumEarnings = None
          )

          val result = ClearData.clearData(livingWithPartnerController, true, model)

          result shouldBe Some(expectedModel)


        }
      }

      "user does not change the answer and its Yes for Do have a partner question" should {
        "return the same PageObjects" in {

          val model = buildPageObjects.copy(livingWithPartner = Some(true))
          val result =  ClearData.clearData(livingWithPartnerController, true, model)

          result shouldBe Some(model)
        }
      }

      "user does not change the answer and its No for Do have a partner question" should {
        "return the same PageObjects" in {

          val model = buildPageObjects.copy(livingWithPartner = Some(false))
          val result =  ClearData.clearData(livingWithPartnerController, false, model)

          result shouldBe Some(model)
        }
      }

    }

    "return None as the result" when {
      "key is not present in controllerToClearDataMap" in {
        val result = ClearData.clearData("keyNotPresent", true, buildPageObjects)

        result shouldBe None
      }
    }
  }
}
