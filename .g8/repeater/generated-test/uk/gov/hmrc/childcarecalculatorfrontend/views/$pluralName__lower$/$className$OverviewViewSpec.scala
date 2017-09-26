package uk.gov.hmrc.childcarecalculatorfrontend.views.$pluralName;format="lower"$

import play.api.mvc.Call
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$.routes
import uk.gov.hmrc.childcarecalculatorfrontend.models.{NormalMode, $className$}
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.$className$OverviewViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$pluralName;format="lower"$.$className;format="decap"$Overview

class $className$OverviewViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "$pluralName;format="decap"$.overview"

  val mockOnwardRoute = Call("GET", "nextPage")

  def mockItem(index: Int) = $className$(s"field1-\$index", s"field2-\$index")

  val viewModelWithNoItems = $className$OverviewViewModel(Seq(), routes.Add$className$Controller.onPageLoad(NormalMode), mockOnwardRoute)
  val viewModelWithOneItem = $className$OverviewViewModel(Seq(mockItem(0)), routes.Add$className$Controller.onPageLoad(NormalMode), mockOnwardRoute)
  val viewModelWithTwoItems = $className$OverviewViewModel(Seq(mockItem(0), mockItem(1)), routes.Add$className$Controller.onPageLoad(NormalMode), mockOnwardRoute)

  def createView = () => $className;format="decap"$Overview(frontendAppConfig, viewModelWithNoItems, NormalMode)(fakeRequest, messages)

  def createViewUsingViewModel = (viewModel: $className$OverviewViewModel) => $className;format="decap"$Overview(frontendAppConfig, viewModel, NormalMode)(fakeRequest, messages)

  "$className$ Overview view" must {

    behave like normalPage(createView, messageKeyPrefix)

    behave like pageWithBackLink(createView)

    "contain a link to add a new $className;format="decap"$" in {
      val doc = asDocument(createView())
      val addLink = doc.getElementById("add$className$")
      addLink.attr("href") mustBe routes.Add$className$Controller.onPageLoad(NormalMode).url
      addLink.text mustBe messages(s"\$messageKeyPrefix.add_link")
    }

    "contain a button link to go to the next page" in {
      val doc = asDocument(createView())
      val links = doc.getElementsByClass("button")
      links.size mustBe 1
      links.first.text mustBe messages("site.save_and_continue")
      links.first.attr("href") mustBe mockOnwardRoute.url
    }
  }

  "$className$ Overview" when {
    "rendered with two rows" must {
      "have two rows of data" in {
        val doc = asDocument(createViewUsingViewModel(viewModelWithTwoItems))
        val list = doc.getElementById("$pluralName;format="decap"$")
        val items = list.getElementsByTag("li")
        items.size mustBe 2
      }

      for(index <- List(0, 1)) {
        s"have the correct text for row \$index" in {
          val $className;format="decap"$ = mockItem(index)
          val doc = asDocument(createViewUsingViewModel(viewModelWithTwoItems))
          val item = doc.getElementById("$pluralName;format="decap"$").getElementsByTag("li").get(index)
          item.child(0).text mustBe $className;format="decap"$.field1
        }

        s"have a Delete link that goes to the correct location for row \$index" in {
          val $className;format="decap"$ = mockItem(index)
          val doc = asDocument(createViewUsingViewModel(viewModelWithTwoItems))
          val item = doc.getElementById("$pluralName;format="decap"$").getElementsByTag("li").get(index)
          val link = item.child(1).getElementsByTag("a").first
          link.attr("href") mustBe routes.Delete$className$Controller.onPageLoad(index, NormalMode).url
          link.getElementsByAttributeValue("aria-hidden", "true").first.text mustBe messages("site.delete")
          link.getElementsByClass("visually-hidden").first.text mustBe messages("site.hidden-delete", $className;format="decap"$.field1)
        }

        s"have an Edit link that goes to the correct location for row \$index" in {
          val $className;format="decap"$ = mockItem(index)
          val doc = asDocument(createViewUsingViewModel(viewModelWithTwoItems))
          val item = doc.getElementById("$pluralName;format="decap"$").getElementsByTag("li").get(index)
          val link = item.child(2).getElementsByTag("a").first
          link.attr("href") mustBe routes.Edit$className$Controller.onPageLoad(index, NormalMode).url
          link.getElementsByAttributeValue("aria-hidden", "true").first.text mustBe messages("site.edit")
          link.getElementsByClass("visually-hidden").first.text mustBe messages("site.hidden-edit", $className;format="decap"$.field1)
        }
      }
    }
  }
}
