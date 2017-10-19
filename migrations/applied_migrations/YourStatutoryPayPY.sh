#!/bin/bash

echo "Applying migration YourStatutoryPayPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourStatutoryPayPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourStatutoryPayPY.title = yourStatutoryPayPY" >> ../conf/messages.en
echo "yourStatutoryPayPY.heading = yourStatutoryPayPY" >> ../conf/messages.en
echo "yourStatutoryPayPY.checkYourAnswersLabel = yourStatutoryPayPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourStatutoryPayPY: Option[Boolean] = cacheMap.getEntry[Boolean](YourStatutoryPayPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourStatutoryPayPY: Option[AnswerRow] = userAnswers.yourStatutoryPayPY map {";\
     print "    x => AnswerRow(\"yourStatutoryPayPY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.YourStatutoryPayPYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourStatutoryPayPY complete"
