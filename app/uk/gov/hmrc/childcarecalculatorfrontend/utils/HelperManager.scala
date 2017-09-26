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

import java.text.SimpleDateFormat

import org.joda.time.LocalDate
import play.api.Play.current
import play.api.{Configuration, Play}
import uk.gov.hmrc.childcarecalculatorfrontend.models.YouPartnerBothEnum.YouPartnerBothEnum
import uk.gov.hmrc.childcarecalculatorfrontend.models.{EmploymentStatusEnum, LocationEnum, PageObjects, YouPartnerBothEnum}

trait HelperManager {

  def defineMinimumEarnings(isPartner: Boolean, pageObjects: PageObjects): Option[Boolean] = {
    if(isPartner) {
      if(pageObjects.household.partner.isDefined && pageObjects.household.partner.get.minimumEarnings.isDefined &&
        pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW.isDefined) {
        pageObjects.household.partner.get.minimumEarnings.get.earnMoreThanNMW
      } else {
        None
      }
    } else {
      if(pageObjects.household.parent.minimumEarnings.isDefined && pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW.isDefined) {
        pageObjects.household.parent.minimumEarnings.get.earnMoreThanNMW
      } else {
        None
      }
    }
  }

  def getMinimumEarningsAmountForAgeRange(ageRange: Option[String]): Int = {
    val nmwConfig: Configuration = getNMWConfig(LocalDate.now)

    nmwConfig.getInt(ageRange.getOrElse("non-existing-age")).get
  }

  def getLatestConfig(configType: String, currentDate: LocalDate): Configuration = {
    val dateFormat = new SimpleDateFormat("dd-MM-yyyy")
    val configs: Seq[Configuration] = Play.application.configuration.getConfigSeq(configType).getOrElse(Seq())

    val configsExcludingDefault: Seq[Configuration] = configs.filterNot(
      _.getString("rule-date").equals(Some("default"))
    ).sortWith(
        (conf1, conf2) => dateFormat.parse(conf1.getString("rule-date").get).after(dateFormat.parse(conf2.getString("rule-date").get))
      )

    val result = configsExcludingDefault.find { conf =>
      val ruleDate = dateFormat.parse(conf.getString("rule-date").get)
      currentDate.toDate.compareTo(ruleDate) >= 0
    }

    result match {
      case Some(conf) =>
        conf
      case _ =>
        configs.filter(_.getString("rule-date").equals(Some("default"))).head
    }
  }

  def getNMWConfig(currentDate: LocalDate): Configuration = {
    getLatestConfig("nmw", currentDate)
  }

  def defineInPaidEmployment(pageObjects: PageObjects): YouPartnerBothEnum = {
    pageObjects.whichOfYouInPaidEmployment.getOrElse(YouPartnerBothEnum.YOU)
  }

  /**
    * Returns the value as true if living with partner else false
    * @param pageObjects
    * @param controllerId
    * @return
    */
  def isLivingWithPartner(pageObjects: PageObjects,
                          controllerId: String = CCConstants.helperManagerId): Boolean = {
    getOrException(pageObjects.livingWithPartner,
      Some(controllerId),
      Some("livingWithPartner"))
  }

  /**
    * Throws exception with appropriate error message if optional element value is None otherwise returns the value
    * ex - val a = Some(5), return value is 5
    *      val a = Some(PageObjects), return value is PageObjects
    *      val a = None , return is runtime exception
    * @param optionalElement
    * @param controllerId
    * @param objectName
    * @param errorMessage
    * @tparam T
    */

  def getOrException[T](optionalElement: Option[T],
                        controllerId: Option[String] = None,
                        objectName: Option[String] = None,
                        errorMessage: String = "no element found"): T = {

    val controller = controllerId.getOrElse("")
    val objectId = objectName.getOrElse("")

    if(controllerId.isDefined && objectName.isDefined){
      optionalElement.fold(throw new RuntimeException(s"no element found in $controller while fetching $objectId"))(identity)
    }else{
      optionalElement.fold(throw new RuntimeException(errorMessage))(identity)
    }

  }

  private def validMinEarnings(modifiedPageObjects: PageObjects): Boolean = {
    val parent = modifiedPageObjects.household.parent
    val parentNMW = parent.minimumEarnings.fold(false)(_.earnMoreThanNMW.fold(false)(identity))

    val partnerOption = modifiedPageObjects.household.partner
    val partnerNMW = partnerOption.fold(false)(_.minimumEarnings.fold(false)(_.earnMoreThanNMW.fold(false)(identity)))

    val parentMinEarnElig = minimumEarningsEligibility(modifiedPageObjects)
    val partnerMinEarnElig = minimumEarningsEligibility(modifiedPageObjects, isPartner = true)

   HelperManager.defineInPaidEmployment(modifiedPageObjects) match {
      case YouPartnerBothEnum.BOTH =>{
        minEarningsEligibilityForPaidEmpBoth(modifiedPageObjects, parentNMW, partnerNMW)
      }
      case YouPartnerBothEnum.YOU => if(parentNMW) true else parentMinEarnElig
      case YouPartnerBothEnum.PARTNER => if(partnerNMW) true else partnerMinEarnElig
    }

  }

  /**
    * Returns the Parent and partner minimum earnings eligibility
    * @param pageObjects
    * @param isPartner
    * @return
    */
  private def minimumEarningsEligibility(pageObjects: PageObjects,
                                         isPartner: Boolean = false) = {
    if(isPartner){
      val partnerOption = pageObjects.household.partner
      val partnerMinEarningsOption = partnerOption.flatMap(_.minimumEarnings)
      val partnerApprentice = partnerMinEarningsOption.fold(false)(_.employmentStatus.contains(EmploymentStatusEnum.APPRENTICE))
      val partnerSelfEmployed = partnerMinEarningsOption.fold(false)(_.selfEmployedIn12Months.fold(false)(identity))

      partnerApprentice || partnerSelfEmployed

    }else{
      val parent = pageObjects.household.parent
      val parentMinEarningsOption = parent.minimumEarnings
      val parentApprentice = parentMinEarningsOption.fold(false)(_.employmentStatus.contains(EmploymentStatusEnum.APPRENTICE))
      val parentSelfEmployed = parentMinEarningsOption.fold(false)(_.selfEmployedIn12Months.fold(false)(identity))

      parentApprentice || parentSelfEmployed
    }
  }

  private def minEarningsEligibilityForPaidEmpBoth(pageObjects: PageObjects,
                                                    parentNMW: Boolean,
                                                   partnerNMW: Boolean) = {
    val parentMinEarnElig = minimumEarningsEligibility(pageObjects)
    val partnerMinEarnElig = minimumEarningsEligibility(pageObjects, isPartner = true)

    (parentNMW, partnerNMW) match {
      case (true, true) => true
      case (true, false) => partnerMinEarnElig
      case (false, true) => parentMinEarnElig
      case (false, false) => partnerMinEarnElig && parentMinEarnElig
    }
  }

  private def validMaxEarnings(modifiedPageObjects: PageObjects): Boolean = {
    val parentMaxEarnings = modifiedPageObjects.household.parent.maximumEarnings.fold(false)(identity)
    val partnerMaxEarnings = modifiedPageObjects.household.partner.fold(false)(_.maximumEarnings.fold(false)(identity))

    val paidEmployment: YouPartnerBothEnum = HelperManager.defineInPaidEmployment(modifiedPageObjects)
    paidEmployment match {
      case YouPartnerBothEnum.BOTH =>
        (parentMaxEarnings, partnerMaxEarnings) match {
          case (true, true) => false
          case (_, _) => true
        }
      case YouPartnerBothEnum.YOU => !parentMaxEarnings
      case YouPartnerBothEnum.PARTNER => !partnerMaxEarnings
    }
  }

  def checkMaxHoursEligibility(modifiedPageObjects: PageObjects): Boolean = {
    validMinEarnings(modifiedPageObjects) &&
    validMaxEarnings(modifiedPageObjects) &&
    modifiedPageObjects.household.location == LocationEnum.ENGLAND
  }

}

object HelperManager extends HelperManager
