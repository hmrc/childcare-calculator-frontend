/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import org.scalatestplus.play.PlaySpec
import play.api.i18n.Lang
import services.MoreInfoService
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location.Location
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel

class MoreInfoServiceSpec extends PlaySpec with SpecBase {

  implicit val l: Lang = lang

  private val allSchemesValid = ResultsViewModel(
    tc = Some(2.0),
    tfc = Some(2.0),
    freeHours = Some(2.0), location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

  private val tcSchemeInvalid = ResultsViewModel(
    tc = None,
    tfc = Some(2.0),
    freeHours = Some(2.0), location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

  private val tfcSchemeInvalid = ResultsViewModel(
    tc = Some(2.0),
    tfc = None,
    freeHours = Some(2.0), location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

  private val escSchemeInvalid = ResultsViewModel(
    tc = Some(2.0),
    tfc = Some(2.0),
    freeHours = Some(2.0), location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

  private val freeHoursSchemeInvalid = ResultsViewModel(
    tc = Some(2.0),
    tfc = Some(2.0),
    freeHours = None, location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true)

  case class Results(location: Location, key: String, scheme: ResultsViewModel)

  private val summaryContent = messages("aboutYourResults.more.info.summary")

  private val allValid = List(
    Results(Location.ENGLAND, "england", allSchemesValid),
    Results(Location.WALES, "wales", allSchemesValid),
    Results(Location.SCOTLAND, "scotland", allSchemesValid),
    Results(Location.NORTHERN_IRELAND, "northern-ireland", allSchemesValid)
  )

  private val tcInvalid = List(
    Results(Location.ENGLAND, "england", tcSchemeInvalid),
    Results(Location.WALES, "wales", tcSchemeInvalid),
    Results(Location.SCOTLAND, "scotland", tcSchemeInvalid),
    Results(Location.NORTHERN_IRELAND, "northern-ireland", tcSchemeInvalid)
  )

  private val tfcInvalid = List(
    Results(Location.ENGLAND, "england", tfcSchemeInvalid),
    Results(Location.WALES, "wales", tfcSchemeInvalid),
    Results(Location.SCOTLAND, "scotland", tfcSchemeInvalid),
    Results(Location.NORTHERN_IRELAND, "northern-ireland", tfcSchemeInvalid)
  )

  private val freeHoursInvalid = List(
    Results(Location.ENGLAND, "england", freeHoursSchemeInvalid),
    Results(Location.WALES, "wales", freeHoursSchemeInvalid),
    Results(Location.SCOTLAND, "scotland", freeHoursSchemeInvalid),
    Results(Location.NORTHERN_IRELAND, "northern-ireland", freeHoursSchemeInvalid)
  )

  private val service = new MoreInfoService(messagesApi)

  "MoreInfoService" should {

    "not return free hours link if we are not entitled to maximum free hours in England" in {
      val result = service.getSchemeContent(Location.ENGLAND,ResultsViewModel(tc = Some(2.0), tfc = Some(2.0), freeHours = Some(15), location = Location.ENGLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true))
      result must not contain Map("link" -> messages(s"aboutYourResults.more.info.england.hours.link"), "title" -> messages(s"aboutYourResults.more.info.england.hours.title"))
    }

    "not return free hours link if we are not entitled to maximum free hours and we live in WALES" in {
      val result = service.getSchemeContent(Location.WALES,ResultsViewModel(tc = Some(2.0), tfc = Some(2.0), freeHours = Some(10), location = Location.WALES, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true))
      result must not contain Map("link" -> messages(s"aboutYourResults.more.info.wales.hours.link"), "title" -> messages(s"aboutYourResults.more.info.wales.hours.title"))
    }

    "not return free hours link if we are not entitled to maximum free hours and we live in SCOTLAND" in {
      val result = service.getSchemeContent(Location.SCOTLAND,ResultsViewModel(tc = Some(2.0), tfc = Some(2.0), freeHours = Some(16), location = Location.SCOTLAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true))
      result must not contain Map("link" -> messages(s"aboutYourResults.more.info.scotland.hours.link"), "title" -> messages(s"aboutYourResults.more.info.scotland.hours.title"))
    }

    "not return free hours link if we are not entitled to maximum free hours and we live in NORTHERN IRELAND" in {
      val result = service.getSchemeContent(Location.NORTHERN_IRELAND,ResultsViewModel(tc = Some(2.0), tfc = Some(2.0), freeHours = Some(12.5), location = Location.NORTHERN_IRELAND, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true))
      result must not contain Map("link" -> messages(s"aboutYourResults.more.info.northern-ireland.hours.link"), "title" -> messages(s"aboutYourResults.more.info.northern-ireland.hours.title"))
    }

    for (test <- allValid) {
      s"return correct footer information for ${test.key} when all schemes are valid" in {

        service.getSchemeContent(test.location, test.scheme) must contain(
          Map(
            "link" -> messages(s"aboutYourResults.more.info.${test.key}.hours.link"),
            "title" -> messages(s"aboutYourResults.more.info.${test.key}.hours.title"),
            "link" -> messages(s"aboutYourResults.more.info.${test.key}.tc.link"),
            "title" -> messages(s"aboutYourResults.more.info.${test.key}.tc.title"),
            "link" -> messages(s"aboutYourResults.more.info.${test.key}.tfc.link"),
            "title" -> messages(s"aboutYourResults.more.info.${test.key}.tfc.title")
          ))

        val summary = service.getSummary(test.location, test.scheme)
        test.location match {
          case Location.ENGLAND => {
            summary must contain (summaryContent)
          }
          case _ =>
            summary must not contain summaryContent
        }
      }
    }

    for (testTC <- tcInvalid) {
      s"return correct footer information for ${testTC.key} when tc is invalid" in {
        val result = service.getSchemeContent(testTC.location, testTC.scheme)
        result must contain(
          Map(
            "link" -> messages(s"aboutYourResults.more.info.${testTC.key}.hours.link"),
            "title" -> messages(s"aboutYourResults.more.info.${testTC.key}.hours.title"),
            "link" -> messages(s"aboutYourResults.more.info.${testTC.key}.tfc.link"),
            "title" -> messages(s"aboutYourResults.more.info.${testTC.key}.tfc.title")

          ))

        result must not contain Map(
            "link" -> messages(s"aboutYourResults.more.info.${testTC.key}.tc.link"),
            "title" -> messages(s"aboutYourResults.more.info.${testTC.key}.tc.title"))
      }
    }

    for (testTFC <- tfcInvalid) {
      s"return correct footer information for ${testTFC.key} when tfc is invalid" in {
        val result = service.getSchemeContent(testTFC.location, testTFC.scheme)
        result must contain(
          Map(
            "link" -> messages(s"aboutYourResults.more.info.${testTFC.key}.hours.link"),
            "title" -> messages(s"aboutYourResults.more.info.${testTFC.key}.hours.title"),
            "link" -> messages(s"aboutYourResults.more.info.${testTFC.key}.tc.link"),
            "title" -> messages(s"aboutYourResults.more.info.${testTFC.key}.tc.title")
          ))

        result must not contain Map(
            "link" -> messages(s"aboutYourResults.more.info.${testTFC.key}.tfc.link"),
            "title" -> messages(s"aboutYourResults.more.info.${testTFC.key}.tfc.title"))

        service.getSummary(testTFC.location, testTFC.scheme) must not contain
          summaryContent
      }
    }

    for (testFreeHours <- freeHoursInvalid) {
      s"return correct footer information for ${testFreeHours.key} when free hours is invalid" in {
        val result = service.getSchemeContent(testFreeHours.location, testFreeHours.scheme)
        result must contain(
          Map(
            "link" -> messages(s"aboutYourResults.more.info.${testFreeHours.key}.tc.link"),
            "title" -> messages(s"aboutYourResults.more.info.${testFreeHours.key}.tc.title"),
            "link" -> messages(s"aboutYourResults.more.info.${testFreeHours.key}.tfc.link"),
            "title" -> messages(s"aboutYourResults.more.info.${testFreeHours.key}.tfc.title")
          ))

        result must not contain Map(
            "link" -> messages(s"aboutYourResults.more.info.${testFreeHours.key}.hours.link"),
            "title" -> messages(s"aboutYourResults.more.info.${testFreeHours.key}.hours.title"))

        service.getSummary(testFreeHours.location, testFreeHours.scheme) must not contain
          summaryContent
      }

    }

  }

}
