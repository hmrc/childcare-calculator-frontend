package uk.gov.hmrc.childcarecalculatorfrontend.views

import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.ViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$className;format="decap"$

class $className$ViewSpec extends ViewBehaviours {

  def view = () => $className;format="decap"$(frontendAppConfig)(fakeRequest, messages)

  "$className$ view" must {

    behave like normalPage(view, "$className;format="decap"$")
  }
}
