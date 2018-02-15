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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import java.text.SimpleDateFormat
import javax.inject.Singleton

import org.joda.time.LocalDate
import play.api.mvc.{AnyContent, Call}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import play.api.Configuration
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.models.requests.DataRequest
import uk.gov.hmrc.http.Request
import uk.gov.hmrc.http.cache.client.CacheMap

class Utils {

  /**
    * Throws exception with appropriate error message if optional element value is None otherwise returns the value
    * ex - val a = Some(5), return value is 5
    * val a = Some(PageObjects), return value is PageObjects
    * val a = None , return is runtime exception
    *
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

    if (controllerId.isDefined && objectName.isDefined) {
      optionalElement.fold(throw new RuntimeException(s"no element found in $controller while fetching $objectId"))(identity)
    } else {
      optionalElement.fold(throw new RuntimeException(errorMessage))(identity)
    }

  }

  def getStatutoryPayType(implicit request: DataRequest[AnyContent])  = request.userAnswers.yourStatutoryPayType.getOrElse("")

  /**
    * Gets the NMW for the given age range
    *
    * @param configuration
    * @param currentDate
    * @param ageRange
    * @return
    */
  def getEarningsForAgeRange(configuration: Configuration,
                             currentDate: LocalDate,
                             ageRange: Option[String]) = {
    getOrException(getNMWConfig(configuration, currentDate).getInt(ageRange.getOrElse("non-existent-age")))
  }

  /**
    *
    * @param currentDate
    * @return
    */
  def getNMWConfig(configuration: Configuration,
                   currentDate: LocalDate): Configuration = getLatestConfig(configuration,
    nmwConfigFileAbbreviation,
    currentDate)

  /**
    * Gets the latest configuration for the input config type
    *
    * @param configType
    * @param currentDate
    * @return
    */
  def getLatestConfig(configuration: Configuration, configType: String, currentDate: LocalDate): Configuration = {
    val dateFormat = new SimpleDateFormat(ccDateFormat)
    val configs: Seq[Configuration] = configuration.getConfigSeq(configType).getOrElse(Seq())

    val configsExcludingDefault: Seq[Configuration] = configs.filterNot(
      _.getString(ruleDateConfigParam).contains("default")
    ).sortWith(
      (conf1, conf2) => dateFormat.parse(conf1.getString(ruleDateConfigParam).get).after(dateFormat.parse(conf2.getString(ruleDateConfigParam).get))
    )

    val result = configsExcludingDefault.find { conf =>
      val ruleDate = dateFormat.parse(conf.getString(ruleDateConfigParam).get)
      currentDate.toDate.compareTo(ruleDate) >= 0
    }

    result match {
      case Some(conf) =>
        conf
      case _ =>
        configs.filter(_.getString(ruleDateConfigParam).contains("default")).head
    }
  }

  /**
    * * Returns the call from the input function (f: A => Call) when optionalElement has some value otherwise
    * returns SessionExpired Page
    * Ex - getCall(Some(true)){case _ => Call("GET", "http://test.com")} returns Call("GET", "http://test.com")
    *      getCall(None){case _ => Call("GET", "http://test.com")} returns routes.SessionExpiredController.onPageLoad()
    *
    * @param optionalElement
    * @param f
    * @tparam A
    * @return Call
    */
  def getCall[A](optionalElement: Option[A])(f: PartialFunction[A, Call]): Call =
    optionalElement.flatMap(f.lift).getOrElse(routes.SessionExpiredController.onPageLoad())

  /**
    * Returns the value with comma when value is more than 999, also removes the decimal part
    * and gives the whole number
    *
    * Ex - 30 -> 30 , 30.35 -> 30, 1300 -> 1,300
    * @param value
    * @return
    */
  def valueFormatter(value: BigDecimal): String = {
    val valueFormatter = new java.text.DecimalFormat("##,###")
    valueFormatter.format(value)
  }

  def emptyCacheMap(existingCacheMap: CacheMap): CacheMap = existingCacheMap.copy(data = Map())

}
