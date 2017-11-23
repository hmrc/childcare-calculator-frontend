#!/bin/bash

echo "Applying migration BothStatutoryPayCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothStatutoryPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothStatutoryPayCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothStatutoryPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothStatutoryPayCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothStatutoryPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothStatutoryPayCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothStatutoryPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothStatutoryPayCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothStatutoryPayCY.title = bothStatutoryPayCY" >> ../conf/messages.en
echo "bothStatutoryPayCY.heading = bothStatutoryPayCY" >> ../conf/messages.en
echo "bothStatutoryPayCY.checkYourAnswersLabel = bothStatutoryPayCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothStatutoryPayCY: Option[Boolean] = cacheMap.getEntry[Boolean](BothStatutoryPayCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothStatutoryPayCY: Option[AnswerRow] = userAnswers.bothStatutoryPayCY map {";\
     print "    x => AnswerRow(\"bothStatutoryPayCY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.BothStatutoryPayCYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothStatutoryPayCY complete"
