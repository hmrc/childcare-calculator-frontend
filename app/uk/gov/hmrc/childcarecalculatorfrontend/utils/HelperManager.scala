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
import uk.gov.hmrc.childcarecalculatorfrontend.models.{YouPartnerBothEnum, PageObjects}

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
}

object HelperManager extends HelperManager
