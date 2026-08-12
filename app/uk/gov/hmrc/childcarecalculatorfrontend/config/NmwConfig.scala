/*
 * Copyright 2026 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.config

import play.api.Configuration
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.Age

import java.text.SimpleDateFormat
import java.time.{LocalDate, ZoneId}
import javax.inject.{Inject, Singleton}
import scala.jdk.CollectionConverters.CollectionHasAsScala

@Singleton
class NmwConfig @Inject (configuration: Configuration) {

  private val path      = "nmw"
  private val dateParam = "rule-date"

  private val dateFormat = "dd-MM-yyyy"

  private val (default: Configuration, sortedConfigs: Seq[(Configuration, LocalDate)]) = {
    val dateFormatter = new SimpleDateFormat(dateFormat)

    val (default, rest) = configuration.underlying
      .getConfigList(path)
      .asScala
      .toSeq
      .map(Configuration(_))
      .map(config => config -> config.get[String](dateParam))
      .partition(_._2.equalsIgnoreCase("default"))

    val restSorted = rest
      .map { case (config, dateString) => config -> dateFormatter.parse(dateString) }
      .sortWith((c1, c2) => c1._2.after(c2._2))
      .map { case (config, date) => config -> date.toInstant.atZone(ZoneId.systemDefault()).toLocalDate }

    (default.head._1, restSorted)
  }

  private def configForDate(currentDate: LocalDate): Configuration =
    sortedConfigs
      .find { case (_, date) =>
        currentDate.compareTo(date) >= 0
      }
      .map(_._1)
      .getOrElse(default)

  def getEarningsForAgeRange(currentDate: LocalDate, ageRange: Option[Age]): Int =
    configForDate(currentDate)
      .getOptional[Int](ageRange.map(_.toString).getOrElse("non-existent-age"))
      .getOrElse {
        throw new RuntimeException(s"no minimum wage config found for age $ageRange")
      }

}
