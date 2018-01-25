package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import javax.inject.Inject

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

class FeedbackSurveyController  @Inject()(appConfig: FrontendAppConfig,
                                          val messagesApi: MessagesApi) extends FrontendController with I18nSupport {
  def loadFeedbackSurvey : Action[AnyContent] = Action {
    implicit request => Redirect(appConfig.surveyUrl)
  }
}
