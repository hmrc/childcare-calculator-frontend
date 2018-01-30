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

@Singleton
class MoreInfoService @Inject() (val messages: MessagesApi) extends MoreInfoServiceInterface{

  def getSchemeContent(l: Location, r: ResultsViewModel): List[Map[String, String]] = {

    val location = locationValue(l)

    val freeHours = linkData(location, "hours", r.freeHours)

    val taxCredits = linkData(location, "tc", r.tc)

    val taxFreeChildCare = linkData(location, "tfc", r.tfc)

    val childCareVouchers = linkData(location, "esc", r.esc)

    List(freeHours, taxCredits, taxFreeChildCare, childCareVouchers).flatten
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

  private def locationValue(location: Location): String = location match {
    case Location.ENGLAND => "england"
    case Location.WALES=> "wales"
    case Location.SCOTLAND => "scotland"
    case Location.NORTHERN_IRELAND => "northern-ireland"
  }

}

@ImplementedBy(classOf[MoreInfoService])
trait MoreInfoServiceInterface {
  def getSchemeContent(location: Location, results: ResultsViewModel): List[Map[String, String]]
  def getSummary(location: Location, results: ResultsViewModel): Option[String]
}