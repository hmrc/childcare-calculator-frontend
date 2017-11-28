#!/bin/bash

echo "Applying migration YourStatutoryPayType"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourStatutoryPayType               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayTypeController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourStatutoryPayType               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayTypeController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourStatutoryPayType               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayTypeController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourStatutoryPayType               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayTypeController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourStatutoryPayType.title = yourStatutoryPayType" >> ../conf/messages.en
echo "yourStatutoryPayType.heading = yourStatutoryPayType" >> ../conf/messages.en
echo "yourStatutoryPayType.option1 = yourStatutoryPayType" Option 1 >> ../conf/messages.en
echo "yourStatutoryPayType.option2 = yourStatutoryPayType" Option 2 >> ../conf/messages.en
echo "yourStatutoryPayType.checkYourAnswersLabel = yourStatutoryPayType" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourStatutoryPayType: Option[String] = cacheMap.getEntry[String](YourStatutoryPayTypeId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourStatutoryPayType: Option[AnswerRow] = userAnswers.yourStatutoryPayType map {";\
     print "    x => AnswerRow(\"yourStatutoryPayType.checkYourAnswersLabel\", s\"yourStatutoryPayType.$x\", true, routes.YourStatutoryPayTypeController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourStatutoryPayType complete"
