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

package services

import javax.inject.Singleton

import com.google.inject.{ImplementedBy, Inject}
import play.api.i18n.MessagesApi
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants

import scala.math.BigDecimal

@Singleton
class MoreInfoService @Inject() (val messages: MessagesApi) extends MoreInfoServiceInterface{

  def getSchemeContent(userLocation: Location, model: ResultsViewModel): List[Map[String, String]] = {
    val freeHours = (userLocation,model.freeHours) match {
      case (Location.ENGLAND,Some(ChildcareConstants.maxFreeHours)) => linkData(userLocation.toString, "hours", model.freeHours)
      case _ => None
    }

    val taxCredits = linkData(userLocation.toString, "tc", model.tc)
    val taxFreeChildCare = linkData(userLocation.toString, "tfc", model.tfc)
    List(freeHours, taxCredits, taxFreeChildCare).flatten
  }

  def getSummary(location: Location, results: ResultsViewModel): Option[String] = (location, results.freeHours, results.tfc) match {
    case (Location.ENGLAND, Some(freeHours), Some(tfc)) if tfc > 0 && freeHours > 0 => {
      Some(messages(s"aboutYourResults.more.info.summary"))
    }
    case (_, _, _) => {
      None
    }
  }

  private def linkData(key: String, scheme: String, value: Option[BigDecimal]) = value match {
    case Some(x) if x > 0 => {
      Some(Map(
        "title" -> messages(s"aboutYourResults.more.info.$key.$scheme.title"),
        "link" -> messages(s"aboutYourResults.more.info.$key.$scheme.link")
      ))
    }
    case _ => None
  }
}

@ImplementedBy(classOf[MoreInfoService])
trait MoreInfoServiceInterface {
  def getSchemeContent(location: Location, results: ResultsViewModel): List[Map[String, String]]
  def getSummary(location: Location, results: ResultsViewModel): Option[String]
}