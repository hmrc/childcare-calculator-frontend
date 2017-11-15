#!/bin/bash

echo "Applying migration YourStatutoryPayPerWeek"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /yourStatutoryPayPerWeek               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayPerWeekController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /yourStatutoryPayPerWeek               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayPerWeekController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeYourStatutoryPayPerWeek               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayPerWeekController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeYourStatutoryPayPerWeek               uk.gov.hmrc.childcarecalculatorfrontend.controllers.YourStatutoryPayPerWeekController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "yourStatutoryPayPerWeek.title = yourStatutoryPayPerWeek" >> ../conf/messages.en
echo "yourStatutoryPayPerWeek.heading = yourStatutoryPayPerWeek" >> ../conf/messages.en
echo "yourStatutoryPayPerWeek.checkYourAnswersLabel = yourStatutoryPayPerWeek" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def yourStatutoryPayPerWeek: Option[Int] = cacheMap.getEntry[Int](YourStatutoryPayPerWeekId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def yourStatutoryPayPerWeek: Option[AnswerRow] = userAnswers.yourStatutoryPayPerWeek map {";\
     print "    x => AnswerRow(\"yourStatutoryPayPerWeek.checkYourAnswersLabel\", s\"$x\", false, routes.YourStatutoryPayPerWeekController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration YourStatutoryPayPerWeek complete"
