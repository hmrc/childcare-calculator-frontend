package uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{$pluralName$Id, Delete$className$Id}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, $className$}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$pluralName;format="lower"$.delete$className$

import scala.concurrent.Future

class Delete$className;format="cap"$Controller @Inject()(appConfig: FrontendAppConfig,
                                                  mcc: MessagesControllerComponents,
                                                  dataCacheConnector: DataCacheConnector,
                                                  navigator: Navigator,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction) extends FrontendController(mcc)with I18nSupport {

  def onPageLoad(index: Int, mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      request.userAnswers.$pluralName;format="decap"$.flatMap(x => x.lift(index)) match {
        case None => NotFound
        case Some($className;format="decap"$) => Ok(delete$className$(index, $className;format="decap"$, appConfig, BooleanForm(), mode))
      }
  }

  def onSubmit(index: Int, mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      request.userAnswers.$pluralName;format="decap"$.flatMap(x => x.lift(index)) match {
        case None => Future.successful(NotFound)
        case Some($className;format="decap"$) =>
          BooleanForm().bindFromRequest().fold(
            (formWithErrors: Form[Boolean]) =>
              Future.successful(BadRequest(delete$className$(index, $className;format="decap"$, appConfig, formWithErrors, mode))),
            value =>
              if value {
              dataCacheConnector.removeFromCollection[$className$](request.sessionId, $pluralName$Id.toString, $className;format="decap"$).map(cacheMap =>
                Redirect(navigator.nextPage(Delete$className$Id, mode)(new UserAnswers(cacheMap))))
              } else {
                Future.successful(Redirect(navigator.nextPage(Delete$className$Id, mode)(request.userAnswers)))
              }
          )
      }
  }
}
