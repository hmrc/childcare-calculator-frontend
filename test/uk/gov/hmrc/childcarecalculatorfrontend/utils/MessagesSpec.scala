/*
 * Copyright 2024 HM Revenue & Customs
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

import play.api.i18n.Messages
import play.api.i18n.Messages.MessageSource
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase

import scala.io.Source

class MessagesSpec extends SpecBase {

  private val matchSingleQuoteOnly = """\w+'{1}\w+""".r

  private val englishMessages = parseMessages("conf/messages")
  private val welshMessages   = parseMessages("conf/messages.cy")

  "All message files" must {
    "have the same set of keys" in
      withClue(describeMismatch(englishMessages.keySet, welshMessages.keySet)) {
        welshMessages.keySet.diff(englishMessages.keySet).size mustBe 0
        englishMessages.keySet.diff(welshMessages.keySet).size mustBe 0
      }

    "have no unescaped single quotes in value" in {
      assertCorrectUseOfQuotes("English", englishMessages)
      assertCorrectUseOfQuotes("Welsh", welshMessages)
    }

    "have a resolvable message for keys which take args" in
      withClue(
        describeMismatch(
          countMessagesWithArgs(englishMessages).keySet,
          countMessagesWithArgs(welshMessages).keySet,
          "missing args in"
        )
      ) {
        countMessagesWithArgs(welshMessages).keySet.diff(countMessagesWithArgs(englishMessages).keySet).size mustBe 0
        countMessagesWithArgs(englishMessages).keySet.diff(countMessagesWithArgs(welshMessages).keySet).size mustBe 0
      }
  }

  private def parseMessages(filename: String): Map[String, String] =
    Messages.parse(
      new MessageSource {
        override def read: String = Source.fromFile(filename).mkString
      },
      filename
    ) match {
      case Right(messages) => messages
      case Left(e)         => throw e
    }

  private def countMessagesWithArgs(messages: Map[String, String]) =
    messages.filter(_._2.contains("{0}"))

  private def assertCorrectUseOfQuotes(label: String, messages: Map[String, String]): Unit =
    messages.foreach { case (key: String, value: String) =>
      withClue(s"In $label, there is an unescaped or invalid quote:[$key][$value]") {
        matchSingleQuoteOnly.findFirstIn(value).isDefined mustBe false
      }
    }

  private def listMissingMessageKeys(header: String, missingKeys: Set[String]) = {
    val displayLine = "\n" + ("@" * 42) + "\n"
    missingKeys.toList.sorted.mkString(header + displayLine, "\n", displayLine)
  }

  private def describeMismatch(
      englishKeySet: Set[String],
      welshKeySet: Set[String],
      description: String = "missing from"
  ) = {
    val missingFromWelsh   = englishKeySet -- welshKeySet
    val missingFromEnglish = welshKeySet -- englishKeySet
    val welshMsg = if (missingFromWelsh.nonEmpty) {
      Some(
        listMissingMessageKeys(
          s"The following message keys are $description the Welsh Set:",
          englishKeySet -- welshKeySet
        )
      )
    } else {
      None
    }
    val englishMsg = if (missingFromEnglish.nonEmpty) {
      Some(
        listMissingMessageKeys(
          s"The following message keys are $description the English Set:",
          welshKeySet -- englishKeySet
        )
      )
    } else {
      None
    }

    Seq(welshMsg, englishMsg).flatten.mkString("\n")
  }

}
