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

package uk.gov.hmrc.childcarecalculatorfrontend

import org.jsoup.nodes.Document
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import uk.gov.hmrc.play.test.UnitSpec
import scala.concurrent.ExecutionContext.Implicits.global

trait TemplatesValidator extends UnitSpec {
  case class ElementDetails(
                             value: String,
                             id: Option[String] = None,
                             elementClass: Option[String] = None,
                             attribute: Option[String] = None,
                             attributeValue: Option[String] = None,
                             checkAttribute: Option[String] = None,
                             tagName: Option[String] = None,
                             tagIndex: Option[Int] = None)

  val contentData: List[ElementDetails]
  val linksData: List[ElementDetails]

  def verifyPageContent(dynamicContentData: List[ElementDetails] = List.empty)(implicit doc: Document): Unit = {
    verifyContent(contentData ++ dynamicContentData)
  }

  private def verifyContent(data: List[ElementDetails])(implicit doc: Document): Unit = {
    data.map { element =>
      element match {
        case ElementDetails(value, Some(id), Some(elementClass), _, _, _, _, _) =>
          withClue(s"Element with id '${id}' has class '${elementClass}': ") {
            doc.getElementById(id).hasClass(elementClass) shouldBe value
          }
        case ElementDetails(value, Some(id), _, _, _, _, Some(tagName), Some(tagIndex)) =>
          withClue(s"Element with id '${id}', tag '${tagName}' and tagIndex '${tagIndex}': ") {
            doc.getElementById(id).getElementsByTag(tagName).get(tagIndex).text() shouldBe value
          }
        case ElementDetails(value, Some(id), _, _, _, None, _, _) =>
          withClue(s"Element with id '${id}': ") {
            doc.getElementById(id).text() shouldBe value
          }
        case ElementDetails(value, Some(id), _, _, _, Some(checkAttribute), _, _) =>
          withClue(s"Element with id '${id}' and attribute '${checkAttribute}': ") {
            doc.getElementById(id).attr(checkAttribute) shouldBe value
          }
        case ElementDetails(value, _, _, Some(attribute), Some(attributeValue), None, _, _) =>
          withClue(s"Element with attribute's '${attribute}' value '${attributeValue}' should have value '${value}': ") {
            doc.getElementsByAttributeValue(attribute, attributeValue).text() shouldBe value
          }
        case ElementDetails(value, _, _, Some(attribute), Some(attributeValue), Some(checkAttribute), _, _) =>
          withClue(s"Element with attribute's '${attribute}' value '${attributeValue}' should have attribure '${checkAttribute}' with value '${value}': ") {
            doc.getElementsByAttributeValue(attribute, attributeValue).attr(checkAttribute) shouldBe value
          }
        case ElementDetails(value, id, _, _, _, _, Some(tagName), tagIndex) =>
          withClue(s"Element with tag '${tagName}' and tagIndex '${tagIndex.getOrElse(0)}': ") {
            doc.getElementById(id.getOrElse("content")).getElementsByTag(tagName).get(tagIndex.getOrElse(0)).text() shouldBe value
          }
        case ElementDetails(value, _, Some(elementClass), _, _, _, _, tagIndex) =>
          withClue(s"Element with class '${elementClass}' and index '${tagIndex.getOrElse(0)}': ") {
            doc.getElementsByClass(elementClass).get(tagIndex.getOrElse(0)).text() shouldBe value
          }
        case _ => throw new NotImplementedException
      }
    }
  }

  def verifyPageLinks(dynamicLinks: List[ElementDetails] = List.empty)(implicit doc: Document): Unit = {
    verifyLinks(linksData ++ dynamicLinks)
  }

  def verifyMissingContent(data: List[ElementDetails] = List.empty)(implicit doc: Document): Unit = {
    data.map { element =>
      element match {
        case ElementDetails(value, Some(id), _, _, _, _, _, _) =>
          withClue(s"Element with id '${id}' shouldn't be displayed:  ") {
            doc.getElementById(id) shouldBe null
          }
        case _ => throw new NotImplementedException
      }
    }
  }

  def verifyLinks(links: List[ElementDetails])(implicit doc: Document): Unit = {
    links.map { element =>
      element match {
        case ElementDetails(value, Some(id), _, _, _, Some(checkAttribute), _, _) => {
          withClue(s"Attribute '${checkAttribute}' for element with id '${id}' should be '${value}': ") {
            doc.getElementById(id).attr(checkAttribute).endsWith(value) shouldBe true
          }
        }
        case ElementDetails(value, _, Some(elementClass), _, _, Some(checkAttribute), _, _) =>
          withClue(s"Attribute '${checkAttribute}' for element with class '${elementClass}' should be '${value}': ") {
            doc.getElementsByClass(elementClass).attr(checkAttribute).endsWith(value) shouldBe true
          }
        case _ => throw new NotImplementedException
      }
    }
  }

  def verifyChecks(checkedElements: List[String] = List.empty)(implicit doc: Document): Unit = {
    val allCheckedElements = doc.getElementsByAttribute("checked")
    allCheckedElements.size shouldBe checkedElements.size
    allCheckedElements.map { element =>
      withClue(s"Element with '${element.attr("id")}' shouldn't be selected: ") {
        checkedElements.contains(element.attr("id")) shouldBe true
      }
    }
  }

  def verifyErrors(errors: Map[String, String] = Map.empty, validDateInlineErrors: Boolean = true)(implicit doc: Document): Unit = {
    doc.getElementById("errorTitle").text() shouldBe "There is a problem"
    doc.getElementById("error-summary-heading").text() shouldBe "Check you have answered the question correctly"
    val totalAmountOfErrors = errors.size
    doc.getElementsByClass("js-error-summary-messages").first().getElementsByTag("li").size() shouldBe totalAmountOfErrors
    if(validDateInlineErrors) {
      doc.getElementsByClass("error-notification").size() shouldBe totalAmountOfErrors
    }
    doc.getElementById("error-summary-display").hasClass("error-summary--show") should not be errors.isEmpty
    errors.map {
      case (focusElement, errorMessage) => {
        val errorSummary = doc.getElementById(focusElement + "-error-summary")
        errorSummary.text() shouldBe errorMessage
        errorSummary.attr("data-focuses") shouldBe focusElement
        errorSummary.attr("href").replaceAll("""[\.\[\]]""", "") shouldBe "#" + focusElement
        if(validDateInlineErrors) {
          doc.getElementById(focusElement + "-error-message").text() shouldBe errorMessage
        }
      }
    }
  }

}
