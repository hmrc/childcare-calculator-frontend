#!/bin/bash

echo "Applying migration youStatutoryPayPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /youStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.youStatutoryPayPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /youStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.youStatutoryPayPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeyouStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.youStatutoryPayPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeyouStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.youStatutoryPayPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "youStatutoryPayPY.title = youStatutoryPayPY" >> ../conf/messages.en
echo "youStatutoryPayPY.heading = youStatutoryPayPY" >> ../conf/messages.en
echo "youStatutoryPayPY.checkYourAnswersLabel = youStatutoryPayPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def youStatutoryPayPY: Option[Boolean] = cacheMap.getEntry[Boolean](youStatutoryPayPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def youStatutoryPayPY: Option[AnswerRow] = userAnswers.youStatutoryPayPY map {";\
     print "    x => AnswerRow(\"youStatutoryPayPY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.youStatutoryPayPYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration youStatutoryPayPY complete"
