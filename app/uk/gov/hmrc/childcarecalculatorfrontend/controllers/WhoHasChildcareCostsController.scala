package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoHasChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhoHasChildcareCostsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoHasChildcareCosts

import scala.concurrent.Future

class WhoHasChildcareCostsController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val answer = request.userAnswers.whoHasChildcareCosts
      val preparedForm = answer match {
        case None => WhoHasChildcareCostsForm()
        case Some(value) => WhoHasChildcareCostsForm().fill(value)
      }
      Ok(whoHasChildcareCosts(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      WhoHasChildcareCostsForm().bindFromRequest().fold(
        (formWithErrors: Form[Set[String]]) => {
          Future.successful(BadRequest(whoHasChildcareCosts(appConfig, formWithErrors, mode)))
        },
        (value) => {
          dataCacheConnector.save[Set[String]](request.sessionId, WhoHasChildcareCostsId.toString, value).map {
            cacheMap =>
              Redirect(navigator.nextPage(WhoHasChildcareCostsId, mode)(new UserAnswers(cacheMap)))
          }
        }
      )
  }
}
