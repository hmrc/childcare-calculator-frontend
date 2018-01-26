#!/bin/bash

echo "Applying migration SurveyDoNotUnderstand"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /surveyDoNotUnderstand               uk.gov.hmrc.childcarecalculatorfrontend.controllers.SurveyDoNotUnderstandController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /surveyDoNotUnderstand               uk.gov.hmrc.childcarecalculatorfrontend.controllers.SurveyDoNotUnderstandController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSurveyDoNotUnderstand               uk.gov.hmrc.childcarecalculatorfrontend.controllers.SurveyDoNotUnderstandController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSurveyDoNotUnderstand               uk.gov.hmrc.childcarecalculatorfrontend.controllers.SurveyDoNotUnderstandController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "surveyDoNotUnderstand.title = surveyDoNotUnderstand" >> ../conf/messages.en
echo "surveyDoNotUnderstand.heading = surveyDoNotUnderstand" >> ../conf/messages.en
echo "surveyDoNotUnderstand.checkYourAnswersLabel = surveyDoNotUnderstand" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def surveyDoNotUnderstand: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](SurveyDoNotUnderstandId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def surveyDoNotUnderstand: Option[AnswerRow] = userAnswers.surveyDoNotUnderstand map {";\
     print "    x => AnswerRow(\"surveyDoNotUnderstand.checkYourAnswersLabel\", s\"$x\", false, routes.SurveyDoNotUnderstandController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration SurveyDoNotUnderstand complete"
