#!/bin/bash

echo "Applying migration StatutoryPayAmountPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /statutoryPayAmountPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.StatutoryPayAmountPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /statutoryPayAmountPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.StatutoryPayAmountPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeStatutoryPayAmountPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.StatutoryPayAmountPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeStatutoryPayAmountPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.StatutoryPayAmountPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "statutoryPayAmountPY.title = statutoryPayAmountPY" >> ../conf/messages.en
echo "statutoryPayAmountPY.heading = statutoryPayAmountPY" >> ../conf/messages.en
echo "statutoryPayAmountPY.field1 = Field 1" >> ../conf/messages.en
echo "statutoryPayAmountPY.field2 = Field 2" >> ../conf/messages.en
echo "statutoryPayAmountPY.checkYourAnswersLabel = statutoryPayAmountPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def statutoryPayAmountPY: Option[StatutoryPayAmountPY] = cacheMap.getEntry[StatutoryPayAmountPY](StatutoryPayAmountPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def statutoryPayAmountPY: Option[AnswerRow] = userAnswers.statutoryPayAmountPY map {";\
     print "    x => AnswerRow(\"statutoryPayAmountPY.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.StatutoryPayAmountPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration StatutoryPayAmountPY complete"
