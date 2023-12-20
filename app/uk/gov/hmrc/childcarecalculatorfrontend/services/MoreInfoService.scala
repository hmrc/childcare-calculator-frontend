/*
 * Copyright 2023 HM Revenue & Customs
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

package services

import com.google.inject.Inject
import play.api.i18n.{Lang, MessagesApi}
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel

import javax.inject.Singleton

@Singleton
class MoreInfoService @Inject()(val messages: MessagesApi) {

  def getSchemeContent(userLocation: Location, model: ResultsViewModel, hideTC: Boolean)(implicit lang: Lang): List[Map[String, String]] = {
    val freeHours = if (model.freeChildcareWorkingParents) {
      linkData(userLocation.toString, "hours", model.freeHours)
    } else {
      None
    }

    val taxCredits = if (hideTC) None else linkData(userLocation.toString, "tc", model.tc)
    val taxFreeChildCare = linkData(userLocation.toString, "tfc", model.tfc)

    List(freeHours, taxCredits, taxFreeChildCare).flatten
  }

  def getSummary(location: Location, results: ResultsViewModel)(implicit lang: Lang): Option[String] = (location, results.freeHours, results.tfc) match {
    case (Location.ENGLAND, Some(freeHours), Some(tfc)) if tfc > 0 && freeHours > 0 => {
      Some(messages(s"aboutYourResults.more.info.summary"))
    }
    case (_, _, _) => {
      None
    }
  }

  private def linkData(key: String, scheme: String, value: Option[BigDecimal])(implicit lang: Lang) =
    value match {
      case Some(x) if x > 0 => {
        Some(Map(
          "title" -> messages(s"aboutYourResults.more.info.$key.$scheme.title"),
          "link" -> messages(s"aboutYourResults.more.info.$key.$scheme.link")
        ))
      }
      case _ => None
    }
}
