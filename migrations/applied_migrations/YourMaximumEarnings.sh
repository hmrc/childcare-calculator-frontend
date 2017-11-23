#!/bin/bash

echo "Applying migration YourMaximumEarnings"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourMaximumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourMaximumEarningsController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourMaximumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourMaximumEarningsController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourMaximumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourMaximumEarningsController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourMaximumEarnings                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourMaximumEarningsController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourMaximumEarnings.title = yourMaximumEarnings" >> ../conf/messages.en
echo "yourMaximumEarnings.heading = yourMaximumEarnings" >> ../conf/messages.en
echo "yourMaximumEarnings.checkYourAnswersLabel = yourMaximumEarnings" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourMaximumEarnings: Option[Boolean] = cacheMap.getEntry[Boolean](YourMaximumEarningsId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourMaximumEarnings: Option[AnswerRow] = userAnswers.yourMaximumEarnings map {";\
     print "    x => AnswerRow(\"yourMaximumEarnings.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.YourMaximumEarningsController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourMaximumEarnings complete"
