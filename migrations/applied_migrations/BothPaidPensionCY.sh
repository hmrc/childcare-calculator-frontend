#!/bin/bash

echo "Applying migration BothPaidPensionCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothPaidPensionCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidPensionCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothPaidPensionCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidPensionCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothPaidPensionCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidPensionCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothPaidPensionCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidPensionCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothPaidPensionCY.title = bothPaidPensionCY" >> ../conf/messages.en
echo "bothPaidPensionCY.heading = bothPaidPensionCY" >> ../conf/messages.en
echo "bothPaidPensionCY.checkYourAnswersLabel = bothPaidPensionCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothPaidPensionCY: Option[Boolean] = cacheMap.getEntry[Boolean](BothPaidPensionCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothPaidPensionCY: Option[AnswerRow] = userAnswers.bothPaidPensionCY map {";\
     print "    x => AnswerRow(\"bothPaidPensionCY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.BothPaidPensionCYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothPaidPensionCY complete"
