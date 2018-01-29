#!/bin/bash

echo "Applying migration SurveyChildcareSupport"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /surveyChildcareSupport                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.SurveyChildcareSupportController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /surveyChildcareSupport                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.SurveyChildcareSupportController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeSurveyChildcareSupport                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.SurveyChildcareSupportController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeSurveyChildcareSupport                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.SurveyChildcareSupportController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "surveyChildcareSupport.title = surveyChildcareSupport" >> ../conf/messages.en
echo "surveyChildcareSupport.heading = surveyChildcareSupport" >> ../conf/messages.en
echo "surveyChildcareSupport.checkYourAnswersLabel = surveyChildcareSupport" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def surveyChildcareSupport: Option[Boolean] = cacheMap.getEntry[Boolean](SurveyChildcareSupportId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def surveyChildcareSupport: Option[AnswerRow] = userAnswers.surveyChildcareSupport map {";\
     print "    x => AnswerRow(\"surveyChildcareSupport.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.SurveyChildcareSupportController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration SurveyChildcareSupport complete"
