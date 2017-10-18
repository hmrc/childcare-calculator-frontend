#!/bin/bash

echo "Applying migration BothPaidPensionPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothPaidPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidPensionPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothPaidPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidPensionPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothPaidPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidPensionPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothPaidPensionPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidPensionPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothPaidPensionPY.title = bothPaidPensionPY" >> ../conf/messages.en
echo "bothPaidPensionPY.heading = bothPaidPensionPY" >> ../conf/messages.en
echo "bothPaidPensionPY.checkYourAnswersLabel = bothPaidPensionPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothPaidPensionPY: Option[Boolean] = cacheMap.getEntry[Boolean](BothPaidPensionPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothPaidPensionPY: Option[AnswerRow] = userAnswers.bothPaidPensionPY map {";\
     print "    x => AnswerRow(\"bothPaidPensionPY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.BothPaidPensionPYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothPaidPensionPY complete"
