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

///*
// * Copyright 2017 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package uk.gov.hmrc.childcarecalculatorfrontend.navigation
//
//import org.mockito.Mockito._
//import org.scalatest.mockito.MockitoSugar
//import play.api.libs.json.JsValue
//import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
//import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{AreYouSelfEmployedOrApprenticeId, PartnerSelfEmployedOrApprenticeId}
//import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.Schemes
//import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, SelfEmployedOrApprenticeOrNeitherEnum, YouPartnerBothEnum}
//import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
//import uk.gov.hmrc.childcarecalculatorfrontend.{Navigator, SpecBase}
//import uk.gov.hmrc.http.cache.client.CacheMap
//
//
//class SelfEmployedOrApprenticeNavigationSpec extends SpecBase with MockitoSugar{
//
//  def userAnswers(answers: (String, JsValue)*): UserAnswers =
//    new UserAnswers(CacheMap("", Map(answers: _*)))
//
//  val navigator = new Navigator(new Schemes())
//  lazy val selfEmployed: String = SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString
//  lazy val apprentice: String = SelfEmployedOrApprenticeOrNeitherEnum.APPRENTICE.toString
//  lazy val neither: String = SelfEmployedOrApprenticeOrNeitherEnum.NEITHER.toString
//
//  "Are you self employed or apprentice" when {
//    "navigate to have you been self employed less than 12 months when user select self employed" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(false)
//      when(answers.areYouInPaidWork) thenReturn Some(true)
//      when(answers.yourMinimumEarnings) thenReturn Some(false)
//      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(selfEmployed)
//
//      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.YourSelfEmployedController.onPageLoad(NormalMode)
//    }
//
//    "navigate to tc/uc page when user select apprentice or neither" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(false)
//      when(answers.areYouInPaidWork) thenReturn Some(true)
//      when(answers.yourMinimumEarnings) thenReturn Some(false)
//      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice) thenReturn Some(neither)
//
//      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//    }
//
//    "navigate to prent self employed 12 months page when user have partner and select self employed" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)
//      when(answers.yourMinimumEarnings) thenReturn Some(false)
//      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(selfEmployed)
//
//      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.YourSelfEmployedController.onPageLoad(NormalMode)
//    }
//
//    "navigate to partner max earning page when user have partner and partner satisfy minimum earning and parent select apprentice or neither" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)
//      when(answers.yourMinimumEarnings) thenReturn Some(false)
//      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice) thenReturn Some(neither)
//
//      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//
//      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//    }
//
//    "navigate to parent self employed 12 months page when user have partner and partner satisfy minimum earning and parent select self employed" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
//      when(answers.yourMinimumEarnings) thenReturn Some(false)
//      when(answers.partnerMinimumEarnings) thenReturn Some(true)
//      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(selfEmployed)
//
//      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.YourSelfEmployedController.onPageLoad(NormalMode)
//    }
//
//    "navigate to partner self employed or apprentice page when user have partner and parent select apprentice or neither" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
//      when(answers.yourMinimumEarnings) thenReturn Some(false)
//      when(answers.partnerMinimumEarnings) thenReturn Some(true)
//      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice) thenReturn Some(neither)
//
//      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
//
//      navigator.nextPage(AreYouSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
//    }
//
//  }
//
//  "Is your partner self employed or apprentice" when {
//    "navigate to have your partner been self employed less than 12 months when user select self employed" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
//      when(answers.partnerMinimumEarnings) thenReturn Some(false)
//      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(selfEmployed)
//
//      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.PartnerSelfEmployedController.onPageLoad(NormalMode)
//    }
//
//    "navigate to tc/uc page when user select apprentice or neither on partner self employed or apprentice page" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
//      when(answers.partnerMinimumEarnings) thenReturn Some(false)
//      when(answers.partnerSelfEmployedOrApprentice) thenReturn(Some(apprentice)) thenReturn(Some(neither))
//
//      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//
//      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//    }
//
//    "navigate to partner self employed 12 months page when user have partner and parent satisfy minimum earning and partner select self employed" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
//      when(answers.yourMinimumEarnings) thenReturn Some(true)
//      when(answers.partnerMinimumEarnings) thenReturn Some(false)
//      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(selfEmployed)
//
//      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.PartnerSelfEmployedController.onPageLoad(NormalMode)
//    }
//
//    "navigate to parent max earnings page when user have partner and parent satisfy minimum earning and partner select apprentice or neither" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
//      when(answers.yourMinimumEarnings) thenReturn Some(true)
//      when(answers.partnerMinimumEarnings) thenReturn Some(false)
//      when(answers.partnerSelfEmployedOrApprentice) thenReturn (Some(apprentice)) thenReturn(Some(neither))
//
//      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.YourMaximumEarningsController.onPageLoad(NormalMode)
//
//      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.YourMaximumEarningsController.onPageLoad(NormalMode)
//    }
//
//    "navigate to tc/uc page when user have partner and both doesn't satisfy minimum earning and both select apprentice" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
//      when(answers.yourMinimumEarnings) thenReturn Some(false)
//      when(answers.partnerMinimumEarnings) thenReturn Some(false)
//      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice)
//      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(apprentice)
//
//      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//
//    }
//
//    "navigate to tc/uc page when user have partner and both doesn't satisfy minimum earning and both select neither" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
//      when(answers.yourMinimumEarnings) thenReturn Some(false)
//      when(answers.partnerMinimumEarnings) thenReturn Some(false)
//      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(neither)
//      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(neither)
//
//      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//
//    }
//
//    "navigate to tc/uc page when user have partner, parent select self employment and partner select apprentice" in {
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
//      when(answers.yourMinimumEarnings) thenReturn Some(false)
//      when(answers.partnerMinimumEarnings) thenReturn Some(false)
//      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(selfEmployed)
//      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(apprentice)
//
//      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//
//    }
//
//    "navigate to tc/uc page when user have partner, parent select apprentice and partner select neither" in{
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
//      when(answers.yourMinimumEarnings) thenReturn Some(false)
//      when(answers.partnerMinimumEarnings) thenReturn Some(false)
//      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(apprentice)
//      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(neither)
//
//      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//    }
//
//    "navigate to tc/uc page when user have partner, parent select neither and partner select apprentice" in{
//      val answers = spy(userAnswers())
//      when(answers.doYouLiveWithPartner) thenReturn Some(true)
//      when(answers.paidEmployment) thenReturn Some(true)
//      when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
//      when(answers.yourMinimumEarnings) thenReturn Some(false)
//      when(answers.partnerMinimumEarnings) thenReturn Some(false)
//      when(answers.areYouSelfEmployedOrApprentice) thenReturn Some(neither)
//      when(answers.partnerSelfEmployedOrApprentice) thenReturn Some(apprentice)
//
//      navigator.nextPage(PartnerSelfEmployedOrApprenticeId, NormalMode)(answers) mustBe
//        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
//    }
//
//
//  }
//
//}
