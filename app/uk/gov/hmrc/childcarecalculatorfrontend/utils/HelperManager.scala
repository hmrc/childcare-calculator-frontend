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

import uk.gov.hmrc.childcarecalculatorfrontend.models.PageObjects
import java.text.{NumberFormat, SimpleDateFormat}
import java.util.{Calendar, Date, Locale, TimeZone}
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTimeZone, LocalDate}
import play.api.Play.current
import play.api.{Configuration, Logger, Play}

import scala.annotation.tailrec

/**
 * Created by user on 25/08/17.
 */
class HelperManager {
  def isUserEarningLessThanMinimumWage(PageObjects: PageObjects): Boolean = {
    val nmwConfig = getNMWConfig(LocalDate.now)
    val nmwPerAge = nmwConfig.getInt(PageObjects.household.parent.ageRange.getOrElse("non-existing-age"))
    nmwPerAge.isDefined && PageObjects.minimumEarnings.isDefined && (nmwPerAge.get > PageObjects.minimumEarnings.get)
  }

  def getLatestConfig(configType: String, currentDate: LocalDate): Configuration = {
    val dateFormat = new SimpleDateFormat("dd-MM-yyyy")
    val configs: Seq[Configuration] = Play.application.configuration.getConfigSeq(configType).get
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
}
