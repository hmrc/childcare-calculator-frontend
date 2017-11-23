#!/bin/bash

echo "Applying migration YourMinimumEarnings"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourMinimumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourMinimumEarningsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourMinimumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourMinimumEarningsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourMinimumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourMinimumEarningsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourMinimumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourMinimumEarningsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourMinimumEarnings.title = yourMinimumEarnings" >> ../conf/messages.en
echo "yourMinimumEarnings.heading = yourMinimumEarnings" >> ../conf/messages.en
echo "yourMinimumEarnings.checkYourAnswersLabel = yourMinimumEarnings" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourMinimumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](YourMinimumEarningsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourMinimumEarnings: Option[AnswerRow] = userAnswers.yourMinimumEarnings map {";\
     print "    x => AnswerRow(\"yourMinimumEarnings.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.YourMinimumEarningsController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourMinimumEarnings complete"
