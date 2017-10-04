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

package uk.gov.hmrc.childcarecalculatorfrontend.utils

object Utils {

  /**
    * Throws exception with appropriate error message if optional element value is None otherwise returns the value
    * ex - val a = Some(5), return value is 5
    *      val a = Some(PageObjects), return value is PageObjects
    *      val a = None , return is runtime exception
    * @param optionalElement
    * @param controllerId
    * @param objectName
    * @param errorMessage
    * @tparam T
    */

  def getOrException[T](optionalElement: Option[T],
                        controllerId: Option[String] = None,
                        objectName: Option[String] = None,
                        errorMessage: String = "no element found"): T = {

    val controller = controllerId.getOrElse("")
    val objectId = objectName.getOrElse("")

    if(controllerId.isDefined && objectName.isDefined){
      optionalElement.fold(throw new RuntimeException(s"no element found in $controller while fetching $objectId"))(identity)
    }else{
      optionalElement.fold(throw new RuntimeException(errorMessage))(identity)
    }

  }

}
