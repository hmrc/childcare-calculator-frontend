package uk.gov.hmrc.childcarecalculatorfrontend.controllers.$pluralName;format="lower"$

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.$className$Form
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{Edit$className$Id, $pluralName$Id}
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Mode, $className$}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.$pluralName;format="lower"$.edit$className$

import scala.concurrent.Future

class Edit$className;format="cap"$Controller @Inject()(appConfig: FrontendAppConfig,
                                                  mcc: MessagesControllerComponents,
                                                  dataCacheConnector: DataCacheConnector,
                                                  navigator: Navigator,
                                                  getData: DataRetrievalAction,
                                                  requireData: DataRequiredAction) extends FrontendController(mcc)with I18nSupport {

  def onPageLoad(index: Int, mode: Mode): Action[AnyContent] = (getData andThen requireData) {
    implicit request =>
      request.userAnswers.$pluralName;format="decap"$.flatMap(x => x.lift(index)) match {
        case None => NotFound
        case Some($className;format="decap"$) => Ok(edit$className$(index, appConfig, $className$Form().fill($className;format="decap"$), mode))
      }
  }

  def onSubmit(index: Int, mode: Mode): Action[AnyContent] = (getData andThen requireData).async {
    implicit request =>
      request.userAnswers.$pluralName;format="decap"$.flatMap(x => x.lift(index)) match {
        case None => Future.successful(NotFound)
        case Some($className;format="decap"$) =>
          $className$Form().bindFromRequest().fold(
            (formWithErrors: Form[$className$]) =>
              Future.successful(BadRequest(edit$className$(index, appConfig, formWithErrors, mode))),
            value =>
              dataCacheConnector.replaceInCollection[$className$](request.sessionId, $pluralName$Id.toString, index, value).map(cacheMap =>
                Redirect(navigator.nextPage(Edit$className$Id, mode)(new UserAnswers(cacheMap)))
              )
          )
      }
  }
}
