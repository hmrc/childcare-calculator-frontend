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

import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhichDisabilityBenefitsForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.DisabilityBenefit
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.{NewCheckboxViewBehaviours, NewViewBehaviours}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whichDisabilityBenefits

import scala.util.Random

class WhichDisabilityBenefitsViewSpec extends NewViewBehaviours with NewCheckboxViewBehaviours[DisabilityBenefit] {

  val view: whichDisabilityBenefits = inject[whichDisabilityBenefits]
  override val messageKeyPrefix     = "whichDisabilityBenefits"
  override val fieldKey             = "value"
  override val errorMessage         = "error.invalid"

  override val values: Seq[(String, DisabilityBenefit)] = Seq(
    s"whichDisabilityBenefits.${DisabilityBenefit.DisabilityBenefits}" -> DisabilityBenefit.DisabilityBenefits,
    s"whichDisabilityBenefits.${DisabilityBenefit.HigherDisabilityBenefits}" -> DisabilityBenefit.HigherDisabilityBenefits
  )

  override val form: Form[Set[DisabilityBenefit]] = WhichDisabilityBenefitsForm("Foo")

  def render(
      form: Form[Set[DisabilityBenefit]],
      index: Int = 0,
      name: String = "Foo"
  ): Html =
    view(form, index, name)(using fakeRequest, messages)

  override def render(form: Form[Set[DisabilityBenefit]]): Html = render(form = form, index = 0)

  lazy val cases: Seq[(Int, String)] = {
    val names: LazyList[String]     = LazyList.continually(Random.alphanumeric.take(5).mkString)
    lazy val indices: LazyList[Int] = LazyList.from(Random.nextInt(15))
    indices.zip(names).take(3)
  }.distinct

  "WhichDisabilityBenefits view" must {

    behave.like(pageWithBackLink(render))

    behave.like(
      checkboxPage(
        legend = Some(messages(s"$messageKeyPrefix.heading", "Foo")),
        divider = false
      )
    )

    cases.foreach { case (index, name) =>

      s"data of index: $index, name: $name" when
        behave.like(
          normalPageWithTitleParameters(
            () => render(WhichDisabilityBenefitsForm(name), index, name),
            messageKeyPrefix,
            messageKeyPostfix = "",
            Seq("help", "types", "dla", "pip", "types.higher", "dla.higher", "pip.higher"),
            args = Seq(name),
            titleArgs = Seq(messages(s"nth.$index"))
          )
        )
    }
  }

}
