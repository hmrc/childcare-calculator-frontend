package uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.$className$Form
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{Add$className$Id, $pluralName$Id}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, $className$}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$pluralName;format="lower"$.add$className$

import scala.concurrent.Future

class Add$className;format="cap"$Controller @Inject()(appConfig: FrontendAppConfig,
                                                  override val messagesApi: MessagesApi,
                                                  dataCacheConnector: DataCacheConnector,
                                                  navigator: Navigator,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      Ok(add$className$(appConfig, $className$Form(), mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      $className$Form().bindFromRequest().fold(
        (formWithErrors: Form[$className$]) =>
          Future.successful(BadRequest(add$className$(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.addToCollection[$className$](request.sessionId, $pluralName$Id.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(Add$className$Id, mode)(new UserAnswers(cacheMap))))
      )
  }
}
