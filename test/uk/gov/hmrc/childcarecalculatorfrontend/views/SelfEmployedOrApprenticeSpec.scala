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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table
import play.api.data.Form
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.SelfEmployedOrApprenticeForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.EmploymentStatusEnum
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.selfEmployedOrApprentice
import uk.gov.hmrc.childcarecalculatorfrontend.{TestDataForViews, FakeCCApplication, TemplatesValidator}

class SelfEmployedOrApprenticeSpec extends TemplatesValidator with FakeCCApplication with TestDataForViews{

  lazy val backUrlForParent: Call = Call("GET", parentMinimumEarningsPath)
  lazy val backUrlForPartner: Call = Call("GET", partnerMinimumEarningsPath)

  lazy val pageTitleContentParent = "Are you self-employed or an apprentice?"
  lazy val pageTitleContentPartner = "Is your partner an apprentice or self-employed?"

  //To be deleted
  lazy val  selfEmployedTimescaleParentPathTemp = Call("GET", "TO_BE_IMPLEMENTED") //to be replaced by selfEmployedTimescaleParentPath
  lazy val  selfEmployedTimescalePartnerPathTemp = Call("GET", "TO_BE_IMPLEMENTED") //to be replaced by selfEmployedTimescalePartnerPath

  override val contentData: List[ElementDetails] = List(

    ElementDetails(attribute = Some(attributeFor), attributeValue = Some("selfEmployedOrApprentice-selfemployed"), value = "Self-employed"),
    ElementDetails(attribute = Some(attributeFor), attributeValue = Some("selfEmployedOrApprentice-apprentice"), value = "Apprentice"),
    ElementDetails(attribute = Some(attributeFor), attributeValue = Some("selfEmployedOrApprentice-neither"), value = "Neither self-employed or an apprentice"),
    ElementDetails(id = Some(nextButtonId), value = nextButtonLabel),
    ElementDetails(id = Some(backButtonId), value = backButtonLabel)
  )

  override val linksData: List[ElementDetails] = List()

  def getTemplate(form: Form[Option[String]], isPartner: Boolean = false, backUrl: Call): Document = {
    val template = selfEmployedOrApprentice(form, isPartner, backUrl)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  def getNewForm(isPartner: Boolean = false): SelfEmployedOrApprenticeForm = {
    new SelfEmployedOrApprenticeForm(isPartner, applicationMessagesApi)
  }

  val testCases = Table(
    ("Is partner", "Submission path", "Page title", "Back Url"),
    (false, selfEmployedTimescaleParentPathTemp.toString, pageTitleContentParent, backUrlForParent),
    (true, selfEmployedTimescalePartnerPathTemp.toString, pageTitleContentPartner, backUrlForPartner)
  )

  forAll(testCases) {
   case (isPartner, submissionPath, pageTitle, backUrl) =>

    val userType = getUserType(isPartner)

    val dynamicContent = List(
      ElementDetails(id = Some(pageTitleId), value = pageTitle)
    )

    val dynamicLinks = List(
      ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = submissionPath),
      ElementDetails(id = Some(backButtonId), checkAttribute = Some(attributeHref), value = backUrl.toString)
    )

    s"if user is $userType" should {

      "render template successfully" in {
        val template = selfEmployedOrApprentice.render(getNewForm(isPartner).form, isPartner, backUrl, request, applicationMessages)
        template.contentType shouldBe "text/html"

        val template1 = selfEmployedOrApprentice.f(getNewForm(isPartner).form, isPartner, backUrl)(request, applicationMessages)
        template1.contentType shouldBe "text/html"
      }

      "load template successfully" when {
        "nothing is selected initially" in {
          implicit val doc: Document = getTemplate(getNewForm(isPartner).form, isPartner, backUrl)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyErrors()
        }

        "valid value is given" in {
          implicit val doc: Document = getTemplate(getNewForm(isPartner).form.fill(Some(EmploymentStatusEnum.SELFEMPLOYED.toString)), isPartner, backUrl)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyErrors()
        }
      }

      "display correct error message" when {
        s"form is submitted without data ('${applicationMessages.messages(s"self.employed.or.apprentice.not.selected.$userType")}')" in {
          val form = getNewForm(isPartner).form.bind(
            Map(
              selfEmployedOrApprenticeKey -> ""
            )
          )
          implicit val doc: Document = getTemplate(form, isPartner, backUrl)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyErrors(
            errors = Map(selfEmployedOrApprenticeKey -> applicationMessages.messages(s"self.employed.or.apprentice.not.selected.$userType"))
          )
          applicationMessages.messages(s"self.employed.or.apprentice.not.selected.$userType") should not be s"self.employed.or.apprentice.not.selected.$userType"
        }
      }

    }

  }

}
