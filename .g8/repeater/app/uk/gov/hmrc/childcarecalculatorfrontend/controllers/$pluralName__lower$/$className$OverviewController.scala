package uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{AuthAction, DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{$className$OverviewId, $pluralName$Id}
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.viewmodels.$className$OverviewViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$pluralName;format="lower"$.$className;format="decap"$Overview
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

class $className$OverviewController @Inject()(val appConfig: FrontendAppConfig,
                                          val messagesApi: MessagesApi,
                                          navigator: Navigator,
                                          getData: DataRetrievalAction,
                                          requireData: DataRequiredAction) extends FrontendController(mcc)with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val viewModel = $className$OverviewViewModel(
        request.userAnswers.$pluralName;format="decap"$.getOrElse(Seq()),
        routes.Add$className$Controller.onPageLoad(mode),
        navigator.nextPage($className$OverviewId, mode)(request.userAnswers))
      Ok($className;format="decap"$Overview(appConfig, viewModel, mode))
  }
}
