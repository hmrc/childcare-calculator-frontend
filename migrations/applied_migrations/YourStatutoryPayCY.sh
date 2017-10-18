#!/bin/bash

echo "Applying migration YourStatutoryPayCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourStatutoryPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourStatutoryPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourStatutoryPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourStatutoryPayCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourStatutoryPayCY.title = yourStatutoryPayCY" >> ../conf/messages.en
echo "yourStatutoryPayCY.heading = yourStatutoryPayCY" >> ../conf/messages.en
echo "yourStatutoryPayCY.checkYourAnswersLabel = yourStatutoryPayCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourStatutoryPayCY: Option[Boolean] = cacheMap.getEntry[Boolean](YourStatutoryPayCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourStatutoryPayCY: Option[AnswerRow] = userAnswers.yourStatutoryPayCY map {";\
     print "    x => AnswerRow(\"yourStatutoryPayCY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.YourStatutoryPayCYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourStatutoryPayCY complete"
