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
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import play.api.Play.current
import play.api.{Configuration, Play}

object Utils {

  /**
    * Throws exception with appropriate error message if optional element value is None otherwise returns the value
    * ex - val a = Some(5), return value is 5
    *      val a = Some(PageObjects), return value is PageObjects
    *      val a = None , return is runtime exception
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

    if(controllerId.isDefined && objectName.isDefined){
      optionalElement.fold(throw new RuntimeException(s"no element found in $controller while fetching $objectId"))(identity)
    }else{
      optionalElement.fold(throw new RuntimeException(errorMessage))(identity)
    }

  }

 /*/**
    *
    * @param currentDate
    * @return
    */
  def getNMWConfig(currentDate: LocalDate): Configuration = getLatestConfig(nmwConfigFileAbbreviation, currentDate)

  /**
    * Gets the latest configuration for the input config type
    *
    * @param configType
    * @param currentDate
    * @return
    */
  def getLatestConfig(configType: String, currentDate: LocalDate): Configuration = {
    val dateFormat = new SimpleDateFormat(ccDateFormat)
    val configs: Seq[Configuration] = Play.application.configuration.getConfigSeq(configType).getOrElse(Seq())

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
*/
}
