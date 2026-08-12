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

package uk.gov.hmrc.childcarecalculatorfrontend.views
/*
 * Copyright 2021 HM Revenue & Customs
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

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalactic.source.Position
import org.scalatest.Assertion
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase

import scala.jdk.CollectionConverters.*

trait NewViewSpecBase extends SpecBase {

  def asDocument(html: Html): Document = Jsoup.parse(html.toString())

  def assertEqualsMessage(doc: Document, cssSelector: String, expectedMessageKey: String)(using Position): Assertion =
    assertEqualsValue(doc, cssSelector, messages(expectedMessageKey))

  def assertEqualsValue(doc: Document, cssSelector: String, expectedValue: String)(using Position): Assertion = {
    val elements = doc.select(cssSelector)

    if (elements.isEmpty) throw new IllegalArgumentException(s"CSS Selector $cssSelector wasn't rendered.")

    // <p> HTML elements are rendered out with a carriage return on some pages, so discount for comparison
    assert(elements.first().html().replace("\n", "") == expectedValue)
  }

  def assertNotContainsValue(doc: Document, cssSelector: String, expectedValue: String)(using Position): Assertion = {
    val elements = doc.select(cssSelector)

    if (elements.isEmpty) throw new IllegalArgumentException(s"CSS Selector $cssSelector wasn't rendered.")

    // <p> HTML elements are rendered out with a carriage return on some pages, so discount for comparison
    assert(!elements.first().html().replace("\n", "").sorted.contains(expectedValue.sorted))
  }

  def assertPageTitleEqualsMessage(doc: Document, expectedMessageKey: String, args: Any*)(using Position): Assertion = {
    val headers = doc.getElementsByTag("h1")
    headers.first.text.replaceAll("\u00a0", " ") mustBe messages(expectedMessageKey, args*).replaceAll("&nbsp;", " ")
  }

  def assertPageTitleEqualsString(doc: Document, expectedMessage: String)(using Position): Assertion = {
    val headers = doc.getElementsByTag("h1")
    headers.size mustBe 1
    headers.first.text.replaceAll("\u00a0", " ") mustBe expectedMessage.replaceAll("&nbsp;", " ")
  }

  def assertContainsText(doc: Document, text: String)(using Position): Assertion =
    assert(doc.toString.contains(text), "\n\ntext " + text + " was not rendered on the page.\n")

  def assertNotContainsText(doc: Document, text: String)(using Position): Assertion =
    assert(!doc.toString.contains(text), "\n\ntext " + text + " was rendered on the page.\n")

  def assertContainsLinkWithHref(doc: Document, text: String, href: String)(using Position): Assertion =
    doc.select("main a").asScala.toList.map(l => (l.text, l.attr("href"))) must contain((text, href))

  def assertContainsMessages(doc: Document, expectedMessageKeys: String*)(using Position): Unit =
    for (key <- expectedMessageKeys) assertContainsText(doc, messages(key))

  def assertRenderedById(doc: Document, id: String)(using Position): Assertion =
    assert(doc.getElementById(id) != null, "\n\nElement " + id + " was not rendered on the page.\n")

  def assertNotRenderedById(doc: Document, id: String)(using Position): Assertion =
    assert(doc.getElementById(id) == null, "\n\nElement " + id + " was rendered on the page.\n")

  def assertRenderedByCssSelector(doc: Document, cssSelector: String)(using Position): Assertion =
    assert(!doc.select(cssSelector).isEmpty, "Element " + cssSelector + " was not rendered on the page.")

  def assertNotRenderedByCssSelector(doc: Document, cssSelector: String)(using Position): Assertion =
    assert(doc.select(cssSelector).isEmpty, "\n\nElement " + cssSelector + " was rendered on the page.\n")

  def assertContainsLabel(doc: Document, forElement: String, expectedText: String)(using Position): Assertion = {
    val labels = doc.getElementsByAttributeValue("for", forElement)
    assert(labels.size == 1, s"\n\nLabel for $forElement was not rendered on the page.")
    assert(labels.first.text() == expectedText, s"\n\nLabel for $forElement was not $expectedText")
  }

  def assertElementHasClass(doc: Document, id: String, expectedClass: String)(using Position): Assertion =
    assert(doc.getElementById(id).hasClass(expectedClass), s"\n\nElement $id does not have class $expectedClass")

  def assertContainsRadioButton(doc: Document, id: String, name: String, value: String, isChecked: Boolean)(
      using Position
  ): Assertion = {
    assertRenderedById(doc, id)
    val radio = doc.getElementById(id)
    assert(radio.attr("name") == name, s"\n\nElement $id does not have name $name")
    assert(radio.attr("value") == value, s"\n\nElement $id does not have value $value")
    isChecked match {
      case true => assert(radio.attr("checked") != null, s"\n\nElement $id is not checked")
      case _ => assert(!radio.hasAttr("checked") && radio.attr("checked") != "checked", s"\n\nElement $id is checked")
    }
  }

  def assertBulletListValues(doc: Document, expected: Seq[String], selector: String)(using Position): Assertion = {
    val bulletItems        = doc.select(selector)
    val expectedListValues = expected.map(messages(_))

    bulletItems.size() mustBe expected.size
    bulletItems.eachText().asScala.toList mustBe expectedListValues

  }

}
