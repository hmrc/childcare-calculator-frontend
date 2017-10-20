package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.YouNoWeeksStatPayPYForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.YouNoWeeksStatPayPYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.youNoWeeksStatPayPY

import scala.concurrent.Future

class YouNoWeeksStatPayPYController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.youNoWeeksStatPayPY match {
        case None => YouNoWeeksStatPayPYForm()
        case Some(value) => YouNoWeeksStatPayPYForm().fill(value)
      }
      Ok(youNoWeeksStatPayPY(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      YouNoWeeksStatPayPYForm().bindFromRequest().fold(
        (formWithErrors: Form[Int]) =>
          Future.successful(BadRequest(youNoWeeksStatPayPY(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[Int](request.sessionId, YouNoWeeksStatPayPYId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(YouNoWeeksStatPayPYId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
