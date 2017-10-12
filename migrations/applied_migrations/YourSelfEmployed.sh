#!/bin/bash

echo "Applying migration YourSelfEmployed"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourSelfEmployed                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourSelfEmployedController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourSelfEmployed                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourSelfEmployedController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourSelfEmployed                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourSelfEmployedController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourSelfEmployed                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourSelfEmployedController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourSelfEmployed.title = yourSelfEmployed" >> ../conf/messages.en
echo "yourSelfEmployed.heading = yourSelfEmployed" >> ../conf/messages.en
echo "yourSelfEmployed.checkYourAnswersLabel = yourSelfEmployed" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourSelfEmployed: Option[Boolean] = cacheMap.getEntry[Boolean](YourSelfEmployedId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourSelfEmployed: Option[AnswerRow] = userAnswers.yourSelfEmployed map {";\
     print "    x => AnswerRow(\"yourSelfEmployed.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.YourSelfEmployedController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourSelfEmployed complete"
