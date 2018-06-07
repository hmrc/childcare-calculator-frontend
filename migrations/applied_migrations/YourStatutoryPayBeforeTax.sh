#!/bin/bash

echo "Applying migration YourStatutoryPayBeforeTax"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourStatutoryPayBeforeTax               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayBeforeTaxController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourStatutoryPayBeforeTax               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayBeforeTaxController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourStatutoryPayBeforeTax               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayBeforeTaxController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourStatutoryPayBeforeTax               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayBeforeTaxController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages
echo "yourStatutoryPayBeforeTax.title = yourStatutoryPayBeforeTax" >> ../conf/messages
echo "yourStatutoryPayBeforeTax.heading = yourStatutoryPayBeforeTax" >> ../conf/messages
echo "yourStatutoryPayBeforeTax.option1 = yourStatutoryPayBeforeTax" Option 1 >> ../conf/messages
echo "yourStatutoryPayBeforeTax.option2 = yourStatutoryPayBeforeTax" Option 2 >> ../conf/messages
echo "yourStatutoryPayBeforeTax.checkYourAnswersLabel = yourStatutoryPayBeforeTax" >> ../conf/messages

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourStatutoryPayBeforeTax: Option[String] = cacheMap.getEntry[String](YourStatutoryPayBeforeTaxId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourStatutoryPayBeforeTax: Option[AnswerRow] = userAnswers.yourStatutoryPayBeforeTax map {";\
     print "    x => AnswerRow(\"yourStatutoryPayBeforeTax.checkYourAnswersLabel\", s\"yourStatutoryPayBeforeTax.$x\", true, routes.YourStatutoryPayBeforeTaxController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourStatutoryPayBeforeTax complete"
