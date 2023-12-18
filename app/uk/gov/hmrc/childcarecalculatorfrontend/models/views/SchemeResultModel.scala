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

package uk.gov.hmrc.childcarecalculatorfrontend.models.views

import play.twirl.api.Html

case class SchemeResultModel( title: String,
                              id: Option[String] = None,
                              couldGet: Option[String] = None,
                              eligibility: Option[EligibilityModel] = None,
                              twoYearOld: Option[String] = None,
                              threeAndFour: Option[String] = None,
                              periodText: Option[String] = None,
                              para1: Option[String] = None,
                              para2: Option[Html] = None,
                              para3: Option[String] = None,
                              detailSummary: Option[String] = None,
                              detailPara1: Option[String] = None,
                              detailPara2: Option[String] = None,
                              detailPara2List: Seq[String] = Nil,
                              detailPara2WithLink: Option[Html] = None,
                              detailPara3: Option[String] = None,
                              detailPara3WithLink: Option[Html] = None,
                              detailPara4: Option[String] = None,
                              notEligibleID: Tuple2[Option[String], Option[String]] = (None, None),
                              insetText: Option[String] = None,
                              insetTextId: Option[String] = None,
                              warningMessage: Option[String] = None,
                              displayTCGuidanceLink: Boolean = false,
                              sectionBreak: Boolean = true)
