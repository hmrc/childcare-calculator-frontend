package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.$className$Form
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.$className$Id
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$className;format="decap"$

import scala.concurrent.Future

class $className;format="cap"$Controller @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val answer = request.userAnswers.$className;format="decap"$
      val preparedForm = answer match {
        case None => $className$Form()
        case Some(value) => $className$Form().fill(value)
      }
      Ok($className;format="decap"$(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      $className$Form().bindFromRequest().fold(
        (formWithErrors: Form[Set[String]]) => {
          Future.successful(BadRequest($className;format="decap"$(appConfig, formWithErrors, mode)))
        },
        (value) => {
          dataCacheConnector.save[Set[String]](request.sessionId, $className$Id.toString, value).map {
            cacheMap =>
              Redirect(navigator.nextPage($className$Id, mode)(new UserAnswers(cacheMap)))
          }
        }
      )
  }
}
