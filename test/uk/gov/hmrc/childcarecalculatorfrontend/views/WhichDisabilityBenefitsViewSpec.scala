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

import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichDisabilityBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{DisabilityBenefits, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{CheckboxViewBehaviours, ViewBehaviours}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichDisabilityBenefits

import scala.util.Random

class WhichDisabilityBenefitsViewSpec extends ViewBehaviours with CheckboxViewBehaviours[DisabilityBenefits.Value] {

  val messageKeyPrefix = "whichDisabilityBenefits"
  val fieldKey = "value"
  val errorMessage = "error.invalid"

  val values: Map[String, DisabilityBenefits.Value] =
    WhichDisabilityBenefitsForm.options.map {
      case (k, v) => k -> DisabilityBenefits.withName(v)
    }

  def form: Form[Set[DisabilityBenefits.Value]] = WhichDisabilityBenefitsForm("Foo")

  override def createView(): Html = createView(form)

  def createView(form: Form[Set[DisabilityBenefits.Value]]): Html = createView(form, 0, "Foo")

  def createView(
                  form: Form[Set[DisabilityBenefits.Value]],
                  index: Int,
                  name: String
                ): Html =
    whichDisabilityBenefits(frontendAppConfig, form, index, name, NormalMode)(fakeRequest, messages)

  lazy val cases: Seq[(Int, String)] = {
    val names: Stream[String] = Stream.continually(Random.alphanumeric.take(5).mkString)
    lazy val indices: Stream[Int] = Stream.from(Random.nextInt(15))
    indices.zip(names).take(3)
  }.distinct

  "WhichDisabilityBenefits view" must {

    behave like pageWithBackLink(createView)

    behave like checkboxPage()

    cases.foreach {
      case (index, name) =>

        s"data of index: $index, name: $name" when {

          behave like normalPageWithTitleAsString(
            () => createView(WhichDisabilityBenefitsForm(name), index, name),
            messageKeyPrefix,
            messages("whichDisabilityBenefits.title"),
            Some(messages("whichDisabilityBenefits.heading", name)),
            Seq("help", "types", "dla", "pip", "types.higher", "dla.higher", "pip.higher")
          )
        }
    }
  }
}
