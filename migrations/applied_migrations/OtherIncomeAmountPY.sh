#!/bin/bash

echo "Applying migration OtherIncomeAmountPY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /otherIncomeAmountPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.OtherIncomeAmountPYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /otherIncomeAmountPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.OtherIncomeAmountPYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeOtherIncomeAmountPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.OtherIncomeAmountPYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeOtherIncomeAmountPY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.OtherIncomeAmountPYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "otherIncomeAmountPY.title = otherIncomeAmountPY" >> ../conf/messages.en
echo "otherIncomeAmountPY.heading = otherIncomeAmountPY" >> ../conf/messages.en
echo "otherIncomeAmountPY.field1 = Field 1" >> ../conf/messages.en
echo "otherIncomeAmountPY.field2 = Field 2" >> ../conf/messages.en
echo "otherIncomeAmountPY.checkYourAnswersLabel = otherIncomeAmountPY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def otherIncomeAmountPY: Option[OtherIncomeAmountPY] = cacheMap.getEntry[OtherIncomeAmountPY](OtherIncomeAmountPYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def otherIncomeAmountPY: Option[AnswerRow] = userAnswers.otherIncomeAmountPY map {";\
     print "    x => AnswerRow(\"otherIncomeAmountPY.checkYourAnswersLabel\", s\"${x.field1} ${x.field2}\", false, routes.OtherIncomeAmountPYController.onPageLoad(CheckMode).url)";\
     print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration OtherIncomeAmountPY complete"
