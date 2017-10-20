#!/bin/bash

echo "Applying migration YourStatutoryPayAmountPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourStatutoryPayAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayAmountPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourStatutoryPayAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayAmountPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourStatutoryPayAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayAmountPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourStatutoryPayAmountPY               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayAmountPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourStatutoryPayAmountPY.title = yourStatutoryPayAmountPY" >> ../conf/messages.en
echo "yourStatutoryPayAmountPY.heading = yourStatutoryPayAmountPY" >> ../conf/messages.en
echo "yourStatutoryPayAmountPY.checkYourAnswersLabel = yourStatutoryPayAmountPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourStatutoryPayAmountPY: Option[BigDecimal] = cacheMap.getEntry[BigDecimal](YourStatutoryPayAmountPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourStatutoryPayAmountPY: Option[AnswerRow] = userAnswers.yourStatutoryPayAmountPY map {";\
     print "    x => AnswerRow(\"yourStatutoryPayAmountPY.checkYourAnswersLabel\", s\"$x\", false, routes.YourStatutoryPayAmountPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourStatutoryPayAmountPY complete"
