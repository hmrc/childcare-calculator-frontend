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

import uk.gov.hmrc.childcarecalculatorfrontend.models.{Claimant, PageObjects}


trait ClearData extends CCConstants{


  /**
    * Clears the data according to the need of passed controllerId
    *
    * @param controllerId
    * @param fieldValue
    * @param pageObjects
    * @return
    */
  def clearData(controllerId: String, fieldValue: Boolean, pageObjects: PageObjects): Option[PageObjects] ={
    controllerToClearDataMap(controllerId, fieldValue, pageObjects).get(controllerId)
  }


  /**
    * Returns updated pageObjects after clearing the data for the passed controllerId
    *
    * @param controllerId
    * @param fieldValue
    * @param pageObjects
    * @return
    */
  private def controllerToClearDataMap(controllerId: String, fieldValue: Boolean, pageObjects: PageObjects): Map[String, PageObjects] = {
    Map(
      livingWithPartnerController -> clearDataForLivingWithPartner(fieldValue, pageObjects)
    )
  }

  /**
    * Clear the data for LivingWithPartner page
    *
    * @param fieldValue
    * @param pageObjects
    * @return
    */
  private def clearDataForLivingWithPartner(fieldValue: Boolean, pageObjects: PageObjects): PageObjects = {
    val existingValueForLivingWithPartner = pageObjects.livingWithPartner.getOrElse(false)
    val hasSelectionBeenChanged = if(existingValueForLivingWithPartner == fieldValue) false else true

    if(hasSelectionBeenChanged){
      (existingValueForLivingWithPartner, fieldValue) match {
        case (true, false) => {
          pageObjects.copy(household = pageObjects.household.copy(partner = None,
            parent = pageObjects.household.parent.copy(benefits = None)),
            livingWithPartner = Some(false),
            paidOrSelfEmployed = None,
            whichOfYouInPaidEmployment = None,
            getVouchers = None,
            whoGetsVouchers = None,
            getBenefits = None,
            getMaximumEarnings = None
          )
        }
        case (false, true) => {
          pageObjects.copy(household = pageObjects.household.copy(parent = Claimant(),
            partner = Some(pageObjects.household.partner.fold(Claimant())(_.copy(benefits = None)))),
            livingWithPartner = Some(true),
            paidOrSelfEmployed = None,
            whichOfYouInPaidEmployment = None,
            getVouchers = None,
            whoGetsVouchers = None,
            getBenefits = None,
            getMaximumEarnings = None
          )
        }
        case _ => pageObjects
      }

    }else{
      pageObjects
    }


  }

}

object ClearData extends ClearData
