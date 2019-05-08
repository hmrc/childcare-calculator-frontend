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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

import org.mockito.Mockito.{spy, when}
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.i18n.Messages
import play.api.libs.json._
import play.api.mvc.Request
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{ChildcarePayFrequencyId, DoYouLiveWithPartnerId, ExpectedChildcareCostsId, NoOfChildrenId}
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap

class FirstParagraphBuilderSpec extends PlaySpec with MockitoSugar with SpecBase {
 "First Paragraph Builder" must {
   "Loading the Do You Have Children section" when {
     "You have two children" in {
       val answers = new UserAnswers(new CacheMap("id", Map(NoOfChildrenId.toString -> JsNumber(2))))

       paragraphBuilder.buildFirstParagraph(answers) must include("you have 2 children")
     }

     "You don’t have children" in {
       val answers = new UserAnswers(new CacheMap("id", Map(NoOfChildrenId.toString -> JsNumber(0))))

       paragraphBuilder.buildFirstParagraph(answers) must include("you don’t have children")
     }

     "The number of children field is empty" in {
       val answers = new UserAnswers(new CacheMap("id", Map()))

       paragraphBuilder.buildFirstParagraph(answers) mustBe ""
     }

     "You have one child" in {
       val answers = new UserAnswers(new CacheMap("id", Map(NoOfChildrenId.toString -> JsNumber(1))))

       paragraphBuilder.buildFirstParagraph(answers) must include("you have one child")
     }
   }

   "Loading the Childcare Costs section" when {

     "We have childcare costs at monthly aggregation" in {
       val answers = new UserAnswers(new CacheMap("id", Map(NoOfChildrenId.toString -> JsNumber(2),ChildcarePayFrequencyId.toString -> Json.obj("1"->JsString(ChildcarePayFrequency.MONTHLY.toString)),ExpectedChildcareCostsId.toString -> Json.obj("1" -> JsNumber(25)))))

       paragraphBuilder.buildFirstParagraph(answers) must include("yearly childcare costs of around £300.")
     }

     "We have more than one childcare cost at monthly aggregation" in {
       val answers = new UserAnswers(new CacheMap("id", Map(NoOfChildrenId.toString -> JsNumber(2),ChildcarePayFrequencyId.toString -> Json.obj("1"->JsString(ChildcarePayFrequency.MONTHLY.toString),
         "2"->JsString(ChildcarePayFrequency.MONTHLY.toString),
         "3"->JsString(ChildcarePayFrequency.MONTHLY.toString)),
         ExpectedChildcareCostsId.toString -> Json.obj("1" -> JsNumber(20),"2" -> JsNumber(10),"3"-> JsNumber(5)))))

       paragraphBuilder.buildFirstParagraph(answers) must include("yearly childcare costs of around £420.")
     }

     "We have one childcare cost at weekly aggregation" in {
       val answers = new UserAnswers(new CacheMap("id", Map(NoOfChildrenId.toString -> JsNumber(2),ChildcarePayFrequencyId.toString -> Json.obj("1"->JsString(ChildcarePayFrequency.WEEKLY.toString)),
         ExpectedChildcareCostsId.toString -> Json.obj("1" -> JsNumber(4)))))

       paragraphBuilder.buildFirstParagraph(answers) must include("yearly childcare costs of around £208.")
     }

     "We have one childcare cost at weekly aggregation and one childcare cost at monthly aggregation" in {
       val answers = new UserAnswers(new CacheMap("id", Map(NoOfChildrenId.toString -> JsNumber(2),ChildcarePayFrequencyId.toString -> Json.obj("1"->JsString(ChildcarePayFrequency.MONTHLY.toString),
         "2"->JsString(ChildcarePayFrequency.MONTHLY.toString),
         "3"->JsString(ChildcarePayFrequency.WEEKLY.toString)),
         ExpectedChildcareCostsId.toString -> Json.obj("1" -> JsNumber(20),"2" -> JsNumber(10),"3"-> JsNumber(10)))))

       paragraphBuilder.buildFirstParagraph(answers) must include("yearly childcare costs of around £880.")
     }

     "We have children but no childcare costs" in {
       val answers = new UserAnswers(new CacheMap("id", Map(NoOfChildrenId.toString -> JsNumber(1))))

       paragraphBuilder.buildFirstParagraph(answers) must include("you have one child.")
     }

     "You have 0 children and no childcare costs" in {
       val answers = new UserAnswers(new CacheMap("id", Map(NoOfChildrenId.toString -> JsNumber(0))))

       paragraphBuilder.buildFirstParagraph(answers) must include("you don’t have children.")
     }

     "There is no data about children or childcare costs" in {
       val answers = new UserAnswers(new CacheMap("id", Map()))

       paragraphBuilder.buildFirstParagraph(answers) mustBe ""
     }
   }

   "Loading the Your Living Status section" when {
     "You live on your own" in {
       val answers = new UserAnswers(new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(false))))

       paragraphBuilder.buildFirstParagraph(answers) must include("on your own")
     }

     "You live with your partner" in {
       val answers = new UserAnswers(new CacheMap("id", Map(DoYouLiveWithPartnerId.toString -> JsBoolean(true))))

       paragraphBuilder.buildFirstParagraph(answers) must include("with your partner")
     }

     "We have no data to establish whether if they live on their own or with partner" in {
       val answers = new UserAnswers(new CacheMap("id", Map()))
       val values = paragraphBuilder.buildFirstParagraph(answers)

       values mustNot include("with your partner and")
       values mustNot include("on your own and")
     }
   }

   "Loading the Who Is In Paid Work section" when {
     "Only you are in paid work" in {
       val answers = spy(userAnswers())
       when(answers.doYouLiveWithPartner) thenReturn Some(true)
       when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)

       paragraphBuilder.buildFirstParagraph(answers) must include("and only you are")
     }

     "Partner in paid work" in {
       val answers = spy(userAnswers())
       when(answers.doYouLiveWithPartner) thenReturn Some(true)
       when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)

       paragraphBuilder.buildFirstParagraph(answers) must include("only your partner is")
     }

     "Both are in paid work" in {
       val answers = spy(userAnswers())
       when(answers.doYouLiveWithPartner) thenReturn Some(true)
       when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)

       paragraphBuilder.buildFirstParagraph(answers) must include("and you are both")
     }

     "Neither in paid work" in {
       val answers = spy(userAnswers())
       when(answers.doYouLiveWithPartner) thenReturn Some(true)
       when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothNeitherEnum.NEITHER.toString)

       paragraphBuilder.buildFirstParagraph(answers) must include("and none of you are currently in paid work")
     }

     "You live on your own and you are in paid work" in {
       val answers = spy(userAnswers())
       when(answers.doYouLiveWithPartner) thenReturn Some(false)
       when(answers.areYouInPaidWork) thenReturn Some(true)
       when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)

       paragraphBuilder.buildFirstParagraph(answers) must include("You live on your own and you are currently in paid work")
     }

     "You live on your own and don’t work" in {
       val answers = spy(userAnswers())
       when(answers.doYouLiveWithPartner) thenReturn Some(false)
       when(answers.areYouInPaidWork) thenReturn Some(false)

       paragraphBuilder.buildFirstParagraph(answers) must include("You live on your own.")
     }

     "You are in paid work but there is no data to know if you live with partner" in {
       val answers = spy(userAnswers())
       when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)

       paragraphBuilder.buildFirstParagraph(answers) mustNot include("You live on your own and you are currently in paid work")
     }

     "You live with your partner and no one works" in {
       when(answers.doYouLiveWithPartner) thenReturn Some(true)

       paragraphBuilder.buildFirstParagraph(answers) must include("You live with your partner.")
     }

     "No data about who is in paid work" in {
       val result = paragraphBuilder.buildFirstParagraph(answers)

       result mustNot include("your partner is")
       result mustNot include("you are")
       result mustNot include("you and your partner are")
     }

     "You work x hours a week" in {
       when(answers.areYouInPaidWork) thenReturn Some(true)
       when(answers.doYouLiveWithPartner) thenReturn Some(false)
       when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.YOU.toString)
       when(answers.parentWorkHours) thenReturn Some(BigDecimal(40))

       paragraphBuilder.buildFirstParagraph(answers) must include("You work 40 hours a week")
     }

     "You live on your own and you work x hours a week" in {
       val answers = spy(userAnswers())
       when(answers.doYouLiveWithPartner) thenReturn Some(false)
       when(answers.areYouInPaidWork) thenReturn Some(true)
       when(answers.parentWorkHours) thenReturn Some(BigDecimal(40))

       paragraphBuilder.buildFirstParagraph(answers) must include("You live on your own and you are currently in paid work. You work 40 hours a week.")
     }


     "Your partner works x hours a week" in {
       when(answers.doYouLiveWithPartner) thenReturn Some(true)
       when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.PARTNER.toString)
       when(answers.partnerWorkHours) thenReturn Some(BigDecimal(40))

       paragraphBuilder.buildFirstParagraph(answers) must include("Your partner works 40 hours a week")
     }

     "Your and your partner works x hours a week" in {
       when(answers.doYouLiveWithPartner) thenReturn Some(true)
       when(answers.whoIsInPaidEmployment) thenReturn Some(YouPartnerBothEnum.BOTH.toString)
       when(answers.partnerWorkHours) thenReturn Some(BigDecimal(40))
       when(answers.parentWorkHours) thenReturn Some(BigDecimal(40))

       paragraphBuilder.buildFirstParagraph(answers) must include("You work 40 hours and your partner works 40 hours a week")
     }
   }
 }

  val utils = new Utils()
  val paragraphBuilder = new FirstParagraphBuilder(utils)
  val answers = spy(userAnswers())
  implicit val hc: HeaderCarrier = HeaderCarrier()
  implicit val req: Request[_] = mock[Request[_]]

  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))
}
