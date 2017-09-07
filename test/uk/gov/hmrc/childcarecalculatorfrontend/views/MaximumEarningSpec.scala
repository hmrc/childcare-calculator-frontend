package uk.gov.hmrc.childcarecalculatorfrontend.views

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.prop.TableDrivenPropertyChecks._
import org.scalatest.prop.Tables.Table
import play.api.data.Form
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Call
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.{MaximumEarningForm, MinimumEarningsForm}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.HelperManager
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.{minimumEarning, maximumEarning}
import uk.gov.hmrc.childcarecalculatorfrontend.{FakeCCApplication, TemplatesValidator}

/**
 * Created by user on 07/09/17.
 */
class MaximumEarningSpec extends TemplatesValidator with  FakeCCApplication with HelperManager {

  override val contentData: List[ElementDetails] = List(
    ElementDetails(attribute = Some("for"), attributeValue = Some("maximumEarnings-true"), value = "Yes"),
    ElementDetails(attribute = Some("for"), attributeValue = Some("maximumEarnings-false"), value = "No"),
    ElementDetails(id = Some("next-button"), value = "Continue"),
    ElementDetails(id = Some("back-button"), value = "Back")
  )

  override val linksData: List[ElementDetails] = List(
    ElementDetails(id = Some("back-button"), checkAttribute = Some("href"), value = parentMinimumEarningsPath)
  )

  val backURL = Call("GET", parentMinimumEarningsPath)


  def getTemplate(form: Form[Option[Boolean]], hasPartner: Boolean, isPartner: Boolean): Document = {
    val template = maximumEarning(form, hasPartner, isPartner, backURL)(request, applicationMessages)
    Jsoup.parse(contentAsString(template))
  }

  val isPartnerTestCase = Table(
    ("hasPartner", "isPartner", "errorMessage", "pageTitle", "hintText", "submitURL"),
    (false, false, "on.average.how.much.will.you.earn.parent.error", s"On average, will you earn £ or more a week?", "This is the National Minimum Wage or National Living Wage a week for someone your age.", parentMinimumEarningsPath),
    (false, true, "on.average.how.much.will.you.earn.partner.error", s"On average, will your partner earn £ or more a week?", "This is the National Minimum Wage or National Living Wage a week for someone your partner’s age.", partnerMinimumEarningsPath)
  )


  forAll(isPartnerTestCase) { case (hasPartner, isPartner, errorMessage, pageTitle, hintText, submitURL) =>
    s"calling benefits template when isPartner = ${isPartner}" should {
      "render template" in {
        val template = maximumEarning.render(new MaximumEarningForm(hasPartner, isPartner, applicationMessagesApi).form, hasPartner, isPartner, backURL, request, applicationMessages)
        template.contentType shouldBe "text/html"

        val template1 = maximumEarning.f(new MaximumEarningForm(hasPartner, isPartner, applicationMessagesApi).form, hasPartner, isPartner, backURL)(request, applicationMessages)
        template1.contentType shouldBe "text/html"
      }

      val dynamicContent = List(
        ElementDetails(id = Some("page-title"), value = pageTitle),
        ElementDetails(tagName = Some("p"), tagIndex = Some(0), value = hintText)
      )

      val dynamicLinks = List(
        ElementDetails(elementClass = Some("form"), checkAttribute = Some("action"), value = submitURL)
      )

      "display correct content" when {
        "nothing is selected initially" in {
          implicit val doc: Document = getTemplate(new MaximumEarningForm(hasPartner, isPartner, applicationMessagesApi).form.fill(None), hasPartner, isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks()
          verifyErrors()
        }

        "true is selected" in {
          implicit val doc: Document = getTemplate(new MaximumEarningForm(hasPartner, isPartner, applicationMessagesApi).form.fill(Some(true)), hasPartner, isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks(
            List("maximumEarnings-true")
          )
          verifyErrors()
        }

        "false is selected" in {
          implicit val doc: Document = getTemplate(new MaximumEarningForm(hasPartner, isPartner, applicationMessagesApi).form.fill(Some(false)), hasPartner, isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks(
            List("maximumEarnings-false")
          )
          verifyErrors()
        }

        s"display ${applicationMessages.messages(errorMessage, 1)} form is submitted without data" in {
          val form = new MinimumEarningsForm(isPartner, 1, applicationMessagesApi).form.bind(
            Map(
              "maximumEarnings" -> ""
            )
          )
          implicit val doc: Document = getTemplate(form, hasPartner, isPartner)

          verifyPageContent(dynamicContent)
          verifyPageLinks(dynamicLinks)
          verifyChecks()
          verifyErrors(
            errors = Map("maximumEarnings" -> applicationMessages.messages(errorMessage, 1)),
            validDateInlineErrors = false
          )
          applicationMessages.messages(errorMessage) should not be errorMessage
        }
      }
    }
  }
}
