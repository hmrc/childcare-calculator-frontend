#!/bin/bash

echo "Applying migration YourStatutoryPayAmountCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourStatutoryPayAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayAmountCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourStatutoryPayAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayAmountCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourStatutoryPayAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayAmountCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourStatutoryPayAmountCY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayAmountCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourStatutoryPayAmountCY.title = yourStatutoryPayAmountCY" >> ../conf/messages.en
echo "yourStatutoryPayAmountCY.heading = yourStatutoryPayAmountCY" >> ../conf/messages.en
echo "yourStatutoryPayAmountCY.checkYourAnswersLabel = yourStatutoryPayAmountCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourStatutoryPayAmountCY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YourStatutoryPayAmountCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourStatutoryPayAmountCY: Option[AnswerRow] = userAnswers.yourStatutoryPayAmountCY map {";\
     print "    x => AnswerRow(\"yourStatutoryPayAmountCY.checkYourAnswersLabel\", s\"$x\", false, routes.YourStatutoryPayAmountCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourStatutoryPayAmountCY complete"
