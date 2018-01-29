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

  def get(location: Location, results: ResultsViewModel): List[Map[String, String]] = {
    val locationKey = location match {
      case Location.ENGLAND => "england"
      case Location.WALES=> "wales"
      case Location.SCOTLAND => "scotland"
      case Location.NORTHERN_IRELAND => "northern-ireland"
    }

    val freeHours = results.freeHours match {
      case Some(x) if x > 0 => {
        Map(
          "title" -> messages(s"aboutYourResults.more.info.$locationKey.hours.title"),
          "link" -> messages(s"aboutYourResults.more.info.$locationKey.hours.link")
        )
      }
      case _ => Map("" -> "")
    }

    List(freeHours)
  }
}

@ImplementedBy(classOf[MoreInfoService])
trait MoreInfoServiceInterface {
  def get(location: Location, results: ResultsViewModel): List[Map[String, String]]
}