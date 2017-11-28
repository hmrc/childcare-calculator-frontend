#!/bin/bash

echo "Applying migration YourStatutoryStartDate"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourStatutoryStartDate               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryStartDateController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourStatutoryStartDate               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryStartDateController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourStatutoryStartDate               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryStartDateController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourStatutoryStartDate               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryStartDateController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourStatutoryStartDate.title = yourStatutoryStartDate" >> ../conf/messages.en
echo "yourStatutoryStartDate.heading = yourStatutoryStartDate" >> ../conf/messages.en
echo "yourStatutoryStartDate.checkYourAnswersLabel = yourStatutoryStartDate" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourStatutoryStartDate: Option[Int] = cacheMap.getEntry[Int](YourStatutoryStartDateId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourStatutoryStartDate: Option[AnswerRow] = userAnswers.yourStatutoryStartDate map {";\
     print "    x => AnswerRow(\"yourStatutoryStartDate.checkYourAnswersLabel\", s\"$x\", false, routes.YourStatutoryStartDateController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourStatutoryStartDate complete"
