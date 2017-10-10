package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.AreYouSelfEmployedOrApprenticeForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.AreYouSelfEmployedOrApprenticeId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.areYouSelfEmployedOrApprentice

import scala.concurrent.Future

class AreYouSelfEmployedOrApprenticeController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.areYouSelfEmployedOrApprentice match {
        case None => AreYouSelfEmployedOrApprenticeForm()
        case Some(value) => AreYouSelfEmployedOrApprenticeForm().fill(value)
      }
      Ok(areYouSelfEmployedOrApprentice(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      AreYouSelfEmployedOrApprenticeForm().bindFromRequest().fold(
        (formWithErrors: Form[String]) =>
          Future.successful(BadRequest(areYouSelfEmployedOrApprentice(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[String](request.sessionId, AreYouSelfEmployedOrApprenticeId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(AreYouSelfEmployedOrApprenticeId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
