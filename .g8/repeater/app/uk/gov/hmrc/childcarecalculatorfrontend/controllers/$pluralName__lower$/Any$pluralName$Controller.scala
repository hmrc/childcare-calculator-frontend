package uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.Any$pluralName$Id
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$pluralName;format="lower"$.any$pluralName$

import scala.concurrent.Future

class Any$pluralName$Controller @Inject()(appConfig: FrontendAppConfig,
                                         mcc: MessagesControllerComponents,
                                         dataCacheConnector: DataCacheConnector,
                                         navigator: Navigator,
                                         getData: DataRetrievalAction,
                                         requireData: DataRequiredAction) extends FrontendController(mcc)with I18nSupport {

  def onPageLoad(mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.any$pluralName$ match {
        case None => BooleanForm()
        case Some(value) => BooleanForm().fill(value)
      }
      Ok(any$pluralName$(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      BooleanForm().bindFromRequest().fold(
        (formWithErrors: Form[Boolean]) =>
          Future.successful(BadRequest(any$pluralName$(appConfig, formWithErrors, mode))),
        value =>
          dataCacheConnector.save[Boolean](request.sessionId, Any$pluralName$Id.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(Any$pluralName$Id, mode)(new UserAnswers(cacheMap))))
      )
  }
}
