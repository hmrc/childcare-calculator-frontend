package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.play.bootstrap.controller.FrontendController
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.DataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.{FrontendAppConfig, Navigator}
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhatIsYourPartnersTaxCodeForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhatIsYourPartnersTaxCodeId
import uk.gov.hmrc.childcarecalculatorfrontend.models.Mode
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whatIsYourPartnersTaxCode

import scala.concurrent.Future

class WhatIsYourPartnersTaxCodeController @Inject()(
                                        appConfig: FrontendAppConfig,
                                        override val messagesApi: MessagesApi,
                                        dataCacheConnector: DataCacheConnector,
                                        navigator: Navigator,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction) extends FrontendController with I18nSupport {

  def onPageLoad(mode: Mode) = (getData andThen requireData) {
    implicit request =>
      val preparedForm = request.userAnswers.whatIsYourPartnersTaxCode match {
        case None => WhatIsYourPartnersTaxCodeForm()
        case Some(value) => WhatIsYourPartnersTaxCodeForm().fill(value)
      }
      Ok(whatIsYourPartnersTaxCode(appConfig, preparedForm, mode))
  }

  def onSubmit(mode: Mode) = (getData andThen requireData).async {
    implicit request =>
      WhatIsYourPartnersTaxCodeForm().bindFromRequest().fold(
        (formWithErrors: Form[Int]) =>
          Future.successful(BadRequest(whatIsYourPartnersTaxCode(appConfig, formWithErrors, mode))),
        (value) =>
          dataCacheConnector.save[Int](request.sessionId, WhatIsYourPartnersTaxCodeId.toString, value).map(cacheMap =>
            Redirect(navigator.nextPage(WhatIsYourPartnersTaxCodeId, mode)(new UserAnswers(cacheMap))))
      )
  }
}
