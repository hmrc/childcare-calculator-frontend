#!/bin/bash

echo "Applying migration BothPaidWorkCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothPaidWorkCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidWorkCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothPaidWorkCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidWorkCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothPaidWorkCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidWorkCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothPaidWorkCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidWorkCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothPaidWorkCY.title = bothPaidWorkCY" >> ../conf/messages.en
echo "bothPaidWorkCY.heading = bothPaidWorkCY" >> ../conf/messages.en
echo "bothPaidWorkCY.checkYourAnswersLabel = bothPaidWorkCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothPaidWorkCY: Option[Boolean] = cacheMap.getEntry[Boolean](BothPaidWorkCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothPaidWorkCY: Option[AnswerRow] = userAnswers.bothPaidWorkCY map {";\
     print "    x => AnswerRow(\"bothPaidWorkCY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.BothPaidWorkCYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothPaidWorkCY complete"
