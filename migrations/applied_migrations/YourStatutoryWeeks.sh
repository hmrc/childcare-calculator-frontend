#!/bin/bash

echo "Applying migration YourStatutoryWeeks"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourStatutoryWeeks               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryWeeksController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourStatutoryWeeks               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryWeeksController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourStatutoryWeeks               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryWeeksController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourStatutoryWeeks               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryWeeksController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourStatutoryWeeks.title = yourStatutoryWeeks" >> ../conf/messages.en
echo "yourStatutoryWeeks.heading = yourStatutoryWeeks" >> ../conf/messages.en
echo "yourStatutoryWeeks.checkYourAnswersLabel = yourStatutoryWeeks" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourStatutoryWeeks: Option[Int] = cacheMap.getEntry[Int](YourStatutoryWeeksId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourStatutoryWeeks: Option[AnswerRow] = userAnswers.yourStatutoryWeeks map {";\
     print "    x => AnswerRow(\"yourStatutoryWeeks.checkYourAnswersLabel\", s\"$x\", false, routes.YourStatutoryWeeksController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourStatutoryWeeks complete"
