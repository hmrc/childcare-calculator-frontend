package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.{Inject, Singleton}

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$className;format="decap"$

@Singleton
class $className$Controller @Inject()(val appConfig: FrontendAppConfig,
                                      val messagesApi: MessagesApi,
                                      getData: DataRetrievalAction,
                                      requireData: DataRequiredAction) extends FrontendController(mcc)with I18nSupport {

  def onPageLoad: Action[AnyContent] = (getData andThen requireData) { implicit request =>
    Ok($className;format="decap"$(appConfig))
  }
}
