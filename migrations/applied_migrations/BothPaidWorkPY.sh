#!/bin/bash

echo "Applying migration BothPaidWorkPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothPaidWorkPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidWorkPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothPaidWorkPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidWorkPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothPaidWorkPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidWorkPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothPaidWorkPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothPaidWorkPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothPaidWorkPY.title = bothPaidWorkPY" >> ../conf/messages.en
echo "bothPaidWorkPY.heading = bothPaidWorkPY" >> ../conf/messages.en
echo "bothPaidWorkPY.checkYourAnswersLabel = bothPaidWorkPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothPaidWorkPY: Option[Boolean] = cacheMap.getEntry[Boolean](BothPaidWorkPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothPaidWorkPY: Option[AnswerRow] = userAnswers.bothPaidWorkPY map {";\
     print "    x => AnswerRow(\"bothPaidWorkPY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.BothPaidWorkPYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothPaidWorkPY complete"
