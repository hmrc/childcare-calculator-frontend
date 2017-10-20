#!/bin/bash

echo "Applying migration StatutoryPayAmountCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /statutoryPayAmountCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.StatutoryPayAmountCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /statutoryPayAmountCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.StatutoryPayAmountCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeStatutoryPayAmountCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.StatutoryPayAmountCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeStatutoryPayAmountCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.StatutoryPayAmountCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "statutoryPayAmountCY.title = statutoryPayAmountCY" >> ../conf/messages.en
echo "statutoryPayAmountCY.heading = statutoryPayAmountCY" >> ../conf/messages.en
echo "statutoryPayAmountCY.field1 = Field 1" >> ../conf/messages.en
echo "statutoryPayAmountCY.field2 = Field 2" >> ../conf/messages.en
echo "statutoryPayAmountCY.checkYourAnswersLabel = statutoryPayAmountCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def statutoryPayAmountCY: Option[StatutoryPayAmountCY] = cacheMap.getEntry[StatutoryPayAmountCY](StatutoryPayAmountCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def statutoryPayAmountCY: Option[AnswerRow] = userAnswers.statutoryPayAmountCY map {";\
     print "    x => AnswerRow(\"statutoryPayAmountCY.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.StatutoryPayAmountCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration StatutoryPayAmountCY complete"
