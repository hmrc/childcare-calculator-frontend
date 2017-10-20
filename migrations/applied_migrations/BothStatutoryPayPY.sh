#!/bin/bash

echo "Applying migration BothStatutoryPayPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothStatutoryPayPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothStatutoryPayPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothStatutoryPayPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothStatutoryPayPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothStatutoryPayPY.title = bothStatutoryPayPY" >> ../conf/messages.en
echo "bothStatutoryPayPY.heading = bothStatutoryPayPY" >> ../conf/messages.en
echo "bothStatutoryPayPY.checkYourAnswersLabel = bothStatutoryPayPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothStatutoryPayPY: Option[Boolean] = cacheMap.getEntry[Boolean](BothStatutoryPayPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothStatutoryPayPY: Option[AnswerRow] = userAnswers.bothStatutoryPayPY map {";\
     print "    x => AnswerRow(\"bothStatutoryPayPY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.BothStatutoryPayPYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothStatutoryPayPY complete"
