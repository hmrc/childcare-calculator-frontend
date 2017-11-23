#!/bin/bash

echo "Applying migration OtherIncomeAmountCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /otherIncomeAmountCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.OtherIncomeAmountCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /otherIncomeAmountCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.OtherIncomeAmountCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeOtherIncomeAmountCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.OtherIncomeAmountCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeOtherIncomeAmountCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.OtherIncomeAmountCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "otherIncomeAmountCY.title = otherIncomeAmountCY" >> ../conf/messages.en
echo "otherIncomeAmountCY.heading = otherIncomeAmountCY" >> ../conf/messages.en
echo "otherIncomeAmountCY.field1 = Field 1" >> ../conf/messages.en
echo "otherIncomeAmountCY.field2 = Field 2" >> ../conf/messages.en
echo "otherIncomeAmountCY.checkYourAnswersLabel = otherIncomeAmountCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def otherIncomeAmountCY: Option[OtherIncomeAmountCY] = cacheMap.getEntry[OtherIncomeAmountCY](OtherIncomeAmountCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def otherIncomeAmountCY: Option[AnswerRow] = userAnswers.otherIncomeAmountCY map {";\
     print "    x => AnswerRow(\"otherIncomeAmountCY.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.OtherIncomeAmountCYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration OtherIncomeAmountCY complete"
