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

package uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours

import uk.gov.hmrc.childcarecalculatorfrontend.views.NewViewSpecBase
import play.twirl.api.HtmlFormat

trait NewViewBehaviours extends NewViewSpecBase {

  def normalPage(view: () => HtmlFormat.Appendable,
                 messageKeyPrefix: String,
                 expectedGuidanceKeys: String*): Unit = {

    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc = asDocument(view())
          val nav = doc.getElementsByClass("hmrc-header__service-name")
          nav.text mustBe messages("site.service_name")
        }

        "display the correct browser title" in {
          val doc = asDocument(view())
          assertEqualsValue(doc, "title", messages(s"$messageKeyPrefix.title")+" - "+messages("site.service_name")+" - GOV.UK")
          assertNotContainsValue(doc, "title", "{}")
        }

        "display the correct page title" in {
          val doc = asDocument(view())
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading", 0)
        }

        "display the correct guidance" in {
          val doc = asDocument(view())
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display a beta banner" in {
          val doc = asDocument(view())
          assertRenderedByCssSelector(doc, ".govuk-phase-banner")
        }

        "not display HMRC branding" in {
          val doc = asDocument(view())
          assertNotRenderedByCssSelector(doc, ".organisation-logo")
        }
      }
    }
  }

  def normalPageWithCurrencySymbol(view: () => HtmlFormat.Appendable,
                                   messageKeyPrefix: String,
                                   expectedGuidanceKeys: String*): Unit = {

    normalPage(view, messageKeyPrefix, expectedGuidanceKeys: _*)

    "behave like a currency symbol page" when {
      "rendered" must {
        "display the £ sign and have currencySymbol class" in {
          val doc = asDocument(view())
          assertRenderedByCssSelector(doc, ".currencySymbol")
          assertContainsText(doc, "£")
        }
      }
    }
  }

  def normalPageWithTitleAsString(
                                   view: () => HtmlFormat.Appendable,
                                   messageKeyPrefix: String,
                                   messageKeyPostfix: String,
                                   title: String,
                                   heading: Option[String],
                                   expectedGuidanceKeys: Seq[String],
                                   args: Any*
                                 ): Unit = {

    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc = asDocument(view())
          val nav = doc.getElementsByClass("hmrc-header__service-name")
          nav.text mustBe messages("site.service_name")
        }

        "display the correct browser title" in {
          val doc = asDocument(view())
          assertEqualsValue(doc, "title", title + " - " + messages("site.service_name") + " - GOV.UK")
          assertNotContainsValue(doc, "title", "{}")
        }

        "display the correct page title" in {
          val doc = asDocument(view())
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading$messageKeyPostfix", args: _*)
        }

        "display the correct guidance" in {
          val doc = asDocument(view())
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display a beta banner" in {
          val doc = asDocument(view())
          assertRenderedByCssSelector(doc, ".govuk-phase-banner")
        }

        "not display HMRC branding" in {
          val doc = asDocument(view())
          assertNotRenderedByCssSelector(doc, ".organisation-logo")
        }
      }
    }
  }

  def normalPageWithTitleParameters(
                                   view: () => HtmlFormat.Appendable,
                                   messageKeyPrefix: String,
                                   messageKeyPostfix: String,
                                   expectedGuidanceKeys: Seq[String],
                                   args: Seq[String] = Nil,
                                   titleArgs: Seq[String] = Nil
                                 ): Unit = {

    "behave like a normal page" when {
      "rendered" must {
        "have the correct banner title" in {
          val doc = asDocument(view())
          val nav = doc.getElementsByClass("hmrc-header__service-name")
          nav.text mustBe messages("site.service_name")
        }

        "display the correct browser title" in {
          val doc = asDocument(view())
          assertEqualsValue(doc, "title", messages(s"$messageKeyPrefix.title", titleArgs: _*) + " - " + messages("site.service_name") + " - GOV.UK")
          assertNotContainsValue(doc, "title", "{}")
        }

        "display the correct page title" in {
          val doc = asDocument(view())
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading$messageKeyPostfix", args: _*)
        }

        "display the correct guidance" in {
          val doc = asDocument(view())
          for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
        }

        "display a beta banner" in {
          val doc = asDocument(view())
          assertRenderedByCssSelector(doc, ".govuk-phase-banner")
        }

        "not display HMRC branding" in {
          val doc = asDocument(view())
          assertNotRenderedByCssSelector(doc, ".organisation-logo")
        }
      }
    }
  }

  def pageWithBackLink(view: () => HtmlFormat.Appendable): Unit = {

    "behave like a page with a back link" must {
      "have a back link" in {
        val doc = asDocument(view())
        assertRenderedByCssSelector(doc, ".govuk-back-link")
      }
    }
  }

  def resultPage(view: () => HtmlFormat.Appendable): Unit = {
    "behave like a result page" when {
      "rendered" must {
        "have a link to feedback survey" in {
          val doc = asDocument(view())
          assertRenderedById(doc, "feedbackSurveyLink")
        }
      }
    }
  }
}
